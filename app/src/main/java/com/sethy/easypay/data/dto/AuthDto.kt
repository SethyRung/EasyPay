package com.sethy.easypay.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)

@Serializable
data class RegisterRequest(
    val email: String,
    val phone: String,
    val name: String,
    val password: String
)

@Serializable
data class RefreshTokenRequest(
    val refreshToken: String
)

@Serializable
data class AuthResponse(
    val user: UserDto,
    val accessToken: String,
    val refreshToken: String
)

@Serializable
data class UserDto(
    val id: String,
    val email: String,
    val phone: String?,
    val name: String,
    val createdAt: String? = null
)
