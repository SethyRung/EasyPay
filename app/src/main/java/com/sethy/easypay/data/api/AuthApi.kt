package com.sethy.easypay.data.api

import com.sethy.easypay.data.dto.ApiResponse
import com.sethy.easypay.data.dto.AuthResponse
import com.sethy.easypay.data.dto.LoginRequest
import com.sethy.easypay.data.dto.RegisterRequest
import com.sethy.easypay.data.dto.SessionResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AuthApi {
    @POST("auth/sign-in/email")
    suspend fun signIn(@Body request: LoginRequest): ApiResponse<AuthResponse>

    @POST("auth/sign-up/email")
    suspend fun signUp(@Body request: RegisterRequest): ApiResponse<AuthResponse>

    @POST("auth/sign-out")
    suspend fun signOut()

    @GET("auth/get-session")
    suspend fun getSession(): ApiResponse<SessionResponse>
}
