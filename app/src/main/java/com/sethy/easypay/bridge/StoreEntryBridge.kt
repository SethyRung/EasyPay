package com.sethy.easypay.bridge

import com.sethy.easypay.data.api.WalletApi
import com.sethy.easypay.di.GlitchHost
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StoreEntryBridge @Inject constructor(
    private val walletApi: WalletApi,
    @param:GlitchHost private val glitchHost: String,
) {

    suspend fun openStore(): Result<String> {
        return Result.success(glitchHost)
    }
}
