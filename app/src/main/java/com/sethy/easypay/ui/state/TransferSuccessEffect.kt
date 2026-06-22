package com.sethy.easypay.ui.state

sealed interface TransferSuccessEffect {
    data object NavigateToHome : TransferSuccessEffect
    data object NavigateToSendMoney : TransferSuccessEffect
}
