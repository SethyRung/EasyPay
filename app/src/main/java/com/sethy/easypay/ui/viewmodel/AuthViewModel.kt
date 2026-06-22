package com.sethy.easypay.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sethy.easypay.design.components.PasswordRequirements as DesignPasswordRequirements
import com.sethy.easypay.design.components.PasswordStrength as DesignPasswordStrength
import com.sethy.easypay.domain.usecase.LoginUseCase
import com.sethy.easypay.domain.usecase.RegisterUseCase
import com.sethy.easypay.ui.state.LoginEffect
import com.sethy.easypay.ui.state.LoginEvent
import com.sethy.easypay.ui.state.LoginUiState
import com.sethy.easypay.ui.state.SignupEffect
import com.sethy.easypay.ui.state.SignupEvent
import com.sethy.easypay.ui.state.SignupUiState
import com.sethy.easypay.util.ValidationUtils
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
class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase
) : ViewModel() {

    private val _loginState = MutableStateFlow(LoginUiState())
    val loginState: StateFlow<LoginUiState> = _loginState.asStateFlow()

    private val _loginEffect = Channel<LoginEffect>(Channel.BUFFERED)
    val loginEffect: Flow<LoginEffect> = _loginEffect.receiveAsFlow()

    private val _signupState = MutableStateFlow(SignupUiState())
    val signupState: StateFlow<SignupUiState> = _signupState.asStateFlow()

    private val _signupEffect = Channel<SignupEffect>(Channel.BUFFERED)
    val signupEffect: Flow<SignupEffect> = _signupEffect.receiveAsFlow()

    fun onLoginEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.EmailChanged -> {
                val state = _loginState.value
                _loginState.value = state.copy(
                    email = event.email,
                    emailError = if (state.emailTouched) ValidationUtils.validateEmail(event.email) else null,
                    errorMessage = null
                )
            }
            is LoginEvent.PasswordChanged -> {
                val state = _loginState.value
                _loginState.value = state.copy(
                    password = event.password,
                    passwordError = if (state.passwordTouched) validateLoginPassword(event.password) else null,
                    errorMessage = null
                )
            }
            LoginEvent.EmailTouched -> {
                val state = _loginState.value
                _loginState.value = state.copy(
                    emailTouched = true,
                    emailError = ValidationUtils.validateEmail(state.email)
                )
            }
            LoginEvent.PasswordTouched -> {
                val state = _loginState.value
                _loginState.value = state.copy(
                    passwordTouched = true,
                    passwordError = validateLoginPassword(state.password)
                )
            }
            LoginEvent.Submit -> login()
            LoginEvent.DismissError -> {
                _loginState.value = _loginState.value.copy(errorMessage = null)
            }
        }
    }

    fun onSignupEvent(event: SignupEvent) {
        when (event) {
            is SignupEvent.NameChanged -> {
                val state = _signupState.value
                _signupState.value = state.copy(
                    name = event.name,
                    nameError = ValidationUtils.validateName(event.name),
                    errorMessage = null
                )
            }
            is SignupEvent.EmailChanged -> {
                val state = _signupState.value
                _signupState.value = state.copy(
                    email = event.email,
                    emailError = ValidationUtils.validateEmail(event.email),
                    errorMessage = null
                )
            }
            is SignupEvent.PhoneChanged -> {
                val state = _signupState.value
                _signupState.value = state.copy(
                    phone = event.phone,
                    phoneError = ValidationUtils.validatePhone(event.phone),
                    errorMessage = null
                )
            }
            is SignupEvent.PasswordChanged -> {
                val state = _signupState.value
                _signupState.value = state.copy(
                    password = event.password,
                    passwordError = ValidationUtils.validatePassword(event.password),
                    confirmPasswordError = if (state.confirmPassword.isNotEmpty()) {
                        ValidationUtils.validateConfirmPassword(event.password, state.confirmPassword)
                    } else null,
                    passwordStrength = ValidationUtils.calculatePasswordStrength(event.password).toDesign(),
                    passwordRequirements = ValidationUtils.checkPasswordRequirements(event.password).toDesign(),
                    errorMessage = null
                )
            }
            is SignupEvent.ConfirmPasswordChanged -> {
                val state = _signupState.value
                _signupState.value = state.copy(
                    confirmPassword = event.confirmPassword,
                    confirmPasswordError = ValidationUtils.validateConfirmPassword(
                        state.password,
                        event.confirmPassword
                    ),
                    errorMessage = null
                )
            }
            is SignupEvent.TermsAcceptedChanged -> {
                _signupState.value = _signupState.value.copy(termsAccepted = event.accepted)
            }
            SignupEvent.Submit -> signup()
            SignupEvent.DismissError -> {
                _signupState.value = _signupState.value.copy(errorMessage = null)
            }
        }
    }

    private fun login() {
        val state = _loginState.value
        val emailError = ValidationUtils.validateEmail(state.email)
        val passwordError = validateLoginPassword(state.password)

        _loginState.value = state.copy(
            emailTouched = true,
            passwordTouched = true,
            emailError = emailError,
            passwordError = passwordError
        )

        if (emailError != null || passwordError != null) return

        viewModelScope.launch {
            _loginState.value = state.copy(isLoading = true)
            loginUseCase(state.email, state.password)
                .onSuccess {
                    _loginState.value = LoginUiState()
                    _loginEffect.send(LoginEffect.NavigateToHome)
                }
                .onFailure { error ->
                    _loginState.value = state.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Login failed"
                    )
                }
        }
    }

    private fun signup() {
        val state = _signupState.value
        val validatedState = state.copy(
            nameError = ValidationUtils.validateName(state.name),
            emailError = ValidationUtils.validateEmail(state.email),
            phoneError = ValidationUtils.validatePhone(state.phone),
            passwordError = ValidationUtils.validatePassword(state.password),
            confirmPasswordError = ValidationUtils.validateConfirmPassword(
                state.password,
                state.confirmPassword
            )
        )
        _signupState.value = validatedState

        if (validatedState.run {
                listOfNotNull(
                    nameError, emailError, phoneError,
                    passwordError, confirmPasswordError
                ).isNotEmpty()
            }) return

        viewModelScope.launch {
            _signupState.value = validatedState.copy(isLoading = true)
            registerUseCase(
                validatedState.name,
                validatedState.email,
                validatedState.phone,
                validatedState.password
            )
                .onSuccess {
                    _signupState.value = SignupUiState()
                    _signupEffect.send(SignupEffect.NavigateToHome)
                }
                .onFailure { error ->
                    _signupState.value = validatedState.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Signup failed"
                    )
                }
        }
    }

    private fun validateLoginPassword(password: String): String? {
        return if (password.isBlank() || password.length < 8) {
            "Password must be at least 8 characters"
        } else null
    }

    private fun ValidationUtils.PasswordStrength.toDesign(): DesignPasswordStrength =
        when (this) {
            ValidationUtils.PasswordStrength.WEAK -> DesignPasswordStrength.WEAK
            ValidationUtils.PasswordStrength.MEDIUM -> DesignPasswordStrength.MEDIUM
            ValidationUtils.PasswordStrength.STRONG -> DesignPasswordStrength.STRONG
        }

    private fun ValidationUtils.PasswordRequirements.toDesign(): DesignPasswordRequirements =
        DesignPasswordRequirements(
            minLength = minLength,
            hasUppercase = hasUppercase,
            hasLowercase = hasLowercase,
            hasNumber = hasNumber
        )
}
