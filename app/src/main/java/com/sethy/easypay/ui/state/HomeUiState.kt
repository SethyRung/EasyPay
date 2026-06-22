package com.sethy.easypay.ui.state

import com.sethy.easypay.data.model.Transaction

data class HomeUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val balance: Double = 0.0,
    val transactions: List<Transaction> = emptyList()
)
