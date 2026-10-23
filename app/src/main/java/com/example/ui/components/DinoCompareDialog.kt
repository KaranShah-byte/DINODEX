package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.graphics3d.Interactive3DViewer
import com.example.model.Dinosaur
import com.example.model.DinosaurDatabase

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DinoCompareDialog(
    initialDinoA: Dinosaur,
    initialDinoB: Dinosaur,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var dinoA by remember { mutableStateOf(initialDinoA) }
    var dinoB by remember { mutableStateOf(initialDinoB) }

    var menuAExpanded by remember { mutableStateOf(false) }
    var menuBExpanded by remember { mutableStateOf(false) }

    val allDinos = remember { DinosaurDatabase.dinosaurs }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier.testTag("dino_compare_dialog")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 24.dp)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CompareArrows,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Side-by-Side 3D Comparison",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("button_close_compare")
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }

            // Dropdown Pickers for Dinosaur A & B
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Selector A
                Box(modifier = Modifier.weight(1f)) {
                    OutlinedButton(
                        onClick = { menuAExpanded = true },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().testTag("picker_dino_a")
                    ) {
                        Text(
                            text = dinoA.name,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    DropdownMenu(
                        expanded = menuAExpanded,
                        onDismissRequest = { menuAExpanded = false }
                    ) {
                        allDinos.forEach { dino ->
                            DropdownMenuItem(
                                text = { Text(dino.name) },
                                onClick = {
                                    dinoA = dino
                                    menuAExpanded = false
                                }
                            )
                        }
                    }
                }

                // Selector B
                Box(modifier = Modifier.weight(1f)) {
                    OutlinedButton(
                        onClick = { menuBExpanded = true },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().testTag("picker_dino_b")
                    ) {
                        Text(
                            text = dinoB.name,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    DropdownMenu(
                        expanded = menuBExpanded,
                        onDismissRequest = { menuBExpanded = false }
                    ) {
                        allDinos.forEach { dino ->
                            DropdownMenuItem(
                                text = { Text(dino.name) },
                                onClick = {
                                    dinoB = dino
                                    menuBExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 3D Viewers Side by Side
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Model A
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0D0F13))
                ) {
                    Interactive3DViewer(
                        modelType = dinoA.modelType,
                        modifier = Modifier.fillMaxSize(),
                        showControls = false,
                        initialAutoRotate = true
                    )
                }

                // Model B
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0D0F13))
                ) {
                    Interactive3DViewer(
                        modelType = dinoB.modelType,
                        modifier = Modifier.fillMaxSize(),
                        showControls = false,
                        initialAutoRotate = true
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Comparison Metrics Table
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                item {
                    CompareRow(
                        metric = "Era / Period",
                        valA = "${dinoA.era.title}\n(${dinoA.periodDetail})",
                        valB = "${dinoB.era.title}\n(${dinoB.periodDetail})"
                    )
                }
                item {
                    CompareRow(
                        metric = "Diet",
                        valA = "${dinoA.diet.emoji} ${dinoA.diet.title}",
                        valB = "${dinoB.diet.emoji} ${dinoB.diet.title}"
                    )
                }
                item {
                    CompareRow(
                        metric = "Length",
                        valA = "${dinoA.lengthMeters} m",
                        valB = "${dinoB.lengthMeters} m",
                        highlightA = dinoA.lengthMeters > dinoB.lengthMeters,
                        highlightB = dinoB.lengthMeters > dinoA.lengthMeters
                    )
                }
                item {
                    CompareRow(
                        metric = "Height",
                        valA = "${dinoA.heightMeters} m",
                        valB = "${dinoB.heightMeters} m",
                        highlightA = dinoA.heightMeters > dinoB.heightMeters,
                        highlightB = dinoB.heightMeters > dinoA.heightMeters
                    )
                }
                item {
                    val wA = if (dinoA.weightTons < 1f) "${(dinoA.weightTons * 1000).toInt()} kg" else "${dinoA.weightTons} tons"
                    val wB = if (dinoB.weightTons < 1f) "${(dinoB.weightTons * 1000).toInt()} kg" else "${dinoB.weightTons} tons"
                    CompareRow(
                        metric = "Weight",
                        valA = wA,
                        valB = wB,
                        highlightA = dinoA.weightTons > dinoB.weightTons,
                        highlightB = dinoB.weightTons > dinoA.weightTons
                    )
                }
                item {
                    CompareRow(
                        metric = "Top Speed",
                        valA = "${dinoA.topSpeedKmh} km/h",
                        valB = "${dinoB.topSpeedKmh} km/h",
                        highlightA = dinoA.topSpeedKmh > dinoB.topSpeedKmh,
                        highlightB = dinoB.topSpeedKmh > dinoA.topSpeedKmh
                    )
                }
                item {
                    CompareRow(
                        metric = "Primary Fossils",
                        valA = dinoA.discoveryCountry,
                        valB = dinoB.discoveryCountry
                    )
                }
            }
        }
    }
}

@Composable
private fun CompareRow(
    metric: String,
    valA: String,
    valB: String,
    highlightA: Boolean = false,
    highlightB: Boolean = false
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
        )
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(
                text = metric,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = valA,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = if (highlightA) FontWeight.Bold else FontWeight.Normal,
                    color = if (highlightA) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )

                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(20.dp)
                        .background(MaterialTheme.colorScheme.outlineVariant)
                )

                Text(
                    text = valB,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = if (highlightB) FontWeight.Bold else FontWeight.Normal,
                    color = if (highlightB) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
