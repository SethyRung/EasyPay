package com.sethy.easypay.data.repository

import com.sethy.easypay.data.model.Notification

interface NotificationRepository {
    suspend fun getNotifications(): Result<List<Notification>>
    suspend fun markAsRead(id: String): Result<Unit>
}
