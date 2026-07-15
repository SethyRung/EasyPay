package com.sethy.easypay.data.repository

import com.sethy.easypay.data.auth.AuthSessionNotifier
import com.sethy.easypay.data.model.Notification
import com.sethy.easypay.data.source.WalletDataSource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultNotificationRepository @Inject constructor(
    private val walletDataSource: WalletDataSource,
    authSessionNotifier: AuthSessionNotifier
) : BaseRepository(authSessionNotifier), NotificationRepository {

    override suspend fun getNotifications(): Result<List<Notification>> =
        walletDataSource.getNotifications()

    override suspend fun markAsRead(id: String): Result<Unit> =
        walletDataSource.markNotificationRead(id)
}
