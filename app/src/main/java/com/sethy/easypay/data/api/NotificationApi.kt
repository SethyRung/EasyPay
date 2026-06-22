package com.sethy.easypay.data.api

import com.sethy.easypay.data.dto.ApiResponse
import com.sethy.easypay.data.dto.NotificationResponse
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface NotificationApi {
    @GET("notifications")
    suspend fun getNotifications(): ApiResponse<List<NotificationResponse>>

    @POST("notifications/{id}/read")
    suspend fun markAsRead(@Path("id") id: String): ApiResponse<Unit>
}
