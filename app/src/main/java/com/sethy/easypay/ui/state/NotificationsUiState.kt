package com.sethy.easypay.ui.state

import com.sethy.easypay.data.model.Notification

data class NotificationsUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val transientErrorMessage: String? = null,
    val selectedTab: NotificationTab = NotificationTab.ALL,
    val notifications: List<Notification> = emptyList()
)

enum class NotificationTab {
    ALL, UNREAD, ALERTS
}
