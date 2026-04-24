package com.sethy.easypay.data.repository

import com.sethy.easypay.data.dto.ApiResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

abstract class BaseRepository {

    protected suspend fun <T> safeApiCall(
        apiCall: suspend () -> ApiResponse<T>
    ): Result<T> = withContext(Dispatchers.IO) {
        try {
            val response = apiCall()
            if (response.status.code == "SUCCESS") {
                val data = response.data
                if (data != null) {
                    Result.success(data)
                } else {
                    Result.failure(ApiException(response.status.message, response.status.code))
                }
            } else {
                Result.failure(ApiException(response.status.message, response.status.code))
            }
        } catch (e: IOException) {
            Result.failure(NetworkException("No internet connection. Please check your network.", e))
        } catch (e: Exception) {
            Result.failure(UnexpectedException(e.message ?: "An unexpected error occurred", e))
        }
    }

    class ApiException(
        message: String,
        val code: String
    ) : Exception(message)

    class NetworkException(
        message: String,
        cause: Throwable? = null
    ) : Exception(message, cause)

    class UnexpectedException(
        message: String,
        cause: Throwable? = null
    ) : Exception(message, cause)
}
