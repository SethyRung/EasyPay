package com.sethy.easypay.data.repository

import com.sethy.easypay.data.model.User

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<User>
    suspend fun register(name: String, email: String, phone: String, password: String): Result<User>
    suspend fun logout(): Result<Unit>
    suspend fun isLoggedIn(): Boolean
}
