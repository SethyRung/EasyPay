package com.sethy.easypay.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sethy.easypay.domain.usecase.GetCurrentUserUseCase
import com.sethy.easypay.domain.usecase.LogoutUseCase
import com.sethy.easypay.ui.state.ProfileEffect
import com.sethy.easypay.ui.state.ProfileEvent
import com.sethy.easypay.ui.state.ProfileUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getCurrentUser: GetCurrentUserUseCase,
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileUiState())
    val state: StateFlow<ProfileUiState> = _state.asStateFlow()

    private val _effect = Channel<ProfileEffect>(Channel.BUFFERED)
    val effect: Flow<ProfileEffect> = _effect.receiveAsFlow()

    init {
        load()
    }

    fun onEvent(event: ProfileEvent) {
        when (event) {
            ProfileEvent.Load -> load()
            ProfileEvent.EditProfile -> viewModelScope.launch {
                _effect.send(ProfileEffect.ShowError("Edit profile not yet implemented"))
            }
            ProfileEvent.LogoutClicked -> {
                _state.value = _state.value.copy(showLogoutDialog = true)
            }
            ProfileEvent.DismissLogout -> {
                _state.value = _state.value.copy(showLogoutDialog = false)
            }
            ProfileEvent.ConfirmLogout -> confirmLogout()
            ProfileEvent.Back -> viewModelScope.launch {
                _effect.send(ProfileEffect.NavigateBack)
            }
            ProfileEvent.DismissError -> {
                _state.value = _state.value.copy(errorMessage = null)
            }
        }
    }

    private fun load() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, errorMessage = null)
            getCurrentUser()
                .onSuccess { user ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        user = user
                    )
                }
                .onFailure { error ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Failed to load profile"
                    )
                }
        }
    }

    private fun confirmLogout() {
        viewModelScope.launch {
            val result = logoutUseCase()
            if (result.isFailure) {
                val message = result.exceptionOrNull()?.message
                    ?: "Server logout failed. You are logged out locally."
                _state.value = _state.value.copy(errorMessage = message)
                kotlinx.coroutines.delay(LOGOUT_FAILURE_DISPLAY_MS)
            }
            _state.value = ProfileUiState()
            _effect.send(ProfileEffect.NavigateToLogin)
        }
    }

    private companion object {
        const val LOGOUT_FAILURE_DISPLAY_MS = 2000L
    }
}