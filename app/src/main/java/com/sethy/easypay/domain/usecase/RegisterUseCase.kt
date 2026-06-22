package com.sethy.easypay.domain.usecase

import com.sethy.easypay.data.model.User
import com.sethy.easypay.data.repository.AuthRepository
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        name: String,
        email: String,
        phone: String,
        password: String
    ): Result<User> = authRepository.register(name, email, phone, password)
}
