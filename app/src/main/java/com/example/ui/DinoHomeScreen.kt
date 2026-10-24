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
import androidx.compose.material.icons.automirrored.filled.CompareArrows
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.auth.AuthViewModel
import com.example.model.DinosaurDatabase
import kotlinx.coroutines.launch
import com.example.auth.GoogleSignInCard
import com.example.auth.UserProfileDialog
import com.example.auth.UserProfileTopBarButton
import com.example.gemini.viewmodels.ChatViewModel
import com.example.gemini.viewmodels.LiveVoiceViewModel
import com.example.gemini.viewmodels.MapsGroundingViewModel
import com.example.ui.components.DinoCard
import com.example.ui.components.DinoCompareDialog
import com.example.ui.components.DinoDetailSheet
import com.example.ui.components.DinoFilters
import com.example.ui.components.HeroBanner
import com.example.ui.screens.ChatScreen
import com.example.ui.screens.LiveVoiceScreen
import com.example.ui.screens.MapsGroundingScreen

enum class DinoNavTab(val label: String) {
    ENCYCLOPEDIA("3D Models"),
    CHAT("Dr. Rex Chat"),
    VOICE("Live Voice"),
    MAPS("Fossil Sites")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DinoHomeScreen(
    viewModel: DinoViewModel = viewModel(),
    chatViewModel: ChatViewModel = viewModel(),
    liveVoiceViewModel: LiveVoiceViewModel = viewModel(),
    mapsViewModel: MapsGroundingViewModel = viewModel(),
    authViewModel: AuthViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(DinoNavTab.ENCYCLOPEDIA) }
    var showProfileDialog by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()
    val currentUser by authViewModel.currentUser.collectAsStateWithLifecycle()
    val isSigningIn by authViewModel.isLoading.collectAsStateWithLifecycle()

    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedEra by viewModel.selectedEra.collectAsStateWithLifecycle()
    val selectedDiet by viewModel.selectedDiet.collectAsStateWithLifecycle()
    val selectedSort by viewModel.selectedSort.collectAsStateWithLifecycle()
    val favoriteIds by viewModel.favoriteDinoIds.collectAsStateWithLifecycle()
    val favoritesOnly by viewModel.favoritesOnly.collectAsStateWithLifecycle()
    val dinosaurs by viewModel.filteredDinosaurs.collectAsStateWithLifecycle()
    val selectedDinoForDetail by viewModel.selectedDinoForDetail.collectAsStateWithLifecycle()
    val compareDinos by viewModel.compareDinos.collectAsStateWithLifecycle()

    // Sync cloud favorites from Firestore upon sign-in
    LaunchedEffect(currentUser) {
        if (currentUser != null) {
            val cloudFavs = authViewModel.authManager.fetchCloudFavorites()
            if (cloudFavs.isNotEmpty()) {
                viewModel.setFavorites(cloudFavs)
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            if (selectedTab == DinoNavTab.ENCYCLOPEDIA) {
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
                                imageVector = Icons.AutoMirrored.Filled.CompareArrows,
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
                                .padding(end = 6.dp)
                                .testTag("button_top_favorites")
                        ) {
                            Icon(
                                imageVector = if (favoritesOnly) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                contentDescription = "Show Favorites"
                            )
                        }

                        // Google Sign-In / User Profile Avatar
                        UserProfileTopBarButton(
                            user = currentUser,
                            onClick = { showProfileDialog = true },
                            modifier = Modifier.padding(end = 12.dp)
                        )
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background
                    )
                )
            }
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp,
                modifier = Modifier.testTag("bottom_nav_bar")
            ) {
                // 1. Encyclopedia tab
                NavigationBarItem(
                    selected = selectedTab == DinoNavTab.ENCYCLOPEDIA,
                    onClick = { selectedTab = DinoNavTab.ENCYCLOPEDIA },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.ViewInAr,
                            contentDescription = "3D Models"
                        )
                    },
                    label = { Text("3D Models", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                    ),
                    modifier = Modifier.testTag("nav_tab_encyclopedia")
                )

                // 2. Gemini Chat tab
                NavigationBarItem(
                    selected = selectedTab == DinoNavTab.CHAT,
                    onClick = { selectedTab = DinoNavTab.CHAT },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "Dr. Rex Chat"
                        )
                    },
                    label = { Text("Dr. Rex Chat", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                    ),
                    modifier = Modifier.testTag("nav_tab_chat")
                )

                // 3. Live Voice tab (gemini-3.8-live)
                NavigationBarItem(
                    selected = selectedTab == DinoNavTab.VOICE,
                    onClick = { selectedTab = DinoNavTab.VOICE },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.GraphicEq,
                            contentDescription = "Live Voice"
                        )
                    },
                    label = { Text("Live Voice", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF00E676),
                        indicatorColor = Color(0xFF00E676).copy(alpha = 0.2f)
                    ),
                    modifier = Modifier.testTag("nav_tab_voice")
                )

                // 4. Maps Grounding tab (gemini-3.8-flash + googleMaps)
                NavigationBarItem(
                    selected = selectedTab == DinoNavTab.MAPS,
                    onClick = { selectedTab = DinoNavTab.MAPS },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Fossil Sites"
                        )
                    },
                    label = { Text("Fossil Sites", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF4285F4),
                        indicatorColor = Color(0xFF4285F4).copy(alpha = 0.2f)
                    ),
                    modifier = Modifier.testTag("nav_tab_maps")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                DinoNavTab.ENCYCLOPEDIA -> {
                    val handleToggleFavorite: (String) -> Unit = { dinoId ->
                        val wasFav = favoriteIds.contains(dinoId)
                        viewModel.toggleFavorite(dinoId)
                        if (currentUser != null) {
                            val target = DinosaurDatabase.getById(dinoId)
                            coroutineScope.launch {
                                authViewModel.authManager.syncFavoriteToCloud(
                                    dinoId = dinoId,
                                    name = target?.name ?: dinoId,
                                    era = target?.era?.title ?: "",
                                    diet = target?.diet?.title ?: "",
                                    isFavorite = !wasFav
                                )
                            }
                        }
                    }

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Google Sign-In Banner for new / unauthenticated users
                        if (currentUser == null) {
                            item {
                                Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                                    GoogleSignInCard(
                                        onSignInClick = { authViewModel.signInWithGoogle() },
                                        isLoading = isSigningIn
                                    )
                                }
                            }
                        }

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
                                        onToggleFavorite = { handleToggleFavorite(dino.id) },
                                        onClick = { viewModel.selectDinosaurForDetail(dino) }
                                    )
                                }
                            }
                        }
                    }
                }

                DinoNavTab.CHAT -> {
                    ChatScreen(
                        chatViewModel = chatViewModel,
                        onNavigateToVoice = { selectedTab = DinoNavTab.VOICE }
                    )
                }

                DinoNavTab.VOICE -> {
                    LiveVoiceScreen(
                        liveVoiceViewModel = liveVoiceViewModel
                    )
                }

                DinoNavTab.MAPS -> {
                    MapsGroundingScreen(
                        mapsViewModel = mapsViewModel
                    )
                }
            }

            // Detail Sheet (Fullscreen/Overlay)
            selectedDinoForDetail?.let { detailDino ->
                DinoDetailSheet(
                    dinosaur = detailDino,
                    isFavorite = favoriteIds.contains(detailDino.id),
                    onToggleFavorite = { viewModel.toggleFavorite(detailDino.id) },
                    onClose = { viewModel.selectDinosaurForDetail(null) },
                    onAskDrRex = { dino ->
                        viewModel.selectDinosaurForDetail(null)
                        selectedTab = DinoNavTab.CHAT
                        chatViewModel.sendMessage("Tell me all about ${dino.name}'s anatomy, feeding strategies, and prehistoric world.")
                    },
                    onVoiceChat = { dino ->
                        viewModel.selectDinosaurForDetail(null)
                        selectedTab = DinoNavTab.VOICE
                        liveVoiceViewModel.startSession(dino.name)
                        liveVoiceViewModel.sendTextTurn("Tell me about ${dino.name}!")
                    },
                    onFindMuseums = { dino ->
                        viewModel.selectDinosaurForDetail(null)
                        selectedTab = DinoNavTab.MAPS
                        mapsViewModel.search("Famous museums and dig sites exhibiting ${dino.name} fossils")
                    }
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

            // User Profile / Google Sign-In Dialog
            if (showProfileDialog) {
                UserProfileDialog(
                    user = currentUser,
                    isLoading = isSigningIn,
                    onSignIn = {
                        authViewModel.signInWithGoogle(
                            onSuccess = { showProfileDialog = false }
                        )
                    },
                    onSignOut = { authViewModel.signOut() },
                    onDismiss = { showProfileDialog = false },
                    favoritesCount = favoriteIds.size
                )
            }
        }
    }
}
