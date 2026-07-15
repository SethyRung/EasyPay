package com.sethy.easypay.data.api

import com.sethy.easypay.data.local.AuthTokenManager
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthInterceptor @Inject constructor(
    private val tokenManager: AuthTokenManager,
    @Suppress("unused") private val authApi: AuthApi
) : Interceptor {

    private val publicPaths: Set<String> = setOf(
        "auth/sign-in/email",
        "auth/sign-up/email",
        "auth/get-session"
    )

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        if (isPublicPath(originalRequest.url)) {
            return chain.proceed(originalRequest)
        }

        val token = tokenManager.getAccessTokenSync()
        val requestWithToken = addAuthHeader(originalRequest, token)
        return chain.proceed(requestWithToken)
    }

    internal fun isPublicPath(url: HttpUrl): Boolean {
        val segments = url.encodedPath
            .split('/')
            .filter { it.isNotEmpty() }
        val apiIndex = segments.indexOf("api")
        val effective = if (apiIndex >= 0) segments.drop(apiIndex + 1) else segments
        val path = effective.joinToString("/")
        return publicPaths.contains(path)
    }

    private fun addAuthHeader(request: Request, token: String? = null): Request {
        val accessToken = token ?: tokenManager.getAccessTokenSync() ?: return request

        return request.newBuilder()
            .header("Authorization", "Bearer $accessToken")
            .build()
    }
}
