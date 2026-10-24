package com.example.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    val authManager = AuthManager(application.applicationContext)

    val currentUser: StateFlow<FirebaseUser?> = authManager.authStateFlow()
        .stateIn(viewModelScope, SharingStarted.Eagerly, authManager.currentUser)

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun signInWithGoogle(onSuccess: (() -> Unit)? = null) {
        if (_isLoading.value) return
        _isLoading.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            val result = authManager.signInWithGoogle()
            _isLoading.value = false
            result.fold(
                onSuccess = {
                    _errorMessage.value = null
                    onSuccess?.invoke()
                },
                onFailure = { error ->
                    val msg = error.localizedMessage ?: "Google Sign-In failed"
                    if (!msg.contains("cancellation", ignoreCase = true) && !msg.contains("cancelled", ignoreCase = true)) {
                        _errorMessage.value = msg
                    }
                }
            )
        }
    }

    fun signOut() {
        authManager.signOut()
    }

    fun clearError() {
        _errorMessage.value = null
    }
}
