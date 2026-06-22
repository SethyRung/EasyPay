package com.sethy.easypay.domain.usecase

import com.sethy.easypay.data.model.User
import com.sethy.easypay.data.repository.AuthRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class LoginUseCaseTest {

    private val authRepository: AuthRepository = mock()
    private val loginUseCase = LoginUseCase(authRepository)

    private val testUser = User(
        id = "user-1",
        name = "Alice Smith",
        email = "alice@example.com",
        phone = "+1234567890",
        balance = 1_000.0
    )

    @Test
    fun `invoke returns success when login succeeds`() = runTest {
        whenever(authRepository.login("alice@example.com", "Password1"))
            .thenReturn(Result.success(testUser))

        val result = loginUseCase("alice@example.com", "Password1")

        assertTrue(result.isSuccess)
        assertEquals(testUser, result.getOrNull())
    }

    @Test
    fun `invoke returns failure when login fails`() = runTest {
        val exception = Exception("Invalid credentials")
        whenever(authRepository.login("bad@example.com", "wrong"))
            .thenReturn(Result.failure(exception))

        val result = loginUseCase("bad@example.com", "wrong")

        assertTrue(result.isFailure)
        assertEquals("Invalid credentials", result.exceptionOrNull()?.message)
    }
}
