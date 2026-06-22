package com.sethy.easypay.domain.usecase

import com.sethy.easypay.data.repository.NotificationRepository
import javax.inject.Inject

class MarkNotificationReadUseCase @Inject constructor(
    private val notificationRepository: NotificationRepository
) {
    suspend operator fun invoke(id: String): Result<Unit> =
        notificationRepository.markAsRead(id)
}
