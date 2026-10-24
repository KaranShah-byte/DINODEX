package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.model.DinoDiet
import com.example.model.DinoEra
import com.example.model.Dinosaur
import com.example.model.DinosaurDatabase
import com.example.ui.components.DinoSortOption
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class DinoFilterState(
    val query: String = "",
    val era: DinoEra = DinoEra.ALL,
    val diet: DinoDiet = DinoDiet.ALL,
    val sort: DinoSortOption = DinoSortOption.NAME_ASC,
    val favoriteIds: Set<String> = setOf("trex", "spinosaurus"),
    val favoritesOnly: Boolean = false
)

class DinoViewModel : ViewModel() {

    private val _filterState = MutableStateFlow(DinoFilterState())

    val searchQuery: StateFlow<String> = _filterState
        .map { it.query }
        .stateIn(viewModelScope, SharingStarted.Eagerly, "")

    val selectedEra: StateFlow<DinoEra> = _filterState
        .map { it.era }
        .stateIn(viewModelScope, SharingStarted.Eagerly, DinoEra.ALL)

    val selectedDiet: StateFlow<DinoDiet> = _filterState
        .map { it.diet }
        .stateIn(viewModelScope, SharingStarted.Eagerly, DinoDiet.ALL)

    val selectedSort: StateFlow<DinoSortOption> = _filterState
        .map { it.sort }
        .stateIn(viewModelScope, SharingStarted.Eagerly, DinoSortOption.NAME_ASC)

    val favoriteDinoIds: StateFlow<Set<String>> = _filterState
        .map { it.favoriteIds }
        .stateIn(viewModelScope, SharingStarted.Eagerly, setOf("trex", "spinosaurus"))

    val favoritesOnly: StateFlow<Boolean> = _filterState
        .map { it.favoritesOnly }
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    private val _selectedDinoForDetail = MutableStateFlow<Dinosaur?>(null)
    val selectedDinoForDetail: StateFlow<Dinosaur?> = _selectedDinoForDetail

    private val _compareDinos = MutableStateFlow<Pair<Dinosaur, Dinosaur>?>(null)
    val compareDinos: StateFlow<Pair<Dinosaur, Dinosaur>?> = _compareDinos

    val featuredDino: Dinosaur = DinosaurDatabase.dinosaurs.first()

    val filteredDinosaurs: StateFlow<List<Dinosaur>> = _filterState
        .map { state ->
            var list = DinosaurDatabase.filter(state.query, state.era, state.diet)
            if (state.favoritesOnly) {
                list = list.filter { state.favoriteIds.contains(it.id) }
            }
            when (state.sort) {
                DinoSortOption.NAME_ASC -> list.sortedBy { it.name }
                DinoSortOption.ERA_CHRONO -> list.sortedWith(compareBy({ it.era.ordinal }, { it.name }))
                DinoSortOption.LENGTH_DESC -> list.sortedByDescending { it.lengthMeters }
                DinoSortOption.WEIGHT_DESC -> list.sortedByDescending { it.weightTons }
                DinoSortOption.SPEED_DESC -> list.sortedByDescending { it.topSpeedKmh }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = DinosaurDatabase.dinosaurs
        )

    fun onSearchQueryChange(newQuery: String) {
        _filterState.value = _filterState.value.copy(query = newQuery)
    }

    fun onEraSelected(era: DinoEra) {
        _filterState.value = _filterState.value.copy(era = era)
    }

    fun onDietSelected(diet: DinoDiet) {
        _filterState.value = _filterState.value.copy(diet = diet)
    }

    fun onSortSelected(sort: DinoSortOption) {
        _filterState.value = _filterState.value.copy(sort = sort)
    }

    fun toggleFavorite(dinoId: String) {
        val current = _filterState.value.favoriteIds.toMutableSet()
        if (current.contains(dinoId)) {
            current.remove(dinoId)
        } else {
            current.add(dinoId)
        }
        _filterState.value = _filterState.value.copy(favoriteIds = current)
    }

    fun setFavorites(ids: Set<String>) {
        _filterState.value = _filterState.value.copy(favoriteIds = ids)
    }

    fun toggleFavoritesOnly() {
        _filterState.value = _filterState.value.copy(favoritesOnly = !_filterState.value.favoritesOnly)
    }

    fun selectDinosaurForDetail(dinosaur: Dinosaur?) {
        _selectedDinoForDetail.value = dinosaur
    }

    fun openCompare(dinoA: Dinosaur? = null, dinoB: Dinosaur? = null) {
        val first = dinoA ?: DinosaurDatabase.getById("trex") ?: DinosaurDatabase.dinosaurs[0]
        val second = dinoB ?: DinosaurDatabase.getById("spinosaurus") ?: DinosaurDatabase.dinosaurs[1]
        _compareDinos.value = Pair(first, second)
    }

    fun closeCompare() {
        _compareDinos.value = null
    }

    fun resetFilters() {
        _filterState.value = _filterState.value.copy(
            query = "",
            era = DinoEra.ALL,
            diet = DinoDiet.ALL,
            favoritesOnly = false
        )
    }
}
