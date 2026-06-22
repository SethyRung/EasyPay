package com.sethy.easypay.domain.usecase

import com.sethy.easypay.data.model.Notification
import com.sethy.easypay.data.repository.NotificationRepository
import javax.inject.Inject

class GetNotificationsUseCase @Inject constructor(
    private val notificationRepository: NotificationRepository
) {
    suspend operator fun invoke(): Result<List<Notification>> =
        notificationRepository.getNotifications()
}
