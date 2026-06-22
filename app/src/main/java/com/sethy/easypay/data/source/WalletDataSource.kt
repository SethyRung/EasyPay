package com.sethy.easypay.data.source

import com.sethy.easypay.data.model.Notification
import com.sethy.easypay.data.model.Transaction

interface WalletDataSource {
    suspend fun getBalance(): Result<Double>
    suspend fun getTransactions(limit: Int, offset: Int): Result<List<Transaction>>
    suspend fun getTransaction(id: String): Result<Transaction>
    suspend fun sendMoney(recipient: String, amountMinor: Long): Result<Transaction>
    suspend fun getNotifications(): Result<List<Notification>>
    suspend fun markNotificationRead(id: String): Result<Unit>
}
