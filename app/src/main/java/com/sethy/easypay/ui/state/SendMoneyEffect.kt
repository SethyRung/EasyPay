package com.sethy.easypay.ui.state

sealed interface SendMoneyEffect {
    data class NavigateToTransferSuccess(val recipient: String, val amount: Double) : SendMoneyEffect
    data object NavigateBack : SendMoneyEffect
    data class ShowError(val message: String) : SendMoneyEffect
}
