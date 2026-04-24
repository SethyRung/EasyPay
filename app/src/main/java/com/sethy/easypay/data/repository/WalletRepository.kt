package com.sethy.easypay.data.repository

import com.sethy.easypay.data.api.ApiProvider
import com.sethy.easypay.data.api.WalletApi
import com.sethy.easypay.data.model.Transaction
import com.sethy.easypay.data.model.TransactionType

class WalletRepository(
    apiProvider: ApiProvider
) : BaseRepository() {

    private val walletApi: WalletApi by lazy {
        apiProvider.createService(WalletApi::class.java)
    }

    suspend fun getBalance(): Result<Double> {
        return safeApiCall {
            walletApi.getBalance()
        }.map { it.balance }
    }

    suspend fun getTransactions(limit: Int = 10, offset: Int = 0): Result<List<Transaction>> {
        return safeApiCall {
            walletApi.getTransactions(limit, offset)
        }.map { response ->
            response.transactions.map { tx ->
                Transaction(
                    id = tx.id,
                    recipientName = tx.description,
                    amount = tx.amount,
                    type = if (tx.type == "credit") TransactionType.RECEIVED else TransactionType.SENT,
                    timestamp = System.currentTimeMillis(),
                    avatarUrl = null
                )
            }
        }
    }
}
