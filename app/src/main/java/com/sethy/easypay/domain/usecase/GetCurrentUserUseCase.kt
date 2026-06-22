package com.sethy.easypay.domain.usecase

import com.sethy.easypay.data.model.User
import com.sethy.easypay.data.repository.WalletRepository
import javax.inject.Inject

class GetCurrentUserUseCase @Inject constructor(
    private val walletRepository: WalletRepository
) {
    suspend operator fun invoke(): Result<User> = walletRepository.getCurrentUser()
}