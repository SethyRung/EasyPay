package com.sethy.easypay.data.source

import com.sethy.easypay.data.api.AuthApi
import com.sethy.easypay.data.auth.AuthSessionNotifier
import com.sethy.easypay.data.dto.LoginRequest
import com.sethy.easypay.data.dto.RegisterRequest
import com.sethy.easypay.data.mapper.toAuthResult
import com.sethy.easypay.data.mapper.toUser
import com.sethy.easypay.data.repository.BaseRepository
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteAuthDataSource @Inject constructor(
    private val authApi: AuthApi,
    authSessionNotifier: AuthSessionNotifier
) : BaseRepository(authSessionNotifier), AuthDataSource {

    override suspend fun login(email: String, password: String): Result<AuthResult> = safeApiCall {
        authApi.signIn(LoginRequest(email, password))
    }.map { it.toAuthResult() }

    override suspend fun register(
        name: String,
        email: String,
        phone: String,
        password: String
    ): Result<AuthResult> = safeApiCall {
        authApi.signUp(RegisterRequest(email, phone, name, password))
    }.map { it.toAuthResult() }

    override suspend fun logout(): Result<Unit> = try {
        authApi.signOut()
        Result.success(Unit)
    } catch (e: HttpException) {
        Result.failure(
            ApiException("Logout failed (HTTP ${e.code()})", "HTTP_${e.code()}")
        )
    } catch (e: IOException) {
        Result.failure(
            NetworkException("Logout failed: ${e.message ?: "network error"}", e)
        )
    }

    override suspend fun getSession(): Result<com.sethy.easypay.data.model.User> = safeApiCall {
        authApi.getSession()
    }.map { it.user.toUser() }
}
