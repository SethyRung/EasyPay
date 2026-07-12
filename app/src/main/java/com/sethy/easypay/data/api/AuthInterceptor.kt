package com.sethy.easypay.data.api

import com.sethy.easypay.data.dto.RefreshTokenRequest
import com.sethy.easypay.data.local.AuthTokenManager
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthInterceptor @Inject constructor(
    private val tokenManager: AuthTokenManager,
    private val authApi: AuthApi
) : Interceptor {

    private val publicPaths: Set<String> = setOf(
        "auth/login",
        "auth/register",
        "auth/refresh"
    )

    private val refreshMutex = Mutex()

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        if (isPublicPath(originalRequest.url)) {
            return chain.proceed(originalRequest)
        }

        val token = getValidAccessToken()

        val requestWithToken = addAuthHeader(originalRequest, token)
        val response = chain.proceed(requestWithToken)

        if (response.code == 401) {
            val newToken = refreshBlocking()

            return if (newToken != null) {
                response.close()
                val retriedRequest = addAuthHeader(originalRequest, newToken)
                chain.proceed(retriedRequest)
            } else {
                tokenManager.clearTokensBlocking()
                response
            }
        }

        return response
    }

    internal fun isPublicPath(url: HttpUrl): Boolean {
        val segments = url.encodedPath
            .split('/')
            .filter { it.isNotEmpty() }
        val apiIndex = segments.indexOf("api")
        val effective = if (apiIndex >= 0) segments.drop(apiIndex + 1) else segments
        if (effective.size != 2) return false
        val key = "${effective[0]}/${effective[1]}"
        return publicPaths.contains(key)
    }

    private fun addAuthHeader(request: Request, token: String? = null): Request {
        val accessToken = token ?: tokenManager.getAccessTokenSync() ?: return request

        return request.newBuilder()
            .header("Authorization", "Bearer $accessToken")
            .build()
    }

    private fun getValidAccessToken(): String? {
        val currentToken = tokenManager.getAccessTokenSync()
        if (currentToken != null && !tokenManager.isAccessTokenExpiredSync()) {
            return currentToken
        }
        return refreshBlocking()
    }

    private fun refreshBlocking(): String? = runBlocking {
        refreshMutex.withLock { performRefresh() }
    }

    private suspend fun performRefresh(): String? {
        val currentToken = tokenManager.getAccessTokenSync()
        if (currentToken != null && !tokenManager.isAccessTokenExpiredSync()) {
            return currentToken
        }

        return try {
            val refreshToken = tokenManager.getRefreshTokenSync() ?: return null

            val response = authApi.refreshToken(RefreshTokenRequest(refreshToken))

            if (response.status.code == "SUCCESS") {
                val authData = response.data!!
                tokenManager.saveTokens(authData.accessToken, authData.refreshToken)
                tokenManager.setTokenExpiry(
                    System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(15)
                )
                authData.accessToken
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
}
