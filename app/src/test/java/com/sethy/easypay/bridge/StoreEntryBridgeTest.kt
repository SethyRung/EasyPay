package com.sethy.easypay.bridge

import com.sethy.easypay.data.api.WalletApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verifyNoInteractions

class StoreEntryBridgeTest {

    private val glitchHost = "https://glitch.example.com"

    private fun createBridge(
        api: WalletApi = mock(),
        host: String = glitchHost,
    ): StoreEntryBridge = StoreEntryBridge(api, host)

    @Test
    fun openStore_returnsSuccessWithHost() = runTest {
        val api: WalletApi = mock()

        val result = createBridge(api).openStore()

        assertTrue("expected success, got $result", result.isSuccess)
        assertEquals(glitchHost, result.getOrNull())
        verifyNoInteractions(api)
    }
}