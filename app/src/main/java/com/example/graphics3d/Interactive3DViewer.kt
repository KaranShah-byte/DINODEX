package com.example.graphics3d

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Accessibility
import androidx.compose.material.icons.filled.CropSquare
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.model.Hotspot3D
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun Interactive3DViewer(
    modelType: String,
    modifier: Modifier = Modifier,
    hotspots: List<Hotspot3D> = emptyList(),
    selectedHotspot: Hotspot3D? = null,
    onHotspotSelected: ((Hotspot3D?) -> Unit)? = null,
    showControls: Boolean = true,
    initialAutoRotate: Boolean = false,
    onExpandFullscreen: (() -> Unit)? = null
) {
    // Camera state
    var yaw by remember { mutableFloatStateOf(0.6f) }
    var pitch by remember { mutableFloatStateOf(0.18f) }
    var zoom by remember { mutableFloatStateOf(1.0f) }

    var isAutoRotating by remember { mutableStateOf(initialAutoRotate) }
    var isWireframe by remember { mutableStateOf(false) }
    var showHumanScale by remember { mutableStateOf(false) }
    var lightAngle by remember { mutableFloatStateOf(0.8f) }

    // Auto-rotation loop
    LaunchedEffect(isAutoRotating) {
        if (isAutoRotating) {
            while (true) {
                withFrameMillis {
                    yaw = (yaw + 0.008f) % (2f * PI.toFloat())
                }
            }
        }
    }

    // Load 3D model geometry
    val model = remember(modelType) { DinoMeshGenerator.getModel(modelType) }
    val humanTriangles = remember { DinoMeshGenerator.createHumanFigure() }

    // Pre-allocate hotspot 2D screen projections for hit-testing
    val projectedHotspots = remember { mutableListOf<Pair<Hotspot3D, Offset>>() }

    Box(
        modifier = modifier
            .background(Color(0xFF0F1115))
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, gestureZoom, _ ->
                    // Touch rotation
                    yaw += pan.x * 0.009f
                    pitch = (pitch - pan.y * 0.007f).coerceIn(-1.2f, 1.2f)
                    // Zoom
                    zoom = (zoom * gestureZoom).coerceIn(0.45f, 2.8f)
                    // Pause auto rotation if user touches
                    if (isAutoRotating) isAutoRotating = false
                }
            }
            .pointerInput(Unit) {
                detectTapGestures { tapOffset ->
                    if (onHotspotSelected != null && projectedHotspots.isNotEmpty()) {
                        // Find closest hotspot within 40dp radius
                        val hit = projectedHotspots.firstOrNull { (_, screenPos) ->
                            val distSq = (tapOffset.x - screenPos.x) * (tapOffset.x - screenPos.x) +
                                    (tapOffset.y - screenPos.y) * (tapOffset.y - screenPos.y)
                            distSq < 1600f // 40px radius
                        }
                        onHotspotSelected(hit?.first)
                    }
                }
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            projectedHotspots.clear()

            val canvasW = size.width
            val canvasH = size.height
            val centerX = canvasW / 2f
            val centerY = canvasH / 2f

            val effectiveScale = (canvasW.coerceAtMost(canvasH) / 3.4f) * model.baseScale * zoom

            // Ground grid pedestal
            drawGroundPedestal(centerX, centerY, effectiveScale, yaw, pitch)

            // Directional lighting
            val lightDir = Vector3D(
                cos(lightAngle) * 1.5f,
                2.0f,
                sin(lightAngle) * 1.5f
            ).normalized()

            // Combine model triangles + optional human figure
            val allTriangles = if (showHumanScale) {
                model.triangles + humanTriangles
            } else {
                model.triangles
            }

            // Transform vertices and compute depth
            val transformedList = ArrayList<TransformedTriangle>(allTriangles.size)

            for (tri in allTriangles) {
                // Apply rotation
                val v1Rot = tri.v1.rotateY(yaw).rotateX(pitch)
                val v2Rot = tri.v2.rotateY(yaw).rotateX(pitch)
                val v3Rot = tri.v3.rotateY(yaw).rotateX(pitch)

                // Face normal
                val normal = Triangle3D.computeNormal(v1Rot, v2Rot, v3Rot)

                // Backface culling: only skip if normal faces away (unless wireframe)
                if (!isWireframe && normal.z <= -0.05f) {
                    continue
                }

                // Perspective projection with slight FOV depth
                val fov = 8.0f
                val p1Scale = effectiveScale * (fov / (fov - v1Rot.z * 0.15f))
                val p2Scale = effectiveScale * (fov / (fov - v2Rot.z * 0.15f))
                val p3Scale = effectiveScale * (fov / (fov - v3Rot.z * 0.15f))

                val p1X = centerX + v1Rot.x * p1Scale
                val p1Y = centerY - v1Rot.y * p1Scale
                val p2X = centerX + v2Rot.x * p2Scale
                val p2Y = centerY - v2Rot.y * p2Scale
                val p3X = centerX + v3Rot.x * p3Scale
                val p3Y = centerY - v3Rot.y * p3Scale

                val avgZ = (v1Rot.z + v2Rot.z + v3Rot.z) / 3f

                // Diffuse + Ambient lighting
                val diffuse = (normal.dot(lightDir)).coerceAtLeast(0f)
                val lightIntensity = 0.35f + 0.65f * diffuse

                transformedList.add(
                    TransformedTriangle(
                        p1X = p1X, p1Y = p1Y, p1Z = v1Rot.z,
                        p2X = p2X, p2Y = p2Y, p2Z = v2Rot.z,
                        p3X = p3X, p3Y = p3Y, p3Z = v3Rot.z,
                        avgZ = avgZ,
                        color = tri.color,
                        normalZ = normal.z,
                        lightIntensity = lightIntensity,
                        partName = tri.partName
                    )
                )
            }

            // Painter's algorithm: sort back-to-front
            transformedList.sortBy { it.avgZ }

            // Render polygons
            val path = Path()
            val wireframeColor = Color(0xFFE5A93C)

            for (t in transformedList) {
                path.reset()
                path.moveTo(t.p1X, t.p1Y)
                path.lineTo(t.p2X, t.p2Y)
                path.lineTo(t.p3X, t.p3Y)
                path.close()

                if (isWireframe) {
                    drawPath(
                        path = path,
                        color = wireframeColor.copy(alpha = 0.75f),
                        style = Stroke(width = 1.2f)
                    )
                } else {
                    // Shaded color modulated by light intensity
                    val baseCol = t.color
                    val shadedColor = Color(
                        red = (baseCol.red * t.lightIntensity).coerceIn(0f, 1f),
                        green = (baseCol.green * t.lightIntensity).coerceIn(0f, 1f),
                        blue = (baseCol.blue * t.lightIntensity).coerceIn(0f, 1f),
                        alpha = 1.0f
                    )
                    drawPath(path = path, color = shadedColor)

                    // Subtle edge definition
                    drawPath(
                        path = path,
                        color = shadedColor.copy(alpha = 0.3f),
                        style = Stroke(width = 0.8f)
                    )
                }
            }

            // Project Hotspots onto screen
            for (hotspot in hotspots) {
                val rot = hotspot.position.rotateY(yaw).rotateX(pitch)
                // Only render if in front half or slightly behind
                if (rot.z > -0.8f) {
                    val hScale = effectiveScale * (8.0f / (8.0f - rot.z * 0.15f))
                    val hX = centerX + rot.x * hScale
                    val hY = centerY - rot.y * hScale

                    val isSelected = selectedHotspot?.id == hotspot.id
                    projectedHotspots.add(hotspot to Offset(hX, hY))

                    // Draw hotspot marker
                    val pinColor = if (isSelected) Color(0xFFFF5252) else Color(0xFFFFB300)
                    // Outer glow
                    drawCircle(
                        color = pinColor.copy(alpha = if (isSelected) 0.5f else 0.25f),
                        radius = if (isSelected) 18.dp.toPx() else 12.dp.toPx(),
                        center = Offset(hX, hY)
                    )
                    // Inner ring
                    drawCircle(
                        color = pinColor,
                        radius = 6.dp.toPx(),
                        center = Offset(hX, hY),
                        style = Stroke(width = 2.dp.toPx())
                    )
                    // Center core
                    drawCircle(
                        color = Color.White,
                        radius = 3.dp.toPx(),
                        center = Offset(hX, hY)
                    )
                }
            }
        }

        // Overlay 3D Controls
        if (showControls) {
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 12.dp),
                shape = RoundedCornerShape(24.dp),
                color = Color(0xCC1A1E24),
                shadowElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Auto-spin
                    FilledTonalIconButton(
                        onClick = { isAutoRotating = !isAutoRotating },
                        colors = IconButtonDefaults.filledTonalIconButtonColors(
                            containerColor = if (isAutoRotating) MaterialTheme.colorScheme.primary else Color(0x33FFFFFF),
                            contentColor = if (isAutoRotating) MaterialTheme.colorScheme.onPrimary else Color.White
                        ),
                        modifier = Modifier
                            .size(38.dp)
                            .testTag("button_toggle_spin")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Sync,
                            contentDescription = "Auto Rotate",
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Wireframe / Shaded
                    FilledTonalIconButton(
                        onClick = { isWireframe = !isWireframe },
                        colors = IconButtonDefaults.filledTonalIconButtonColors(
                            containerColor = if (isWireframe) MaterialTheme.colorScheme.primary else Color(0x33FFFFFF),
                            contentColor = if (isWireframe) MaterialTheme.colorScheme.onPrimary else Color.White
                        ),
                        modifier = Modifier
                            .size(38.dp)
                            .testTag("button_toggle_wireframe")
                    ) {
                        Icon(
                            imageVector = if (isWireframe) Icons.Default.CropSquare else Icons.Default.ViewInAr,
                            contentDescription = "Wireframe Mode",
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Human Scale
                    FilledTonalIconButton(
                        onClick = { showHumanScale = !showHumanScale },
                        colors = IconButtonDefaults.filledTonalIconButtonColors(
                            containerColor = if (showHumanScale) Color(0xFF00B0FF) else Color(0x33FFFFFF),
                            contentColor = if (showHumanScale) Color.Black else Color.White
                        ),
                        modifier = Modifier
                            .size(38.dp)
                            .testTag("button_toggle_human_scale")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Accessibility,
                            contentDescription = "Human Scale Comparison",
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Lighting angle shift
                    FilledTonalIconButton(
                        onClick = { lightAngle = (lightAngle + 1.2f) % (2f * PI.toFloat()) },
                        colors = IconButtonDefaults.filledTonalIconButtonColors(
                            containerColor = Color(0x33FFFFFF),
                            contentColor = Color.White
                        ),
                        modifier = Modifier
                            .size(38.dp)
                            .testTag("button_cycle_light")
                    ) {
                        Icon(
                            imageVector = Icons.Default.LightMode,
                            contentDescription = "Shift Lighting",
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Reset Camera
                    FilledTonalIconButton(
                        onClick = {
                            yaw = 0.6f
                            pitch = 0.18f
                            zoom = 1.0f
                            isAutoRotating = false
                        },
                        colors = IconButtonDefaults.filledTonalIconButtonColors(
                            containerColor = Color(0x33FFFFFF),
                            contentColor = Color.White
                        ),
                        modifier = Modifier
                            .size(38.dp)
                            .testTag("button_reset_camera")
                    ) {
                        Icon(
                            imageVector = Icons.Default.RestartAlt,
                            contentDescription = "Reset Camera View",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Top hint badge
            Surface(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 12.dp, top = 12.dp),
                shape = RoundedCornerShape(12.dp),
                color = Color(0x88000000)
            ) {
                Text(
                    text = "Drag to Rotate • Pinch to Zoom",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFFE0E0E0),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}

// Draw pedestal rings on ground plane (y = -1.2f)
private fun DrawScope.drawGroundPedestal(
    centerX: Float,
    centerY: Float,
    scale: Float,
    yaw: Float,
    pitch: Float
) {
    val floorY = -1.25f
    val radii = listOf(0.8f, 1.4f, 2.0f)

    for (r in radii) {
        val path = Path()
        val segments = 24
        for (i in 0..segments) {
            val angle = (i.toFloat() / segments) * 2f * PI.toFloat()
            val pt = Vector3D(cos(angle) * r, floorY, sin(angle) * r)
                .rotateY(yaw)
                .rotateX(pitch)

            val s = scale * (8.0f / (8.0f - pt.z * 0.15f))
            val x = centerX + pt.x * s
            val y = centerY - pt.y * s

            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        drawPath(
            path = path,
            color = Color(0x22FFFFFF),
            style = Stroke(width = 1f)
        )
    }
}
