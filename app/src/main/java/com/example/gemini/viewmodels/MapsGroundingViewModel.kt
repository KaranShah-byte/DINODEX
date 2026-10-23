package com.example.gemini.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gemini.GeminiApiService
import com.example.gemini.GroundingPlace
import com.example.gemini.MapsGroundingResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MapsGroundingViewModel : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query

    private val _currentResult = MutableStateFlow<MapsGroundingResult?>(null)
    val currentResult: StateFlow<MapsGroundingResult?> = _currentResult

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    val presetQueries = listOf(
        "Famous Natural History Museums with T-Rex skeletons",
        "Dinosaur fossil dig sites in Montana & Hell Creek Formation",
        "Morrison Formation dinosaur quarry sites in Utah & Colorado",
        "Top paleontology museums worldwide with dinosaur exhibits",
        "Museums exhibiting Spinosaurus fossils in Europe and USA"
    )

    fun onQueryChange(newQuery: String) {
        _query.value = newQuery
    }

    fun search(searchQuery: String = _query.value, lat: Double? = null, lng: Double? = null) {
        val q = searchQuery.trim()
        if (q.isBlank() || _isLoading.value) return

        _query.value = q
        _isLoading.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            val result = GeminiApiService.queryWithGoogleMaps(
                query = q,
                latitude = lat,
                longitude = lng
            )

            result.fold(
                onSuccess = { res ->
                    _currentResult.value = res
                    _isLoading.value = false
                },
                onFailure = { err ->
                    val msg = err.localizedMessage ?: "Failed to retrieve Maps Grounding data."
                    _errorMessage.value = msg
                    _isLoading.value = false
                }
            )
        }
    }
}
