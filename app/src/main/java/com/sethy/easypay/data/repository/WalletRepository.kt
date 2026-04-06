package com.sethy.easypay.data.repository

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.sethy.easypay.data.api.WalletApi
import com.sethy.easypay.data.dto.BalanceResponse
import com.sethy.easypay.data.dto.TransactionResponse
import com.sethy.easypay.data.dto.TransactionsListResponse
import com.sethy.easypay.data.local.AuthTokenManager
import com.sethy.easypay.data.model.Transaction
import com.sethy.easypay.data.model.TransactionType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

class WalletRepository(
    private val tokenManager: AuthTokenManager
) {
    private val walletApi by lazy {
        val authInterceptor = okhttp3.Interceptor { chain ->
            val token = runBlocking {
                tokenManager.getAccessToken()
            }
            val originalRequest = chain.request()
            val newRequest = originalRequest.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
            chain.proceed(newRequest)
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()

        val json = Json {
            ignoreUnknownKeys = true
            coerceInputValues = true
        }

        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:3001/api/")
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .client(client)
            .build()

        retrofit.create(WalletApi::class.java)
    }

    suspend fun getBalance(): Result<Double> = withContext(Dispatchers.IO) {
        return@withContext try {
            val response = walletApi.getBalance()
            if (response.status.code == "SUCCESS") {
                Result.success(response.data!!.balance)
            } else {
                Result.failure(Exception(response.status.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getTransactions(limit: Int = 10, offset: Int = 0): Result<List<Transaction>> = withContext(Dispatchers.IO) {
        return@withContext try {
            val response = walletApi.getTransactions(limit, offset)
            if (response.status.code == "SUCCESS") {
                val transactions = response.data!!.transactions.map { tx ->
                    Transaction(
                        id = tx.id,
                        recipientName = tx.description,
                        amount = tx.amount,
                        type = if (tx.type == "credit") TransactionType.RECEIVED else TransactionType.SENT,
                        timestamp = System.currentTimeMillis(),
                        avatarUrl = null
                    )
                }
                Result.success(transactions)
            } else {
                Result.failure(Exception(response.status.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
