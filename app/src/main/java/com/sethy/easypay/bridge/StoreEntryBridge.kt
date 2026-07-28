package com.sethy.easypay.bridge

import com.sethy.easypay.data.api.WalletApi
import com.sethy.easypay.di.GlitchHost
import java.net.URLEncoder
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StoreEntryBridge @Inject constructor(
    private val walletApi: WalletApi,
    @param:GlitchHost private val glitchHost: String,
) {

    suspend fun openStore(): Result<BridgeConsumeHandle> = runCatching {
        val envelope = walletApi.bridgeIssue()
        val ticket = envelope.data?.ticket
            ?: throw IllegalStateException("Bridge issue response missing ticket")
        BridgeConsumeHandle(
            consumeUrl = "$glitchHost/api/bridge/consume",
            postBody = ("ticket=" + URLEncoder.encode(ticket, "UTF-8"))
                .toByteArray(Charsets.UTF_8),
        )
    }
}

data class BridgeConsumeHandle(
    val consumeUrl: String,
    val postBody: ByteArray,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is BridgeConsumeHandle) return false
        return consumeUrl == other.consumeUrl && postBody.contentEquals(other.postBody)
    }

    override fun hashCode(): Int {
        var result = consumeUrl.hashCode()
        result = 31 * result + postBody.contentHashCode()
        return result
    }
}