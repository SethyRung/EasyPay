package com.sethy.easypay.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sethy.easypay.data.model.User
import com.sethy.easypay.data.repository.AuthRepository
import com.sethy.easypay.util.ValidationUtils
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

    // Login field updates
    fun updateLoginEmail(email: String) {
        val error = if (_loginState.value.emailTouched) ValidationUtils.validateEmail(email) else null
        _loginState.value = _loginState.value.copy(email = email, emailError = error, errorMessage = null)
    }

    fun updateLoginPassword(password: String) {
        val error = if (_loginState.value.passwordTouched && password.isNotBlank() && password.length < 8) {
            "Password must be at least 8 characters"
        } else null
        _loginState.value = _loginState.value.copy(password = password, passwordError = error, errorMessage = null)
    }

    fun touchLoginEmail() {
        _loginState.value = _loginState.value.copy(emailTouched = true)
    }

    fun touchLoginPassword() {
        _loginState.value = _loginState.value.copy(passwordTouched = true)
    }

    // Signup field updates with real-time validation
    fun updateSignupName(name: String) {
        val error = if (_signupState.value.nameTouched) ValidationUtils.validateName(name) else null
        _signupState.value = _signupState.value.copy(
            name = name,
            nameError = error,
            errorMessage = null
        )
    }

    fun updateSignupEmail(email: String) {
        val error = if (_signupState.value.emailTouched) ValidationUtils.validateEmail(email) else null
        _signupState.value = _signupState.value.copy(
            email = email,
            emailError = error,
            errorMessage = null
        )
    }

    fun updateSignupPhone(phone: String) {
        val error = if (_signupState.value.phoneTouched) ValidationUtils.validatePhone(phone) else null
        _signupState.value = _signupState.value.copy(
            phone = phone,
            phoneError = error,
            errorMessage = null
        )
    }

    fun updateSignupPassword(password: String) {
        val state = _signupState.value
        val error = if (state.passwordTouched) ValidationUtils.validatePassword(password) else null
        val confirmError = if (state.confirmPasswordTouched && state.confirmPassword.isNotEmpty()) {
            ValidationUtils.validateConfirmPassword(password, state.confirmPassword)
        } else null
        _signupState.value = state.copy(
            password = password,
            passwordError = error,
            confirmPasswordError = confirmError,
            passwordStrength = ValidationUtils.calculatePasswordStrength(password),
            passwordRequirements = ValidationUtils.checkPasswordRequirements(password),
            errorMessage = null
        )
    }

    fun updateSignupConfirmPassword(confirmPassword: String) {
        val state = _signupState.value
        val error = if (state.confirmPasswordTouched) {
            ValidationUtils.validateConfirmPassword(state.password, confirmPassword)
        } else null
        _signupState.value = state.copy(
            confirmPassword = confirmPassword,
            confirmPasswordError = error,
            errorMessage = null
        )
    }

    // Touch tracking for blur-like validation
    fun touchSignupName() {
        val state = _signupState.value
        _signupState.value = state.copy(
            nameTouched = true,
            nameError = ValidationUtils.validateName(state.name)
        )
    }

    fun touchSignupEmail() {
        val state = _signupState.value
        _signupState.value = state.copy(
            emailTouched = true,
            emailError = ValidationUtils.validateEmail(state.email)
        )
    }

    fun touchSignupPhone() {
        val state = _signupState.value
        _signupState.value = state.copy(
            phoneTouched = true,
            phoneError = ValidationUtils.validatePhone(state.phone)
        )
    }

    fun touchSignupPassword() {
        val state = _signupState.value
        _signupState.value = state.copy(
            passwordTouched = true,
            passwordError = ValidationUtils.validatePassword(state.password)
        )
    }

    fun touchSignupConfirmPassword() {
        val state = _signupState.value
        _signupState.value = state.copy(
            confirmPasswordTouched = true,
            confirmPasswordError = ValidationUtils.validateConfirmPassword(state.password, state.confirmPassword)
        )
    }

    // Step validation for signup
    fun validateSignupStep(step: Int): Boolean {
        val state = _signupState.value
        return when (step) {
            0 -> {
                val nameError = ValidationUtils.validateName(state.name)
                val emailError = ValidationUtils.validateEmail(state.email)
                _signupState.value = state.copy(
                    nameTouched = true,
                    emailTouched = true,
                    nameError = nameError,
                    emailError = emailError
                )
                nameError == null && emailError == null
            }
            1 -> {
                val phoneError = ValidationUtils.validatePhone(state.phone)
                val passwordError = ValidationUtils.validatePassword(state.password)
                val confirmError = ValidationUtils.validateConfirmPassword(state.password, state.confirmPassword)
                _signupState.value = state.copy(
                    phoneTouched = true,
                    passwordTouched = true,
                    confirmPasswordTouched = true,
                    phoneError = phoneError,
                    passwordError = passwordError,
                    confirmPasswordError = confirmError
                )
                phoneError == null && passwordError == null && confirmError == null
            }
            else -> true
        }
    }

    fun isSignupStepValid(step: Int): Boolean {
        val state = _signupState.value
        return when (step) {
            0 -> ValidationUtils.validateName(state.name) == null &&
                    ValidationUtils.validateEmail(state.email) == null
            1 -> ValidationUtils.validatePhone(state.phone) == null &&
                    ValidationUtils.validatePassword(state.password) == null &&
                    ValidationUtils.validateConfirmPassword(state.password, state.confirmPassword) == null
            else -> true
        }
    }

    // Login
    fun login(onSuccess: (User) -> Unit) {
        val state = _loginState.value
        val emailError = ValidationUtils.validateEmail(state.email)
        val passwordError = if (state.password.isBlank() || state.password.length < 8) {
            "Password must be at least 8 characters"
        } else null

        _loginState.value = state.copy(
            emailTouched = true,
            passwordTouched = true,
            emailError = emailError,
            passwordError = passwordError
        )

        if (emailError != null || passwordError != null) return

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

    // Signup
    fun signup(onSuccess: (User) -> Unit) {
        val state = _signupState.value
        val nameError = ValidationUtils.validateName(state.name)
        val emailError = ValidationUtils.validateEmail(state.email)
        val phoneError = ValidationUtils.validatePhone(state.phone)
        val passwordError = ValidationUtils.validatePassword(state.password)
        val confirmError = ValidationUtils.validateConfirmPassword(state.password, state.confirmPassword)

        _signupState.value = state.copy(
            nameTouched = true,
            emailTouched = true,
            phoneTouched = true,
            passwordTouched = true,
            confirmPasswordTouched = true,
            nameError = nameError,
            emailError = emailError,
            phoneError = phoneError,
            passwordError = passwordError,
            confirmPasswordError = confirmError
        )

        if (nameError != null || emailError != null || phoneError != null ||
            passwordError != null || confirmError != null) return

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

    fun logout(onLoggedOut: () -> Unit) {
        viewModelScope.launch {
            authRepository.logout()
            _loginState.value = LoginUiState()
            _signupState.value = SignupUiState()
            onLoggedOut()
        }
    }

    fun checkAuthState(
        onAuthenticated: (User) -> Unit,
        onUnauthenticated: () -> Unit
    ) {
        viewModelScope.launch {
            if (authRepository.isLoggedIn()) {
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
    val emailTouched: Boolean = false,
    val passwordTouched: Boolean = false,
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
    val nameTouched: Boolean = false,
    val emailTouched: Boolean = false,
    val phoneTouched: Boolean = false,
    val passwordTouched: Boolean = false,
    val confirmPasswordTouched: Boolean = false,
    val passwordStrength: ValidationUtils.PasswordStrength = ValidationUtils.PasswordStrength.WEAK,
    val passwordRequirements: ValidationUtils.PasswordRequirements = ValidationUtils.PasswordRequirements(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
