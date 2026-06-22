package com.sethy.easypay.domain.usecase

import com.sethy.easypay.data.model.Transaction
import com.sethy.easypay.data.repository.WalletRepository
import javax.inject.Inject

class GetTransactionsUseCase @Inject constructor(
    private val walletRepository: WalletRepository
) {
    suspend operator fun invoke(
        limit: Int = 10,
        offset: Int = 0
    ): Result<List<Transaction>> = walletRepository.getTransactions(limit, offset)
}
