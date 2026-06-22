package com.sethy.easypay.ui.state

import com.sethy.easypay.data.model.User

data class ProfileUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val user: User? = null,
    val showLogoutDialog: Boolean = false
)