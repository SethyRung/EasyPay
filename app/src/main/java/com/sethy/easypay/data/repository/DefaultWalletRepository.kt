package com.sethy.easypay.data.repository

import com.sethy.easypay.data.auth.AuthSessionNotifier
import com.sethy.easypay.data.model.Transaction
import com.sethy.easypay.data.model.User
import com.sethy.easypay.data.source.BillPayment
import com.sethy.easypay.data.source.TopUp
import com.sethy.easypay.data.source.WalletDataSource
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

    override suspend fun getTransaction(id: String): Result<Transaction> =
        walletDataSource.getTransaction(id)

    override suspend fun sendMoney(recipient: String, amount: Double): Result<Transaction> {
        val amountMinor = (amount * 100).toLong()
        return walletDataSource.sendMoney(recipient, amountMinor)
    }

    override suspend fun payBill(
        billerCode: String,
        accountNumber: String,
        amountMinor: Long,
        note: String?
    ): Result<BillPayment> = walletDataSource.payBill(billerCode, accountNumber, amountMinor, note)

    override suspend fun topUp(amountMinor: Long, note: String?): Result<TopUp> =
        walletDataSource.topUp(amountMinor, note)
}
