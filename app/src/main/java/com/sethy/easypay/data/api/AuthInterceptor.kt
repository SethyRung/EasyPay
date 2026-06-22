package com.sethy.easypay.data.api

import com.sethy.easypay.data.dto.RefreshTokenRequest
import com.sethy.easypay.data.local.AuthTokenManager
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
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

    // Paths that don't need an auth token
    private val publicPaths = setOf(
        "auth/login",
        "auth/register",
        "auth/refresh"
    )

    /** Mutex ensures only one thread refreshes at a time.
     *  This is critical because the backend rotates refresh tokens
     *  (revokes the old one immediately). Concurrent refreshes with
     *  the same old token would race and fail. */
    private val refreshMutex = Mutex()

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // Skip auth for public endpoints
        if (isPublicPath(originalRequest)) {
            return chain.proceed(originalRequest)
        }

        // ── Proactive refresh ──────────────────────────────────────────
        // If the access token is already expired (or missing), refresh
        // *before* sending the request. This avoids wasting a 401 round-trip.
        val token = runBlocking {
            getValidAccessToken()
        }

        val requestWithToken = addAuthHeader(originalRequest, token)
        val response = chain.proceed(requestWithToken)

        // ── Reactive refresh (401 fallback) ───────────────────────────
        // Handles clock skew or server-side revocation that the proactive
        // check didn't catch.
        if (response.code == 401) {
            val newToken = runBlocking {
                refreshMutex.withLock { performRefresh() }
            }

            return if (newToken != null) {
                response.close()
                val retriedRequest = addAuthHeader(originalRequest, newToken)
                chain.proceed(retriedRequest)
            } else {
                // Refresh failed — clear tokens and let the 401 propagate
                runBlocking { tokenManager.clearTokens() }
                response
            }
        }

        return response
    }

    private fun isPublicPath(request: Request): Boolean {
        val path = request.url.encodedPath
        return publicPaths.any { path.contains(it) }
    }

    private fun addAuthHeader(request: Request, token: String? = null): Request {
        val accessToken = token ?: runBlocking {
            tokenManager.getAccessToken()
        } ?: return request

        return request.newBuilder()
            .header("Authorization", "Bearer $accessToken")
            .build()
    }

    /** Returns a valid (non-expired) access token, refreshing if necessary. */
    private suspend fun getValidAccessToken(): String? {
        val currentToken = tokenManager.getAccessToken()

        if (currentToken != null && !tokenManager.isAccessTokenExpired()) {
            return currentToken
        }

        // Token is missing or expired — acquire the lock and refresh.
        // Other threads hitting the same state will queue here and reuse
        // the result once the first refresh finishes.
        return refreshMutex.withLock { performRefresh() }
    }

    /** Performs the actual refresh call against /auth/refresh.
     *  Includes a double-check so that if another thread already refreshed
     *  while we were waiting for the lock, we simply return the new token. */
    private suspend fun performRefresh(): String? {
        // Double-check: another thread may have refreshed while we queued
        val currentToken = tokenManager.getAccessToken()
        if (currentToken != null && !tokenManager.isAccessTokenExpired()) {
            return currentToken
        }

        return try {
            val refreshToken = tokenManager.getRefreshToken() ?: return null

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
