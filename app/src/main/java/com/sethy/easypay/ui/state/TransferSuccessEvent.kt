package com.sethy.easypay.ui.state

sealed interface TransferSuccessEvent {
    data object Done : TransferSuccessEvent
    data object TransferMore : TransferSuccessEvent
}
