package com.sethy.easypay.data.api

import com.sethy.easypay.data.dto.ApiResponse
import com.sethy.easypay.data.dto.BalanceResponse
import com.sethy.easypay.data.dto.BridgeIssueResponse
import com.sethy.easypay.data.dto.BillPaymentRequest
import com.sethy.easypay.data.dto.BillPaymentResponse
import com.sethy.easypay.data.dto.SendMoneyRequest
import com.sethy.easypay.data.dto.TopUpRequest
import com.sethy.easypay.data.dto.TopUpResponse
import com.sethy.easypay.data.dto.TransactionResponse
import com.sethy.easypay.data.dto.TransactionsListResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface WalletApi {
    @GET("wallet/balance")
    suspend fun getBalance(): ApiResponse<BalanceResponse>

    @GET("wallet/transactions")
    suspend fun getTransactions(
        @Query("limit") limit: Int = 10,
        @Query("offset") offset: Int = 0
    ): ApiResponse<TransactionsListResponse>

    @GET("wallet/transactions/{id}")
    suspend fun getTransaction(@Path("id") id: String): ApiResponse<TransactionResponse>

    @POST("wallet/send")
    suspend fun sendMoney(@Body request: SendMoneyRequest): ApiResponse<TransactionResponse>

    @POST("payments/bill")
    suspend fun payBill(@Body request: BillPaymentRequest): ApiResponse<BillPaymentResponse>

    @POST("wallet/topup")
    suspend fun topUp(@Body request: TopUpRequest): ApiResponse<TopUpResponse>

    @POST("auth/bridge-issue")
    suspend fun bridgeIssue(): BridgeIssueResponse
}
