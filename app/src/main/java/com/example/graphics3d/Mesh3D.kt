package com.example.graphics3d

import androidx.compose.ui.graphics.Color

data class Triangle3D(
    val v1: Vector3D,
    val v2: Vector3D,
    val v3: Vector3D,
    val color: Color,
    val normal: Vector3D = computeNormal(v1, v2, v3),
    val partName: String? = null
) {
    companion object {
        fun computeNormal(v1: Vector3D, v2: Vector3D, v3: Vector3D): Vector3D {
            val edge1 = v2 - v1
            val edge2 = v3 - v1
            return edge1.cross(edge2).normalized()
        }
    }
}

data class TransformedTriangle(
    val p1X: Float, val p1Y: Float, val p1Z: Float,
    val p2X: Float, val p2Y: Float, val p2Z: Float,
    val p3X: Float, val p3Y: Float, val p3Z: Float,
    val avgZ: Float,
    val color: Color,
    val normalZ: Float,
    val lightIntensity: Float,
    val partName: String?
)

data class Model3D(
    val triangles: List<Triangle3D>,
    val centerOffset: Vector3D = Vector3D(0f, 0f, 0f),
    val baseScale: Float = 1.0f,
    val name: String = ""
)
