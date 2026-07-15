package com.sethy.easypay.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sethy.easypay.data.auth.AuthSessionNotifier
import com.sethy.easypay.data.local.AuthTokenManager
import com.sethy.easypay.data.local.OnboardingPreferences
import com.sethy.easypay.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppSessionViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val tokenManager: AuthTokenManager,
    private val authSessionNotifier: AuthSessionNotifier,
    private val onboardingPreferences: OnboardingPreferences
) : ViewModel() {

    private val _isOnboardingCompleted = MutableStateFlow<Boolean?>(null)
    val isOnboardingCompleted: StateFlow<Boolean?> = _isOnboardingCompleted.asStateFlow()

    private val _isAuthenticated = MutableStateFlow<Boolean?>(null)
    val isAuthenticated: StateFlow<Boolean?> = _isAuthenticated.asStateFlow()

    init {
        checkOnboardingState()
        viewModelScope.launch { refreshSession() }
        observeSessionExpiry()
    }

    private fun checkOnboardingState() {
        viewModelScope.launch {
            _isOnboardingCompleted.value = onboardingPreferences.hasSeenOnboarding()
        }
    }

    private suspend fun refreshSession() {
        val result = authRepository.refreshSession()
        if (result.isSuccess) {
            _isAuthenticated.value = true
        } else {
            onSessionLost()
        }
    }

    private fun observeSessionExpiry() {
        viewModelScope.launch {
            authSessionNotifier.events.collect { onSessionLost() }
        }
    }

    private fun onSessionLost() {
        viewModelScope.launch { tokenManager.clearTokens() }
        _isAuthenticated.value = false
    }

    fun setAuthenticated(authenticated: Boolean) {
        _isAuthenticated.value = authenticated
    }
}
