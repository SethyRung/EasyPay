package com.sethy.easypay.data.api

import com.sethy.easypay.data.dto.ApiResponse
import com.sethy.easypay.data.dto.BalanceResponse
import com.sethy.easypay.data.dto.TransactionsListResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WalletApi {
    @GET("wallet/balance")
    suspend fun getBalance(): ApiResponse<BalanceResponse>

    @GET("wallet/transactions")
    suspend fun getTransactions(
        @Query("limit") limit: Int = 10,
        @Query("offset") offset: Int = 0
    ): ApiResponse<TransactionsListResponse>
}
