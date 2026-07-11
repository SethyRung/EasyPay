package com.sethy.easypay.bridge

import com.sethy.easypay.data.api.WalletApi
import com.sethy.easypay.di.GlitchHost
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StoreEntryBridge @Inject constructor(
    private val walletApi: WalletApi,
    @param:GlitchHost private val glitchHost: String,
    private val cookieStore: WebViewCookieStore,
) {

    suspend fun openStore(): Result<String> = runCatching {
        val response = walletApi.bridgeIssue()
        val cookie = response.cookie?.takeIf { it.isNotBlank() }
            ?: error("Bridge refused: empty cookie in response")
        cookieStore.set(glitchHost, cookie)
        glitchHost
    }
}