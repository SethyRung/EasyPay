package com.sethy.easypay.data.repository

import com.sethy.easypay.data.model.Transaction
import com.sethy.easypay.data.source.WalletDataSource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultWalletRepository @Inject constructor(
    private val walletDataSource: WalletDataSource
) : BaseRepository(), WalletRepository {

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
}
