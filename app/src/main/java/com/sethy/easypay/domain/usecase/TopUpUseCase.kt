package com.sethy.easypay.domain.usecase

import com.sethy.easypay.data.source.TopUp
import com.sethy.easypay.data.repository.WalletRepository
import javax.inject.Inject

class TopUpUseCase @Inject constructor(
    private val walletRepository: WalletRepository
) {
    suspend operator fun invoke(
        amountMajor: Double,
        note: String? = null
    ): Result<TopUp> {
        require(amountMajor > 0) { "Top-up amount must be positive" }
        return walletRepository.topUp(amountMajor, note)
    }
}
