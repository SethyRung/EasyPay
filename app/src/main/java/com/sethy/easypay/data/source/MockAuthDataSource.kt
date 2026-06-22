package com.sethy.easypay.data.source

import com.sethy.easypay.data.model.User
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MockAuthDataSource @Inject constructor() : AuthDataSource {

    override suspend fun login(email: String, password: String): Result<AuthResult> {
        if (email.isBlank() || password.isBlank()) {
            return Result.failure(IllegalArgumentException("Email and password are required"))
        }
        return Result.success(
            AuthResult(
                user = fakeUser(email),
                accessToken = "mock_access_token",
                refreshToken = "mock_refresh_token"
            )
        )
    }

    override suspend fun register(
        name: String,
        email: String,
        phone: String,
        password: String
    ): Result<AuthResult> {
        if (name.isBlank() || email.isBlank() || password.isBlank()) {
            return Result.failure(IllegalArgumentException("All required fields must be filled"))
        }
        return Result.success(
            AuthResult(
                user = User(
                    id = "1",
                    name = name,
                    email = email,
                    phone = phone.ifBlank { null },
                    balance = 0.0
                ),
                accessToken = "mock_access_token",
                refreshToken = "mock_refresh_token"
            )
        )
    }

    override suspend fun logout(): Result<Unit> = Result.success(Unit)

    override suspend fun refreshToken(refreshToken: String): Result<AuthResult> = Result.success(
        AuthResult(
            user = fakeUser("user@example.com"),
            accessToken = "mock_access_token",
            refreshToken = "mock_refresh_token"
        )
    )

    private fun fakeUser(email: String): User {
        val name = email.substringBefore("@").replace(".", " ").replaceFirstChar {
            if (it.isLowerCase()) it.titlecase() else it.toString()
        }
        return User(
            id = "1",
            name = name,
            email = email,
            phone = null,
            balance = 0.0
        )
    }
}
