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
import com.sethy.easypay.ui.state.SignupField
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
                updateSignupField(
                    name = event.name,
                    nameError = if (_signupState.value.nameTouched) ValidationUtils.validateName(event.name) else null
                )
            }
            is SignupEvent.EmailChanged -> {
                updateSignupField(
                    email = event.email,
                    emailError = if (_signupState.value.emailTouched) ValidationUtils.validateEmail(event.email) else null
                )
            }
            is SignupEvent.PhoneChanged -> {
                updateSignupField(
                    phone = event.phone,
                    phoneError = if (_signupState.value.phoneTouched) ValidationUtils.validatePhone(event.phone) else null
                )
            }
            is SignupEvent.PasswordChanged -> {
                val state = _signupState.value
                val passwordError = if (state.passwordTouched) ValidationUtils.validatePassword(event.password) else null
                val confirmError = if (state.confirmPasswordTouched && state.confirmPassword.isNotEmpty()) {
                    ValidationUtils.validateConfirmPassword(event.password, state.confirmPassword)
                } else null
                _signupState.value = state.copy(
                    password = event.password,
                    passwordError = passwordError,
                    confirmPasswordError = confirmError,
                    passwordStrength = ValidationUtils.calculatePasswordStrength(event.password).toDesign(),
                    passwordRequirements = ValidationUtils.checkPasswordRequirements(event.password).toDesign(),
                    errorMessage = null
                )
            }
            is SignupEvent.ConfirmPasswordChanged -> {
                val state = _signupState.value
                val confirmError = if (state.confirmPasswordTouched || state.confirmPassword.isNotEmpty()) {
                    ValidationUtils.validateConfirmPassword(state.password, event.confirmPassword)
                } else null
                _signupState.value = state.copy(
                    confirmPassword = event.confirmPassword,
                    confirmPasswordError = confirmError,
                    errorMessage = null
                )
            }
            is SignupEvent.TermsAcceptedChanged -> {
                _signupState.value = _signupState.value.copy(termsAccepted = event.accepted)
            }
            is SignupEvent.FieldTouched -> {
                applyFieldTouched(event.field)
            }
            SignupEvent.NextStep -> advanceSignupStep()
            SignupEvent.PreviousStep -> {
                val state = _signupState.value
                if (state.currentStep > 0) {
                    _signupState.value = state.copy(currentStep = state.currentStep - 1)
                } else {
                    viewModelScope.launch { _signupEffect.send(SignupEffect.NavigateBack) }
                }
            }
            SignupEvent.Submit -> signup()
            SignupEvent.DismissError -> {
                _signupState.value = _signupState.value.copy(errorMessage = null)
            }
        }
    }

    private fun updateSignupField(
        name: String? = null,
        email: String? = null,
        phone: String? = null,
        nameError: String? = null,
        emailError: String? = null,
        phoneError: String? = null
    ) {
        val state = _signupState.value
        _signupState.value = state.copy(
            name = name ?: state.name,
            email = email ?: state.email,
            phone = phone ?: state.phone,
            nameError = nameError ?: state.nameError,
            emailError = emailError ?: state.emailError,
            phoneError = phoneError ?: state.phoneError,
            errorMessage = null
        )
    }

    private fun applyFieldTouched(field: SignupField) {
        val state = _signupState.value
        val newState = when (field) {
            SignupField.NAME -> state.copy(
                nameTouched = true,
                nameError = ValidationUtils.validateName(state.name)
            )
            SignupField.EMAIL -> state.copy(
                emailTouched = true,
                emailError = ValidationUtils.validateEmail(state.email)
            )
            SignupField.PHONE -> state.copy(
                phoneTouched = true,
                phoneError = ValidationUtils.validatePhone(state.phone)
            )
            SignupField.PASSWORD -> state.copy(
                passwordTouched = true,
                passwordError = ValidationUtils.validatePassword(state.password)
            )
            SignupField.CONFIRM_PASSWORD -> state.copy(
                confirmPasswordTouched = true,
                confirmPasswordError = ValidationUtils.validateConfirmPassword(
                    state.password,
                    state.confirmPassword
                )
            )
        }
        _signupState.value = newState
    }

    private fun advanceSignupStep() {
        val state = _signupState.value
        val validatedState = when (state.currentStep) {
            0 -> state.copy(
                nameTouched = true,
                emailTouched = true,
                nameError = ValidationUtils.validateName(state.name),
                emailError = ValidationUtils.validateEmail(state.email)
            )
            1 -> state.copy(
                phoneTouched = true,
                passwordTouched = true,
                confirmPasswordTouched = true,
                phoneError = ValidationUtils.validatePhone(state.phone),
                passwordError = ValidationUtils.validatePassword(state.password),
                confirmPasswordError = ValidationUtils.validateConfirmPassword(
                    state.password,
                    state.confirmPassword
                )
            )
            else -> state
        }
        _signupState.value = validatedState

        val isValid = when (validatedState.currentStep) {
            0 -> validatedState.nameError == null && validatedState.emailError == null
            1 -> validatedState.phoneError == null &&
                validatedState.passwordError == null &&
                validatedState.confirmPasswordError == null
            else -> true
        }
        if (isValid && validatedState.currentStep < 2) {
            _signupState.value = validatedState.copy(currentStep = validatedState.currentStep + 1)
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
            nameTouched = true,
            emailTouched = true,
            phoneTouched = true,
            passwordTouched = true,
            confirmPasswordTouched = true,
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
