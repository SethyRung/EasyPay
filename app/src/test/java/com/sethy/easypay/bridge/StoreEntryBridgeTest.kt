package com.sethy.easypay.bridge

import com.sethy.easypay.data.api.WalletApi
import com.sethy.easypay.data.dto.ApiResponse
import com.sethy.easypay.data.dto.BridgeIssueData
import com.sethy.easypay.data.dto.Status
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.whenever
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class StoreEntryBridgeTest {

    private val glitchHost = "https://glitch.example.com"
    private val cookieString =
        "session=eyJhbGciOiJIUzI1NiJ9.payload.signature; Path=/; HttpOnly; SameSite=Lax"

    private fun createBridge(
        api: WalletApi = mock(),
        store: WebViewCookieStore = mock(),
        host: String = glitchHost,
    ): StoreEntryBridge = StoreEntryBridge(api, host, store)

    private fun successResponse(data: BridgeIssueData): ApiResponse<BridgeIssueData> =
        ApiResponse(
            status = Status(
                code = "SUCCESS",
                message = "ok",
                requestId = "req-test",
                requestTime = 0L
            ),
            data = data
        )

    @Test
    fun openStore_happyPath_writesCookieAndReturnsGlitchHost() = runTest {
        val api: WalletApi = mock()
        val store: WebViewCookieStore = mock()
        whenever(api.bridgeIssue()).thenReturn(successResponse(BridgeIssueData(cookie = cookieString)))

        val result = createBridge(api, store).openStore()

        assertTrue("expected success, got $result", result.isSuccess)
        assertEquals(glitchHost, result.getOrNull())
        verify(store).set(glitchHost, cookieString)
    }

    @Test
    fun openStore_httpException401_returnsFailure_noCookieWritten() = runTest {
        val api: WalletApi = mock()
        val store: WebViewCookieStore = mock()
        val errorBody = "Unauthorized".toResponseBody("application/json".toMediaType())
        whenever(api.bridgeIssue()).thenThrow(
            HttpException(Response.error<Any>(401, errorBody))
        )

        val result = createBridge(api, store).openStore()

        assertTrue("expected failure, got $result", result.isFailure)
        verifyNoInteractions(store)
    }

    @Test
    fun openStore_httpException503_returnsFailure_noCookieWritten() = runTest {
        val api: WalletApi = mock()
        val store: WebViewCookieStore = mock()
        val errorBody = "Glitch unreachable".toResponseBody("application/json".toMediaType())
        whenever(api.bridgeIssue()).thenThrow(
            HttpException(Response.error<Any>(503, errorBody))
        )

        val result = createBridge(api, store).openStore()

        assertTrue("expected failure, got $result", result.isFailure)
        verifyNoInteractions(store)
    }

    @Test
    fun openStore_failureEnvelope_returnsFailure_noCookieWritten() = runTest {
        val api: WalletApi = mock()
        val store: WebViewCookieStore = mock()
        whenever(api.bridgeIssue()).thenReturn(
            ApiResponse(
                status = Status(
                    code = "FAILURE",
                    message = "bridge disabled",
                    requestId = "req-fail",
                    requestTime = 0L
                ),
                data = null
            )
        )

        val result = createBridge(api, store).openStore()

        assertTrue("expected failure, got $result", result.isFailure)
        verifyNoInteractions(store)
    }

    @Test
    fun openStore_emptyCookie_returnsFailure_noCookieWritten() = runTest {
        val api: WalletApi = mock()
        val store: WebViewCookieStore = mock()
        whenever(api.bridgeIssue()).thenReturn(successResponse(BridgeIssueData(cookie = "")))

        val result = createBridge(api, store).openStore()

        assertTrue("expected failure, got $result", result.isFailure)
        verifyNoInteractions(store)
    }

    @Test
    fun openStore_blankCookie_returnsFailure_noCookieWritten() = runTest {
        val api: WalletApi = mock()
        val store: WebViewCookieStore = mock()
        whenever(api.bridgeIssue()).thenReturn(successResponse(BridgeIssueData(cookie = "   ")))

        val result = createBridge(api, store).openStore()

        assertTrue("expected failure, got $result", result.isFailure)
        verifyNoInteractions(store)
    }

    @Test
    fun openStore_networkIoException_returnsFailure_noCookieWritten() = runTest {
        val api: WalletApi = mock()
        val store: WebViewCookieStore = mock()
        whenever(api.bridgeIssue()).thenThrow(
            RuntimeException("network down", IOException("connection refused"))
        )

        val result = createBridge(api, store).openStore()

        assertTrue("expected failure, got $result", result.isFailure)
        verifyNoInteractions(store)
    }
}
