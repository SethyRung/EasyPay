package com.sethy.easypay.domain.usecase

import com.sethy.easypay.data.model.User
import com.sethy.easypay.data.repository.AuthRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class RegisterUseCaseTest {

    private val authRepository: AuthRepository = mock()
    private val registerUseCase = RegisterUseCase(authRepository)

    private val testUser = User(
        id = "user-2",
        name = "Bob Jones",
        email = "bob@example.com",
        phone = "+1987654321",
        balance = 0.0
    )

    @Test
    fun `invoke returns success when registration succeeds`() = runTest {
        whenever(
            authRepository.register("Bob Jones", "bob@example.com", "+1987654321", "Password1")
        ).thenReturn(Result.success(testUser))

        val result = registerUseCase("Bob Jones", "bob@example.com", "+1987654321", "Password1")

        assertTrue(result.isSuccess)
        assertEquals(testUser, result.getOrNull())
    }

    @Test
    fun `invoke returns failure when registration fails`() = runTest {
        val exception = Exception("Email already in use")
        whenever(
            authRepository.register("Bob Jones", "bob@example.com", "+1987654321", "Password1")
        ).thenReturn(Result.failure(exception))

        val result = registerUseCase("Bob Jones", "bob@example.com", "+1987654321", "Password1")

        assertTrue(result.isFailure)
        assertEquals("Email already in use", result.exceptionOrNull()?.message)
    }
}
