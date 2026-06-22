package com.sethy.easypay.ui.state

sealed interface TransactionDetailEffect {
    data object NavigateBack : TransactionDetailEffect
    data class ShowError(val message: String) : TransactionDetailEffect
    data object ShareReceipt : TransactionDetailEffect
}
