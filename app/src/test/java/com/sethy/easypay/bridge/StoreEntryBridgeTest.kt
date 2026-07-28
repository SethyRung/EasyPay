package com.sethy.easypay.bridge

import com.sethy.easypay.data.api.WalletApi
import com.sethy.easypay.data.dto.ApiResponse
import com.sethy.easypay.data.dto.BridgeIssueData
import com.sethy.easypay.data.dto.Meta
import com.sethy.easypay.data.dto.Status
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.net.URLEncoder

class StoreEntryBridgeTest {

    private val glitchHost = "https://glitch.example.com"

    private fun createBridge(
        api: WalletApi = mock(),
        host: String = glitchHost,
    ): StoreEntryBridge = StoreEntryBridge(api, host)

    private fun envelope(ticket: String) = ApiResponse(
        status = Status(code = "OK", message = "ok", requestId = "req-1", requestTime = 0L),
        data = BridgeIssueData(ticket = ticket),
        meta = Meta(total = 0, limit = 0, offset = 0),
    )

    @Test
    fun openStore_returns_consume_handle_pointing_at_bridge_consume() = runTest {
        val api: WalletApi = mock()
        whenever(api.bridgeIssue()).thenReturn(envelope("abc"))

        val result = createBridge(api).openStore()

        assertTrue("expected success, got $result", result.isSuccess)
        val handle = result.getOrNull()
        assertEquals("$glitchHost/api/bridge/consume", handle?.consumeUrl)
        assertEquals("ticket=abc", String(handle?.postBody ?: ByteArray(0)))
        verify(api).bridgeIssue()
    }

    @Test
    fun openStore_url_encodes_special_characters_in_ticket() = runTest {
        val api: WalletApi = mock()
        val raw = "abc/def+def="
        whenever(api.bridgeIssue()).thenReturn(envelope(raw))

        val handle = createBridge(api).openStore().getOrThrow()

        val expected = "ticket=" + URLEncoder.encode(raw, "UTF-8")
        assertEquals(expected, String(handle.postBody))
        // Regression guard: assert the body was not double-encoded (which would
        // turn '%' into '%25' inside the encoded value).
        val doubleEncoded =
            raw.any { ch -> ch.code > 0x7F || ch in "/+=" } &&
                String(handle.postBody).contains("%25")
        assertFalse("body must not be double-URL-encoded", doubleEncoded)
    }

    @Test
    fun openStore_returns_failure_when_data_is_null() = runTest {
        val api: WalletApi = mock()
        whenever(api.bridgeIssue()).thenReturn(
            ApiResponse(
                status = Status(code = "OK", message = "ok", requestId = "req-2", requestTime = 0L),
                data = null,
                meta = null,
            )
        )

        val result = createBridge(api).openStore()

        assertTrue("expected failure when envelope data is null", result.isFailure)
    }
}