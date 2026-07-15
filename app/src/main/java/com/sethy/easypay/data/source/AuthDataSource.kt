package com.sethy.easypay.data.source

interface AuthDataSource {
    suspend fun login(email: String, password: String): Result<AuthResult>
    suspend fun register(name: String, email: String, phone: String, password: String): Result<AuthResult>
    suspend fun logout(): Result<Unit>
}
