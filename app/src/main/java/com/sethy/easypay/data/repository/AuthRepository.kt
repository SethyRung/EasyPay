package com.sethy.easypay.data.repository

import com.sethy.easypay.data.api.ApiProvider
import com.sethy.easypay.data.dto.LoginRequest
import com.sethy.easypay.data.dto.RefreshTokenRequest
import com.sethy.easypay.data.dto.RegisterRequest
import com.sethy.easypay.data.local.AuthTokenManager
import com.sethy.easypay.data.model.User
import java.util.concurrent.TimeUnit

class AuthRepository(
    private val tokenManager: AuthTokenManager
) {
    private val authApi = ApiProvider.authApi

    suspend fun login(email: String, password: String): Result<User> {
        return try {
            val response = authApi.login(LoginRequest(email, password))
            if (response.status.code == "SUCCESS") {
                val authData = response.data!!
                tokenManager.saveTokens(
                    authData.accessToken,
                    authData.refreshToken
                )
                tokenManager.setTokenExpiry(
                    System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(15)
                )
                Result.success(User(
                    id = authData.user.id,
                    name = authData.user.name,
                    email = authData.user.email,
                    phone = authData.user.phone,
                    balance = 0.0
                ))
            } else {
                Result.failure(Exception(response.status.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(name: String, email: String, phone: String, password: String): Result<User> {
        return try {
            val response = authApi.register(RegisterRequest(email, phone, name, password))
            if (response.status.code == "SUCCESS") {
                val authData = response.data!!
                tokenManager.saveTokens(
                    authData.accessToken,
                    authData.refreshToken
                )
                tokenManager.setTokenExpiry(
                    System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(15)
                )
                Result.success(User(
                    id = authData.user.id,
                    name = authData.user.name,
                    email = authData.user.email,
                    phone = authData.user.phone,
                    balance = 0.0
                ))
            } else {
                Result.failure(Exception(response.status.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun refreshAccessToken(): Result<String> {
        return try {
            val refreshToken = tokenManager.getRefreshToken()
                ?: return Result.failure(Exception("No refresh token"))

            val response = authApi.refreshToken(RefreshTokenRequest(refreshToken))
            if (response.status.code == "SUCCESS") {
                val authData = response.data!!
                tokenManager.saveTokens(
                    authData.accessToken,
                    authData.refreshToken
                )
                tokenManager.setTokenExpiry(
                    System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(15)
                )
                Result.success(authData.accessToken)
            } else {
                Result.failure(Exception(response.status.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun logout() {
        tokenManager.clearTokens()
    }

    suspend fun isLoggedIn(): Boolean {
        return tokenManager.getAccessToken() != null
    }
}
