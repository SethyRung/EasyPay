package com.sethy.easypay.ui.state

data class SendMoneyUiState(
    val amount: String = "0",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
