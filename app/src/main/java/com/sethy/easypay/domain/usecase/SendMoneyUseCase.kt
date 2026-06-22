package com.sethy.easypay.domain.usecase

import com.sethy.easypay.data.model.Transaction
import com.sethy.easypay.data.repository.WalletRepository
import javax.inject.Inject

class SendMoneyUseCase @Inject constructor(
    private val walletRepository: WalletRepository
) {
    suspend operator fun invoke(recipient: String, amount: Double): Result<Transaction> =
        walletRepository.sendMoney(recipient, amount)
}
