package com.sethy.easypay.ui.state

import com.sethy.easypay.data.model.Transaction

data class TransactionDetailUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val transaction: Transaction? = null
)
