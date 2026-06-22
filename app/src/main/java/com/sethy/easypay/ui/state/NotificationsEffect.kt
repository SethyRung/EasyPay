package com.sethy.easypay.ui.state

sealed interface NotificationsEffect {
    data object NavigateBack : NotificationsEffect
    data class ShowError(val message: String) : NotificationsEffect
}
