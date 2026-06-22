package com.sethy.easypay.ui.state

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
