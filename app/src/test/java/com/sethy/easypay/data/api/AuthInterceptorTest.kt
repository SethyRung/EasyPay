package com.sethy.easypay.data.api

import com.sethy.easypay.data.local.AuthTokenManager
import kotlinx.coroutines.test.runTest
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.Interceptor
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class AuthInterceptorTest {

    private val tokenManager: AuthTokenManager = mock()
    private val authApi: AuthApi = mock()
    private val interceptor = AuthInterceptor(tokenManager, authApi)

    @Test
    fun `isPublicPath matches auth sign-in-email under api prefix`() {
        assertTrue(interceptor.isPublicPath("http://10.0.2.2:8080/api/auth/sign-in/email".toHttpUrl()))
    }

    @Test
    fun `isPublicPath matches auth sign-up-email under api prefix`() {
        assertTrue(interceptor.isPublicPath("http://10.0.2.2:8080/api/auth/sign-up/email".toHttpUrl()))
    }

    @Test
    fun `isPublicPath matches auth get-session under api prefix`() {
        assertTrue(interceptor.isPublicPath("http://10.0.2.2:8080/api/auth/get-session".toHttpUrl()))
    }

    @Test
    fun `isPublicPath matches public paths without api prefix`() {
        assertTrue(interceptor.isPublicPath("http://10.0.2.2:8080/auth/sign-in/email".toHttpUrl()))
    }

    @Test
    fun `isPublicPath does NOT match lookalike sign-in-email-attempt`() {
        assertFalse(
            interceptor.isPublicPath("http://10.0.2.2:8080/api/auth/sign-in-email-attempt".toHttpUrl())
        )
    }

    @Test
    fun `isPublicPath does NOT match wallet sign-in-email`() {
        assertFalse(
            interceptor.isPublicPath("http://10.0.2.2:8080/api/wallet/sign-in-email".toHttpUrl())
        )
    }

    @Test
    fun `isPublicPath does NOT match deeper get-session subpath`() {
        assertFalse(
            interceptor.isPublicPath("http://10.0.2.2:8080/api/auth/get-session/extra".toHttpUrl())
        )
    }

    @Test
    fun `isPublicPath does NOT match wallet balance`() {
        assertFalse(interceptor.isPublicPath("http://10.0.2.2:8080/api/wallet/balance".toHttpUrl()))
    }

    @Test
    fun `isPublicPath does NOT match root or empty path`() {
        assertFalse(interceptor.isPublicPath("http://10.0.2.2:8080/".toHttpUrl()))
        assertFalse(interceptor.isPublicPath("http://10.0.2.2:8080".toHttpUrl()))
    }

    @Test
    fun `public paths are forwarded without Authorization header`() = runTest {
        val chain = mockChain(
            url = "http://10.0.2.2:8080/api/auth/sign-in/email".toHttpUrl(),
            responseBody = "ok"
        )

        interceptor.intercept(chain)

        val forwarded = argumentCaptor<Request>().apply {
            verify(chain).proceed(capture())
        }.firstValue
        assertNull("Public path must not carry an Authorization header", forwarded.header("Authorization"))
        verify(tokenManager, never()).getAccessTokenSync()
    }

    @Test
    fun `non-public path gets Bearer token from cache`() = runTest {
        whenever(tokenManager.getAccessTokenSync()).thenReturn("access-1")
        val chain = mockChain(
            url = "http://10.0.2.2:8080/api/wallet/balance".toHttpUrl(),
            responseBody = "{}"
        )

        interceptor.intercept(chain)

        val forwarded = argumentCaptor<Request>().apply {
            verify(chain).proceed(capture())
        }.firstValue
        assertEquals("Bearer access-1", forwarded.header("Authorization"))
    }

    private fun mockChain(
        url: HttpUrl,
        responseBody: String
    ): Interceptor.Chain {
        val request = Request.Builder().url(url).build()
        val response = Response.Builder()
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .code(200)
            .message("OK")
            .body(responseBody.toResponseBody())
            .build()
        val chain = mock<Interceptor.Chain>()
        whenever(chain.request()).thenReturn(request)
        whenever(chain.proceed(any())).thenReturn(response)
        return chain
    }
}
