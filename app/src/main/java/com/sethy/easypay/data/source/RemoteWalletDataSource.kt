package com.sethy.easypay.data.source

import com.sethy.easypay.data.api.AuthApi
import com.sethy.easypay.data.api.NotificationApi
import com.sethy.easypay.data.api.WalletApi
import com.sethy.easypay.data.auth.AuthSessionNotifier
import com.sethy.easypay.data.dto.BillPaymentRequest
import com.sethy.easypay.data.dto.CreateTransferDto
import com.sethy.easypay.data.dto.TopUpRequest
import com.sethy.easypay.data.mapper.toBalance
import com.sethy.easypay.data.mapper.toBillPayment
import com.sethy.easypay.data.mapper.toNotification
import com.sethy.easypay.data.mapper.toTopUp
import com.sethy.easypay.data.mapper.toTransaction
import com.sethy.easypay.data.mapper.toUser
import com.sethy.easypay.data.model.Notification
import com.sethy.easypay.data.model.Transaction
import com.sethy.easypay.data.model.User
import com.sethy.easypay.data.repository.BaseRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteWalletDataSource @Inject constructor(
    private val walletApi: WalletApi,
    private val notificationApi: NotificationApi,
    private val authApi: AuthApi,
    authSessionNotifier: AuthSessionNotifier
) : BaseRepository(authSessionNotifier), WalletDataSource {

    override fun setCurrentUser(user: User) {
        // Backend owns user state; no client-side cache to update.
    }

    override suspend fun getUser(): Result<User> = safeApiCall {
        authApi.getSession()
    }.map { it.user.toUser() }

    override suspend fun getBalance(): Result<Double> = safeApiCall {
        walletApi.getBalance()
    }.map { it.toBalance() }

    override suspend fun getTransactions(limit: Int, offset: Int): Result<List<Transaction>> = safeApiCall {
        walletApi.getTransactions(limit, offset)
    }.map { response ->
        response.transactions.map { it.toTransaction() }
    }

    override suspend fun getTransfer(id: String): Result<Transaction> = safeApiCall {
        walletApi.getTransfer(id)
    }.map { it.toTransaction() }

    override suspend fun createTransfer(
        recipientPhone: String,
        amount: Double,
        idempotencyKey: String,
        note: String?
    ): Result<Transaction> = safeApiCall {
        walletApi.createTransfer(CreateTransferDto(recipientPhone, amount, idempotencyKey, note))
    }.map { it.toTransaction() }

    override suspend fun payBill(
        billerCode: String,
        accountNumber: String,
        amount: Double,
        note: String?
    ): Result<BillPayment> = safeApiCall {
        walletApi.payBill(BillPaymentRequest(billerCode, accountNumber, amount, note))
    }.map { it.toBillPayment() }

    override suspend fun topUp(amount: Double, note: String?): Result<TopUp> = safeApiCall {
        walletApi.topUp(TopUpRequest(amount, note))
    }.map { it.toTopUp() }

    override suspend fun getNotifications(): Result<List<Notification>> = safeApiCall {
        notificationApi.getNotifications()
    }.map { response ->
        response.map { it.toNotification() }
    }

    override suspend fun markNotificationRead(id: String): Result<Unit> = safeApiCall {
        notificationApi.markAsRead(id)
    }
}
