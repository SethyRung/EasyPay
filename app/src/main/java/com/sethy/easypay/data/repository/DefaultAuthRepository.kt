package com.sethy.easypay.data.repository

import com.sethy.easypay.data.auth.AuthSessionNotifier
import com.sethy.easypay.data.local.AuthTokenManager
import com.sethy.easypay.data.model.User
import com.sethy.easypay.data.source.AuthDataSource
import com.sethy.easypay.data.source.WalletDataSource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultAuthRepository @Inject constructor(
    private val authDataSource: AuthDataSource,
    private val tokenManager: AuthTokenManager,
    private val walletDataSource: WalletDataSource,
    authSessionNotifier: AuthSessionNotifier
) : BaseRepository(authSessionNotifier), AuthRepository {

    override suspend fun login(email: String, password: String): Result<User> {
        return authDataSource.login(email, password)
            .onSuccess { result ->
                saveTokens(result)
                walletDataSource.setCurrentUser(result.user)
            }
            .map { it.user }
    }

    override suspend fun register(
        name: String,
        email: String,
        phone: String,
        password: String
    ): Result<User> {
        return authDataSource.register(name, email, phone, password)
            .onSuccess { result ->
                saveTokens(result)
                walletDataSource.setCurrentUser(result.user)
            }
            .map { it.user }
    }

    override suspend fun logout(): Result<Unit> {
        val serverResult = authDataSource.logout()
        tokenManager.clearTokens()
        return serverResult
    }

    override suspend fun isLoggedIn(): Boolean {
        return tokenManager.getAccessToken() != null
    }

    override suspend fun refreshSession(): Result<User> = authDataSource.getSession()

    private suspend fun saveTokens(result: com.sethy.easypay.data.source.AuthResult) {
        tokenManager.saveTokens(result.accessToken)
    }
}
