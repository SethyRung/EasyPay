package com.sethy.easypay.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Notification(
    val id: String,
    val title: String,
    val body: String,
    val type: NotificationType,
    val timestamp: Long,
    val isRead: Boolean
)
