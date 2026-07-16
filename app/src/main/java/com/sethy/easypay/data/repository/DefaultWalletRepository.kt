package com.sethy.easypay.data.repository

import com.sethy.easypay.data.auth.AuthSessionNotifier
import com.sethy.easypay.data.model.Transaction
import com.sethy.easypay.data.model.User
import com.sethy.easypay.data.source.BillPayment
import com.sethy.easypay.data.source.TopUp
import com.sethy.easypay.data.source.WalletDataSource
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultWalletRepository @Inject constructor(
    private val walletDataSource: WalletDataSource,
    authSessionNotifier: AuthSessionNotifier
) : BaseRepository(authSessionNotifier), WalletRepository {

    override suspend fun getCurrentUser(): Result<User> = walletDataSource.getUser()

    override suspend fun getBalance(): Result<Double> = walletDataSource.getBalance()

    override suspend fun getTransactions(
        limit: Int,
        offset: Int
    ): Result<List<Transaction>> = walletDataSource.getTransactions(limit, offset)

    override suspend fun getTransfer(id: String): Result<Transaction> =
        walletDataSource.getTransfer(id)

    override suspend fun createTransfer(
        recipientPhone: String,
        amount: Double,
        note: String?
    ): Result<Transaction> = walletDataSource.createTransfer(
        recipientPhone = recipientPhone,
        amount = amount,
        idempotencyKey = UUID.randomUUID().toString(),
        note = note
    )

    override suspend fun payBill(
        billerCode: String,
        accountNumber: String,
        amount: Double,
        note: String?
    ): Result<BillPayment> = walletDataSource.payBill(billerCode, accountNumber, amount, note)

    override suspend fun topUp(amount: Double, note: String?): Result<TopUp> =
        walletDataSource.topUp(amount, note)
}
