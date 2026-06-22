package com.sethy.easypay.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class NotificationResponse(
    val id: String,
    val title: String,
    val body: String,
    val type: String,
    val timestamp: Long,
    val isRead: Boolean
)
