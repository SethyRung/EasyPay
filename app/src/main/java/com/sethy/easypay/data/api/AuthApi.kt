package com.sethy.easypay.data.api

import com.sethy.easypay.data.dto.ApiResponse
import com.sethy.easypay.data.dto.AuthResponse
import com.sethy.easypay.data.dto.LoginRequest
import com.sethy.easypay.data.dto.RefreshTokenRequest
import com.sethy.easypay.data.dto.RegisterRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): ApiResponse<AuthResponse>

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): ApiResponse<AuthResponse>

    @POST("auth/refresh")
    suspend fun refreshToken(@Body request: RefreshTokenRequest): ApiResponse<AuthResponse>

    @POST("auth/logout")
    suspend fun logout()
}
