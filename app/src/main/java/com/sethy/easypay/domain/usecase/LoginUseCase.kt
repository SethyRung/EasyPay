package com.sethy.easypay.domain.usecase

import com.sethy.easypay.data.model.User
import com.sethy.easypay.data.repository.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<User> =
        authRepository.login(email, password)
}
