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
data class AuthResponse(
    val user: UserDto,
    val token: String,
    val redirect: Boolean? = null
)

@Serializable
data class UserDto(
    val id: String,
    val email: String,
    val phone: String?,
    val name: String,
    val createdAt: String? = null,
    val emailVerified: Boolean = false,
    val image: String? = null,
    val updatedAt: String? = null
)

@Serializable
data class SessionDto(
    val id: String,
    val userId: String,
    val token: String,
    val expiresAt: String,
    val ipAddress: String? = null,
    val userAgent: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null
)

@Serializable
data class SessionResponse(
    val user: UserDto,
    val session: SessionDto
)
