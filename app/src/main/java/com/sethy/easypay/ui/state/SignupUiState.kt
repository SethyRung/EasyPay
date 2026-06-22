package com.sethy.easypay.ui.state

import com.sethy.easypay.design.components.PasswordRequirements
import com.sethy.easypay.design.components.PasswordStrength

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
    val passwordStrength: PasswordStrength = PasswordStrength.WEAK,
    val passwordRequirements: PasswordRequirements = PasswordRequirements(),
    val termsAccepted: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)