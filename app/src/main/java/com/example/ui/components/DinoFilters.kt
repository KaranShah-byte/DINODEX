package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.model.DinoDiet
import com.example.model.DinoEra

enum class DinoSortOption(val label: String) {
    NAME_ASC("Name (A–Z)"),
    ERA_CHRONO("Era (Triassic → Cretaceous)"),
    LENGTH_DESC("Size (Longest First)"),
    WEIGHT_DESC("Weight (Heaviest First)"),
    SPEED_DESC("Top Speed (Fastest First)")
}

@Composable
fun DinoFilters(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    selectedEra: DinoEra,
    onEraSelected: (DinoEra) -> Unit,
    selectedDiet: DinoDiet,
    onDietSelected: (DinoDiet) -> Unit,
    selectedSort: DinoSortOption,
    onSortSelected: (DinoSortOption) -> Unit,
    matchingCount: Int,
    onResetFilters: () -> Unit,
    modifier: Modifier = Modifier
) {
    var sortMenuExpanded by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxWidth()) {
        // Search bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .testTag("search_input_field"),
            placeholder = { Text("Search dinosaurs, fossils, traits...") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(
                        onClick = { onSearchQueryChange("") },
                        modifier = Modifier.testTag("button_clear_search")
                    ) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear search")
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
            )
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Era Filter Chips Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Era:",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            DinoEra.values().forEach { era ->
                val isSelected = selectedEra == era
                FilterChip(
                    selected = isSelected,
                    onClick = { onEraSelected(era) },
                    label = { Text(era.title) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = era.color.copy(alpha = 0.25f),
                        selectedLabelColor = era.color
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = isSelected,
                        selectedBorderColor = era.color,
                        borderColor = MaterialTheme.colorScheme.outlineVariant
                    ),
                    modifier = Modifier.testTag("filter_era_${era.name.lowercase()}")
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Diet Filter Chips Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Diet:",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            DinoDiet.values().forEach { diet ->
                val isSelected = selectedDiet == diet
                FilterChip(
                    selected = isSelected,
                    onClick = { onDietSelected(diet) },
                    label = { Text("${diet.emoji} ${diet.title}") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = diet.color.copy(alpha = 0.25f),
                        selectedLabelColor = diet.color
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = isSelected,
                        selectedBorderColor = diet.color,
                        borderColor = MaterialTheme.colorScheme.outlineVariant
                    ),
                    modifier = Modifier.testTag("filter_diet_${diet.name.lowercase()}")
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Status bar & Sort menu
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$matchingCount species found",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Sort dropdown
                Box {
                    AssistChip(
                        onClick = { sortMenuExpanded = true },
                        label = { Text(selectedSort.label) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Sort,
                                contentDescription = "Sort",
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                        ),
                        modifier = Modifier.testTag("button_sort_selector")
                    )

                    DropdownMenu(
                        expanded = sortMenuExpanded,
                        onDismissRequest = { sortMenuExpanded = false }
                    ) {
                        DinoSortOption.values().forEach { sortOpt ->
                            DropdownMenuItem(
                                text = { Text(sortOpt.label) },
                                onClick = {
                                    onSortSelected(sortOpt)
                                    sortMenuExpanded = false
                                }
                            )
                        }
                    }
                }

                val hasActiveFilter = searchQuery.isNotBlank() || selectedEra != DinoEra.ALL || selectedDiet != DinoDiet.ALL
                if (hasActiveFilter) {
                    Spacer(modifier = Modifier.width(8.dp))
                    AssistChip(
                        onClick = onResetFilters,
                        label = { Text("Reset") },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f),
                            labelColor = MaterialTheme.colorScheme.error
                        ),
                        modifier = Modifier.testTag("button_reset_filters")
                    )
                }
            }
        }
    }
}
