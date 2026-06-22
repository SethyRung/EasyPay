package com.sethy.easypay.ui.state

sealed interface SendMoneyEvent {
    data class KeypadKeyClicked(val key: String) : SendMoneyEvent
    data object BackspaceClicked : SendMoneyEvent
    data class Send(val recipient: String, val amount: Double) : SendMoneyEvent
    data object DismissError : SendMoneyEvent
}
