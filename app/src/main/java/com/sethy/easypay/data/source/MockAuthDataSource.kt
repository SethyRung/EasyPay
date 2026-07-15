package com.sethy.easypay.data.source

import com.sethy.easypay.data.model.User
import java.util.UUID
import javax.inject.Singleton

/**
 * Demo credentials: the seeded user from `assets/data/user.json` with
 * password [DEMO_PASSWORD] — the only credentials guaranteed to succeed on
 * a fresh install. The seed function is shared with `MockWalletDataSource`,
 * so the demo user you sign in as is also the user shown on the home
 * screen.
 */
@Singleton
class MockAuthDataSource(
    private val seed: suspend () -> User
) : AuthDataSource {

    private data class StoredUser(val user: User, val password: String)

    private val lock = Any()
    private val users = mutableMapOf<String, StoredUser>()
    @Volatile private var loaded = false

    private suspend fun ensureLoaded() {
        if (loaded) return
        // Hoist the suspend call out of `synchronized` — Kotlin forbids
        // suspension points inside critical sections. Safe under
        // double-checked locking: `loaded` is volatile and the second
        // arrival sees `loaded = true` and exits.
        val demo = seed()
        synchronized(lock) {
            if (loaded) return
            users[demo.email.lowercase()] = StoredUser(demo, DEMO_PASSWORD)
            loaded = true
        }
    }

    override suspend fun login(email: String, password: String): Result<AuthResult> {
        if (email.isBlank() || password.isBlank()) {
            return Result.failure(IllegalArgumentException("Email and password are required"))
        }
        ensureLoaded()
        val stored = synchronized(lock) { users[email.lowercase()] }
        // Same message for unknown email and wrong password so callers can't
        // enumerate registered emails.
        return if (stored != null && stored.password == password) {
            Result.success(
                AuthResult(
                    user = stored.user,
                    accessToken = "mock_access_token"
                )
            )
        } else {
            Result.failure(InvalidCredentialsException())
        }
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
        ensureLoaded()
        val key = email.lowercase()
        return synchronized(lock) {
            if (users.containsKey(key)) {
                Result.failure(UserAlreadyExistsException())
            } else {
                val user = User(
                    id = UUID.randomUUID().toString(),
                    name = name,
                    email = email,
                    phone = phone.ifBlank { null },
                    balance = 0.0
                )
                users[key] = StoredUser(user, password)
                Result.success(
                    AuthResult(
                        user = user,
                        accessToken = "mock_access_token"
                    )
                )
            }
        }
    }

    override suspend fun logout(): Result<Unit> = Result.success(Unit)

    companion object {
        /** Password for the demo account seeded from `assets/data/user.json`. */
        const val DEMO_PASSWORD = "password123"
    }
}
