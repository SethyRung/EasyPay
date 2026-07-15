package com.sethy.easypay.data.source

import com.sethy.easypay.data.model.User

interface AuthDataSource {
    suspend fun login(email: String, password: String): Result<AuthResult>
    suspend fun register(name: String, email: String, phone: String, password: String): Result<AuthResult>
    suspend fun logout(): Result<Unit>
    suspend fun getSession(): Result<User>
}
