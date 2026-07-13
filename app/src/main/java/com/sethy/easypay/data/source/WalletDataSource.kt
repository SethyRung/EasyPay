package com.sethy.easypay.data.source

import com.sethy.easypay.data.model.Notification
import com.sethy.easypay.data.model.Transaction
import com.sethy.easypay.data.model.User

data class BillPayment(
    val transactionId: String,
    val walletId: String,
    val balanceAfterMinor: Long,
    val amountMinor: Long
)

data class TopUp(
    val transactionId: String,
    val walletId: String,
    val balanceAfterMinor: Long,
    val amountMinor: Long
)

interface WalletDataSource {
    fun setCurrentUser(user: User)
    suspend fun getUser(): Result<User>
    suspend fun getBalance(): Result<Double>
    suspend fun getTransactions(limit: Int, offset: Int): Result<List<Transaction>>
    suspend fun getTransaction(id: String): Result<Transaction>
    suspend fun sendMoney(recipient: String, amountMinor: Long): Result<Transaction>
    suspend fun payBill(
        billerCode: String,
        accountNumber: String,
        amountMinor: Long,
        note: String?
    ): Result<BillPayment>
    suspend fun topUp(amountMinor: Long, note: String?): Result<TopUp>
    suspend fun getNotifications(): Result<List<Notification>>
    suspend fun markNotificationRead(id: String): Result<Unit>
}
