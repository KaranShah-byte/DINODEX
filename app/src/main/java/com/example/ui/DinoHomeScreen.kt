package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.DinoCard
import com.example.ui.components.DinoCompareDialog
import com.example.ui.components.DinoDetailSheet
import com.example.ui.components.DinoFilters
import com.example.ui.components.HeroBanner

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DinoHomeScreen(
    viewModel: DinoViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedEra by viewModel.selectedEra.collectAsStateWithLifecycle()
    val selectedDiet by viewModel.selectedDiet.collectAsStateWithLifecycle()
    val selectedSort by viewModel.selectedSort.collectAsStateWithLifecycle()
    val favoriteIds by viewModel.favoriteDinoIds.collectAsStateWithLifecycle()
    val favoritesOnly by viewModel.favoritesOnly.collectAsStateWithLifecycle()
    val dinosaurs by viewModel.filteredDinosaurs.collectAsStateWithLifecycle()
    val selectedDinoForDetail by viewModel.selectedDinoForDetail.collectAsStateWithLifecycle()
    val compareDinos by viewModel.compareDinos.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.testTag("app_title_row")
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.size(34.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.ViewInAr,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "DinoSphere 3D",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                actions = {
                    // Compare Dinosaurs
                    FilledTonalIconButton(
                        onClick = { viewModel.openCompare() },
                        colors = IconButtonDefaults.filledTonalIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f)
                        ),
                        modifier = Modifier
                            .padding(end = 4.dp)
                            .testTag("button_top_compare")
                    ) {
                        Icon(
                            imageVector = Icons.Default.CompareArrows,
                            contentDescription = "Compare Dinosaurs"
                        )
                    }

                    // Favorites Only Toggle
                    FilledTonalIconButton(
                        onClick = { viewModel.toggleFavoritesOnly() },
                        colors = IconButtonDefaults.filledTonalIconButtonColors(
                            containerColor = if (favoritesOnly) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f),
                            contentColor = if (favoritesOnly) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                        ),
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .testTag("button_top_favorites")
                    ) {
                        Icon(
                            imageVector = if (favoritesOnly) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Show Favorites"
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Hero Banner
                item {
                    HeroBanner(
                        featuredDino = viewModel.featuredDino,
                        onExploreFeatured = {
                            viewModel.selectDinosaurForDetail(viewModel.featuredDino)
                        },
                        onOpenCompare = {
                            viewModel.openCompare()
                        }
                    )
                }

                // Search and Filters Section
                item {
                    DinoFilters(
                        searchQuery = searchQuery,
                        onSearchQueryChange = { viewModel.onSearchQueryChange(it) },
                        selectedEra = selectedEra,
                        onEraSelected = { viewModel.onEraSelected(it) },
                        selectedDiet = selectedDiet,
                        onDietSelected = { viewModel.onDietSelected(it) },
                        selectedSort = selectedSort,
                        onSortSelected = { viewModel.onSortSelected(it) },
                        matchingCount = dinosaurs.size,
                        onResetFilters = { viewModel.resetFilters() }
                    )
                }

                // Empty State if no dinosaurs match
                if (dinosaurs.isEmpty()) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(40.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.SearchOff,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(56.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "No dinosaurs found",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Try adjusting your search query, era, or diet filters.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { viewModel.resetFilters() },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.testTag("button_empty_reset")
                            ) {
                                Text("Reset All Filters")
                            }
                        }
                    }
                } else {
                    // Dinosaur Cards List
                    items(dinosaurs, key = { it.id }) { dino ->
                        Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                            DinoCard(
                                dinosaur = dino,
                                isFavorite = favoriteIds.contains(dino.id),
                                onToggleFavorite = { viewModel.toggleFavorite(dino.id) },
                                onClick = { viewModel.selectDinosaurForDetail(dino) }
                            )
                        }
                    }
                }
            }

            // Detail Sheet (Fullscreen/Overlay)
            selectedDinoForDetail?.let { detailDino ->
                DinoDetailSheet(
                    dinosaur = detailDino,
                    isFavorite = favoriteIds.contains(detailDino.id),
                    onToggleFavorite = { viewModel.toggleFavorite(detailDino.id) },
                    onClose = { viewModel.selectDinosaurForDetail(null) }
                )
            }

            // Compare Dialog
            compareDinos?.let { pair ->
                DinoCompareDialog(
                    initialDinoA = pair.first,
                    initialDinoB = pair.second,
                    onDismiss = { viewModel.closeCompare() }
                )
            }
        }
    }
}
