package com.sethy.easypay.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sethy.easypay.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppSessionViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _isAuthenticated = MutableStateFlow<Boolean?>(null)
    val isAuthenticated: StateFlow<Boolean?> = _isAuthenticated.asStateFlow()

    init {
        checkAuthState()
    }

    fun checkAuthState() {
        viewModelScope.launch {
            _isAuthenticated.value = authRepository.isLoggedIn()
        }
    }

    fun setAuthenticated(authenticated: Boolean) {
        _isAuthenticated.value = authenticated
    }
}
