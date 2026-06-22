package com.sethy.easypay.data.source

import com.sethy.easypay.data.api.NotificationApi
import com.sethy.easypay.data.api.WalletApi
import com.sethy.easypay.data.dto.SendMoneyRequest
import com.sethy.easypay.data.mapper.toBalance
import com.sethy.easypay.data.mapper.toNotification
import com.sethy.easypay.data.mapper.toTransaction
import com.sethy.easypay.data.model.Notification
import com.sethy.easypay.data.model.Transaction
import com.sethy.easypay.data.repository.BaseRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteWalletDataSource @Inject constructor(
    private val walletApi: WalletApi,
    private val notificationApi: NotificationApi
) : BaseRepository(), WalletDataSource {

    override suspend fun getBalance(): Result<Double> = safeApiCall {
        walletApi.getBalance()
    }.map { it.toBalance() }

    override suspend fun getTransactions(limit: Int, offset: Int): Result<List<Transaction>> = safeApiCall {
        walletApi.getTransactions(limit, offset)
    }.map { response ->
        response.transactions.map { it.toTransaction() }
    }

    override suspend fun getTransaction(id: String): Result<Transaction> = safeApiCall {
        walletApi.getTransaction(id)
    }.map { it.toTransaction() }

    override suspend fun sendMoney(recipient: String, amountMinor: Long): Result<Transaction> = safeApiCall {
        walletApi.sendMoney(SendMoneyRequest(recipient, amountMinor))
    }.map { it.toTransaction() }

    override suspend fun getNotifications(): Result<List<Notification>> = safeApiCall {
        notificationApi.getNotifications()
    }.map { response ->
        response.map { it.toNotification() }
    }

    override suspend fun markNotificationRead(id: String): Result<Unit> = safeApiCall {
        notificationApi.markAsRead(id)
    }
}
