package com.sethy.easypay.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sethy.easypay.data.model.User
import com.sethy.easypay.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _loginState = MutableStateFlow(LoginUiState())
    val loginState: StateFlow<LoginUiState> = _loginState.asStateFlow()

    private val _signupState = MutableStateFlow(SignupUiState())
    val signupState: StateFlow<SignupUiState> = _signupState.asStateFlow()

    fun updateLoginEmail(email: String) {
        _loginState.value = _loginState.value.copy(email = email, emailError = null)
    }

    fun updateLoginPassword(password: String) {
        _loginState.value = _loginState.value.copy(password = password, passwordError = null)
    }

    fun updateSignupName(name: String) {
        _signupState.value = _signupState.value.copy(name = name, nameError = null)
    }

    fun updateSignupEmail(email: String) {
        _signupState.value = _signupState.value.copy(email = email, emailError = null)
    }

    fun updateSignupPhone(phone: String) {
        _signupState.value = _signupState.value.copy(phone = phone, phoneError = null)
    }

    fun updateSignupPassword(password: String) {
        _signupState.value = _signupState.value.copy(
            password = password,
            passwordError = null,
            confirmPasswordError = if (_signupState.value.confirmPassword.isNotEmpty() &&
                password != _signupState.value.confirmPassword) {
                "Passwords do not match"
            } else null
        )
    }

    fun updateSignupConfirmPassword(confirmPassword: String) {
        _signupState.value = _signupState.value.copy(
            confirmPassword = confirmPassword,
            confirmPasswordError = if (confirmPassword != _signupState.value.password) {
                "Passwords do not match"
            } else null
        )
    }

    fun login(onSuccess: (User) -> Unit) {
        val state = _loginState.value
        var hasError = false

        if (state.email.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(state.email).matches()) {
            _loginState.value = state.copy(emailError = "Enter a valid email")
            hasError = true
        }

        if (state.password.length < 8) {
            _loginState.value = state.copy(passwordError = "Password must be at least 8 characters")
            hasError = true
        }

        if (hasError) return

        viewModelScope.launch {
            _loginState.value = state.copy(isLoading = true)
            authRepository.login(state.email, state.password)
                .onSuccess { user ->
                    _loginState.value = LoginUiState()
                    onSuccess(user)
                }
                .onFailure { e ->
                    _loginState.value = state.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Login failed"
                    )
                }
        }
    }

    fun signup(onSuccess: (User) -> Unit) {
        val state = _signupState.value
        var hasError = false

        if (state.name.isBlank()) {
            _signupState.value = state.copy(nameError = "Name is required")
            hasError = true
        }

        if (state.email.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(state.email).matches()) {
            _signupState.value = state.copy(emailError = "Enter a valid email")
            hasError = true
        }

        if (state.phone.isBlank() || !state.phone.matches(Regex("^\\+?[1-9]\\d{1,14}$"))) {
            _signupState.value = state.copy(phoneError = "Enter a valid phone number (e.g., +1234567890)")
            hasError = true
        }

        if (state.password.length < 8) {
            _signupState.value = state.copy(passwordError = "Password must be at least 8 characters")
            hasError = true
        }

        if (state.password != state.confirmPassword) {
            _signupState.value = state.copy(confirmPasswordError = "Passwords do not match")
            hasError = true
        }

        if (hasError) return

        viewModelScope.launch {
            _signupState.value = state.copy(isLoading = true)
            authRepository.register(state.name, state.email, state.phone, state.password)
                .onSuccess { user ->
                    _signupState.value = SignupUiState()
                    onSuccess(user)
                }
                .onFailure { e ->
                    _signupState.value = state.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Signup failed"
                    )
                }
        }
    }

    fun checkAuthState(
        onAuthenticated: (User) -> Unit,
        onUnauthenticated: () -> Unit
    ) {
        viewModelScope.launch {
            if (authRepository.isLoggedIn()) {
                // TODO: Fetch user details from backend
                onUnauthenticated()
            } else {
                onUnauthenticated()
            }
        }
    }
}

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val emailError: String? = null,
    val passwordError: String? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

data class SignupUiState(
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val nameError: String? = null,
    val emailError: String? = null,
    val phoneError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
