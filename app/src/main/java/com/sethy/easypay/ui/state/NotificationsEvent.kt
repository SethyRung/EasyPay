package com.sethy.easypay.ui.state

sealed interface NotificationsEvent {
    data object Load : NotificationsEvent
    data class TabSelected(val tab: NotificationTab) : NotificationsEvent
    data class NotificationClicked(val id: String) : NotificationsEvent
    data object MarkAllRead : NotificationsEvent
    data object Back : NotificationsEvent
    data object DismissError : NotificationsEvent
    data object DismissTransientError : NotificationsEvent
}
