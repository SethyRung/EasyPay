package com.sethy.easypay.data.source

import com.sethy.easypay.data.api.AuthApi
import com.sethy.easypay.data.dto.LoginRequest
import com.sethy.easypay.data.dto.RefreshTokenRequest
import com.sethy.easypay.data.dto.RegisterRequest
import com.sethy.easypay.data.mapper.toAuthResult
import com.sethy.easypay.data.repository.BaseRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteAuthDataSource @Inject constructor(
    private val authApi: AuthApi
) : BaseRepository(), AuthDataSource {

    override suspend fun login(email: String, password: String): Result<AuthResult> = safeApiCall {
        authApi.login(LoginRequest(email, password))
    }.map { it.toAuthResult() }

    override suspend fun register(
        name: String,
        email: String,
        phone: String,
        password: String
    ): Result<AuthResult> = safeApiCall {
        authApi.register(RegisterRequest(email, phone, name, password))
    }.map { it.toAuthResult() }

    override suspend fun logout(): Result<Unit> = try {
        authApi.logout()
        Result.success(Unit)
    } catch (_: Exception) {
        Result.success(Unit)
    }

    override suspend fun refreshToken(refreshToken: String): Result<AuthResult> = safeApiCall {
        authApi.refreshToken(RefreshTokenRequest(refreshToken))
    }.map { it.toAuthResult() }
}
