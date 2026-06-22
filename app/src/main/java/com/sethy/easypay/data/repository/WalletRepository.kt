package com.sethy.easypay.data.repository

import com.sethy.easypay.data.model.Transaction
import com.sethy.easypay.data.model.User

interface WalletRepository {
    suspend fun getCurrentUser(): Result<User>
    suspend fun getBalance(): Result<Double>
    suspend fun getTransactions(limit: Int = 10, offset: Int = 0): Result<List<Transaction>>
    suspend fun getTransaction(id: String): Result<Transaction>
    suspend fun sendMoney(recipient: String, amount: Double): Result<Transaction>
}
