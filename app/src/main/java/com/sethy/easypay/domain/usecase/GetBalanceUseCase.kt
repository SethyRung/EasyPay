package com.sethy.easypay.domain.usecase

import com.sethy.easypay.data.repository.WalletRepository
import javax.inject.Inject

class GetBalanceUseCase @Inject constructor(
    private val walletRepository: WalletRepository
) {
    suspend operator fun invoke(): Result<Double> = walletRepository.getBalance()
}
