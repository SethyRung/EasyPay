package com.sethy.easypay.ui.state

import com.sethy.easypay.design.components.PasswordRequirements
import com.sethy.easypay.design.components.PasswordStrength

data class SignupUiState(
    val currentStep: Int = 0,
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
    val passwordStrength: PasswordStrength = PasswordStrength.WEAK,
    val passwordRequirements: PasswordRequirements = PasswordRequirements(),
    val termsAccepted: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
