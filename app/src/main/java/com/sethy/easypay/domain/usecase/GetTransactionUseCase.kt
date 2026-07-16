package com.sethy.easypay.domain.usecase

import com.sethy.easypay.data.model.Transaction
import com.sethy.easypay.data.repository.WalletRepository
import javax.inject.Inject

class GetTransactionUseCase @Inject constructor(
    private val walletRepository: WalletRepository
) {
    suspend operator fun invoke(id: String): Result<Transaction> =
        walletRepository.getTransfer(id)
}
