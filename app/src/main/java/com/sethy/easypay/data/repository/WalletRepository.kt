package com.sethy.easypay.data.repository

import com.sethy.easypay.data.model.Transaction
import com.sethy.easypay.data.model.User
import com.sethy.easypay.data.source.BillPayment
import com.sethy.easypay.data.source.TopUp

interface WalletRepository {
    suspend fun getCurrentUser(): Result<User>
    suspend fun getBalance(): Result<Double>
    suspend fun getTransactions(limit: Int = 10, offset: Int = 0): Result<List<Transaction>>
    suspend fun getTransfer(id: String): Result<Transaction>
    suspend fun createTransfer(
        recipientPhone: String,
        amount: Double,
        note: String?
    ): Result<Transaction>
    suspend fun payBill(
        billerCode: String,
        accountNumber: String,
        amount: Double,
        note: String?
    ): Result<BillPayment>
    suspend fun topUp(amount: Double, note: String?): Result<TopUp>
}
