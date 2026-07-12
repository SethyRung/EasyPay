package com.sethy.easypay.data.api

import com.sethy.easypay.data.dto.ApiResponse
import com.sethy.easypay.data.dto.AuthResponse
import com.sethy.easypay.data.dto.RefreshTokenRequest
import com.sethy.easypay.data.dto.Status
import com.sethy.easypay.data.dto.UserDto
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
    fun `isPublicPath matches auth login under api prefix`() {
        assertTrue(interceptor.isPublicPath("http://10.0.2.2:8080/api/auth/login".toHttpUrl()))
    }

    @Test
    fun `isPublicPath matches auth register under api prefix`() {
        assertTrue(interceptor.isPublicPath("http://10.0.2.2:8080/api/auth/register".toHttpUrl()))
    }

    @Test
    fun `isPublicPath matches auth refresh under api prefix`() {
        assertTrue(interceptor.isPublicPath("http://10.0.2.2:8080/api/auth/refresh".toHttpUrl()))
    }

    @Test
    fun `isPublicPath matches public paths without api prefix`() {
        assertTrue(interceptor.isPublicPath("http://10.0.2.2:8080/auth/login".toHttpUrl()))
    }

    @Test
    fun `isPublicPath does NOT match lookalike auth_login-attempt`() {
        assertFalse(
            interceptor.isPublicPath("http://10.0.2.2:8080/api/auth/login-attempt".toHttpUrl())
        )
    }

    @Test
    fun `isPublicPath does NOT match wallet auth-login`() {
        assertFalse(
            interceptor.isPublicPath("http://10.0.2.2:8080/api/wallet/auth-login".toHttpUrl())
        )
    }

    @Test
    fun `isPublicPath does NOT match deeper auth refresh subpath`() {
        assertFalse(
            interceptor.isPublicPath("http://10.0.2.2:8080/api/auth/refresh/extra".toHttpUrl())
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
            url = "http://10.0.2.2:8080/api/auth/login".toHttpUrl(),
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
        whenever(tokenManager.isAccessTokenExpiredSync()).thenReturn(false)
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

    @Test
    fun `401 triggers refresh and retries with new token`() = runTest {
        var savedAccess: String? = "expired-access"
        var expired = true
        whenever(tokenManager.getAccessTokenSync()).thenAnswer { savedAccess }
        whenever(tokenManager.isAccessTokenExpiredSync()).thenAnswer { expired }
        whenever(tokenManager.getRefreshTokenSync()).thenReturn("refresh-1")
        whenever(tokenManager.saveTokens(any(), any())).thenAnswer { invocation ->
            savedAccess = invocation.arguments[0] as String
            expired = false
        }
        whenever(tokenManager.setTokenExpiry(any())).thenAnswer { expired = false }

        val newAuth = AuthResponse(
            user = UserDto(id = "u1", email = "a@b.com", name = "A", phone = null),
            accessToken = "new-access",
            refreshToken = "new-refresh"
        )
        val apiResponse = ApiResponse(
            status = Status(code = "SUCCESS", message = "ok", requestId = "req-1", requestTime = 0L),
            data = newAuth
        )
        whenever(authApi.refreshToken(RefreshTokenRequest("refresh-1"))).thenReturn(apiResponse)

        val targetUrl = "http://10.0.2.2:8080/api/wallet/balance"
        val originalRequest = Request.Builder().url(targetUrl).build()
        val first = Response.Builder()
            .request(originalRequest)
            .protocol(Protocol.HTTP_1_1)
            .code(401)
            .message("Unauthorized")
            .body("".toResponseBody())
            .build()
        val second = Response.Builder()
            .request(originalRequest)
            .protocol(Protocol.HTTP_1_1)
            .code(200)
            .message("OK")
            .body("{}".toResponseBody())
            .build()

        val capturedRequests = mutableListOf<Request>()
        val chain = mock<Interceptor.Chain>()
        whenever(chain.request()).thenReturn(originalRequest)
        whenever(chain.proceed(any())).thenAnswer { invocation ->
            capturedRequests += invocation.arguments[0] as Request
            if (capturedRequests.size == 1) first else second
        }

        val response = interceptor.intercept(chain)

        assertEquals(200, response.code)
        assertEquals(2, capturedRequests.size)
        assertEquals("Bearer new-access", capturedRequests[1].header("Authorization"))
        verify(tokenManager).saveTokens("new-access", "new-refresh")
        // The double-check in performRefresh should suppress a second API
        // call once the cache reflects a non-expired token.
        verify(authApi).refreshToken(RefreshTokenRequest("refresh-1"))
    }

    @Test
    fun `401 with refresh failure clears tokens and returns 401`() = runTest {
        whenever(tokenManager.getAccessTokenSync()).thenReturn("expired-access")
        whenever(tokenManager.isAccessTokenExpiredSync()).thenReturn(true)
        whenever(tokenManager.getRefreshTokenSync()).thenReturn("refresh-1")
        whenever(authApi.refreshToken(RefreshTokenRequest("refresh-1")))
            .thenThrow(RuntimeException("network down"))

        val first = Response.Builder()
            .request(Request.Builder().url("http://10.0.2.2:8080/api/wallet/balance").build())
            .protocol(Protocol.HTTP_1_1)
            .code(401)
            .message("Unauthorized")
            .body("".toResponseBody())
            .build()
        val chain = mock<Interceptor.Chain>()
        whenever(chain.request()).thenReturn(
            Request.Builder().url("http://10.0.2.2:8080/api/wallet/balance").build()
        )
        whenever(chain.proceed(any())).thenReturn(first)

        val response = interceptor.intercept(chain)

        assertEquals(401, response.code)
        verify(tokenManager).clearTokensBlocking()
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
