package com.sethy.easypay.data.repository

import com.sethy.easypay.data.api.ApiProvider
import com.sethy.easypay.data.api.AuthApi
import com.sethy.easypay.data.dto.LoginRequest
import com.sethy.easypay.data.dto.RefreshTokenRequest
import com.sethy.easypay.data.dto.RegisterRequest
import com.sethy.easypay.data.local.AuthTokenManager
import com.sethy.easypay.data.model.User
import java.util.concurrent.TimeUnit

class AuthRepository(
    private val apiProvider: ApiProvider,
    private val tokenManager: AuthTokenManager
) : BaseRepository() {

    private val authApi: AuthApi by lazy {
        apiProvider.createService(AuthApi::class.java)
    }

    suspend fun login(email: String, password: String): Result<User> {
        return safeApiCall {
            authApi.login(LoginRequest(email, password))
        }.map { authData ->
            tokenManager.saveTokens(authData.accessToken, authData.refreshToken)
            tokenManager.setTokenExpiry(
                System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(15)
            )
            User(
                id = authData.user.id,
                name = authData.user.name,
                email = authData.user.email,
                phone = authData.user.phone,
                balance = 0.0
            )
        }
    }

    suspend fun register(name: String, email: String, phone: String, password: String): Result<User> {
        return safeApiCall {
            authApi.register(RegisterRequest(email, phone, name, password))
        }.map { authData ->
            tokenManager.saveTokens(authData.accessToken, authData.refreshToken)
            tokenManager.setTokenExpiry(
                System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(15)
            )
            User(
                id = authData.user.id,
                name = authData.user.name,
                email = authData.user.email,
                phone = authData.user.phone,
                balance = 0.0
            )
        }
    }

    suspend fun logout() {
        try {
            authApi.logout()
        } catch (_: Exception) {
            // Ignore network errors on logout
        } finally {
            tokenManager.clearTokens()
        }
    }

    suspend fun isLoggedIn(): Boolean {
        return tokenManager.getAccessToken() != null
    }
}
