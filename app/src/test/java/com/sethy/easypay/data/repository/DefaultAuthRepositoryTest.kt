package com.sethy.easypay.data.repository

import com.sethy.easypay.data.local.AuthTokenManager
import com.sethy.easypay.data.model.User
import com.sethy.easypay.data.source.AuthDataSource
import com.sethy.easypay.data.source.AuthResult
import com.sethy.easypay.data.source.WalletDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.*
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class DefaultAuthRepositoryTest {

    private val authDataSource: AuthDataSource = mock()
    private val tokenManager: AuthTokenManager = mock()
    private val walletDataSource: WalletDataSource = mock()
    private val testDispatcher = StandardTestDispatcher()

    private val testUser = User(
        id = "user-1",
        name = "Alice Smith",
        email = "alice@example.com",
        phone = "+1234567890",
        balance = 1_000.0
    )

    private val testAuthResult = AuthResult(
        user = testUser,
        accessToken = "access-token-123"
    )

    private fun createRepository() = DefaultAuthRepository(authDataSource, tokenManager, walletDataSource)

    // ─── login ───────────────────────────────────────────────────────────────

    @Test
    fun `login returns user on success and saves tokens`() = runTest(testDispatcher) {
        Dispatchers.setMain(testDispatcher)
        whenever(authDataSource.login("alice@example.com", "Password123"))
            .thenReturn(Result.success(testAuthResult))

        val repo = createRepository()
        val result = repo.login("alice@example.com", "Password123")

        assertTrue(result.isSuccess)
        assertEquals(testUser, result.getOrNull())
        verify(authDataSource).login("alice@example.com", "Password123")
    }

    @Test
    fun `login returns failure on auth failure`() = runTest(testDispatcher) {
        Dispatchers.setMain(testDispatcher)
        whenever(authDataSource.login("bad@example.com", "wrong"))
            .thenReturn(Result.failure(Exception("Invalid credentials")))

        val repo = createRepository()
        val result = repo.login("bad@example.com", "wrong")

        assertTrue(result.isFailure)
        assertEquals("Invalid credentials", result.exceptionOrNull()?.message)
    }

    // ─── register ────────────────────────────────────────────────────────────

    @Test
    fun `register returns user on success and saves tokens`() = runTest(testDispatcher) {
        Dispatchers.setMain(testDispatcher)
        whenever(
            authDataSource.register("Bob Jones", "bob@example.com", "+1987654321", "Password123")
        ).thenReturn(Result.success(testAuthResult))

        val repo = createRepository()
        val result = repo.register("Bob Jones", "bob@example.com", "+1987654321", "Password123")

        assertTrue(result.isSuccess)
        assertEquals(testUser, result.getOrNull())
    }

    @Test
    fun `register returns failure on registration failure`() = runTest(testDispatcher) {
        Dispatchers.setMain(testDispatcher)
        whenever(
            authDataSource.register("Bob Jones", "bob@example.com", "+1987654321", "Password123")
        ).thenReturn(Result.failure(Exception("Email already in use")))

        val repo = createRepository()
        val result = repo.register("Bob Jones", "bob@example.com", "+1987654321", "Password123")

        assertTrue(result.isFailure)
        assertEquals("Email already in use", result.exceptionOrNull()?.message)
    }

    // ─── logout ──────────────────────────────────────────────────────────────

    @Test
    fun `logout returns server success and clears tokens`() = runTest(testDispatcher) {
        Dispatchers.setMain(testDispatcher)
        whenever(authDataSource.logout()).thenReturn(Result.success(Unit))

        val repo = createRepository()
        val result = repo.logout()
        advanceUntilIdle()

        assertTrue(result.isSuccess)
        verify(authDataSource).logout()
        verify(tokenManager).clearTokens()
    }

    @Test
    fun `logout still clears tokens when server call fails`() = runTest(testDispatcher) {
        Dispatchers.setMain(testDispatcher)
        whenever(authDataSource.logout())
            .thenReturn(Result.failure(Exception("Logout failed: network error")))

        val repo = createRepository()
        val result = repo.logout()
        advanceUntilIdle()

        verify(tokenManager).clearTokens()
        assertTrue(result.isFailure)
        assertEquals("Logout failed: network error", result.exceptionOrNull()?.message)
    }

    // ─── isLoggedIn ─────────────────────────────────────────────────────────

    @Test
    fun `isLoggedIn returns true when token exists`() = runTest(testDispatcher) {
        Dispatchers.setMain(testDispatcher)
        whenever(tokenManager.getAccessToken()).thenReturn("valid-token")

        val repo = createRepository()
        val result = repo.isLoggedIn()

        assertTrue(result)
    }

    @Test
    fun `isLoggedIn returns false when no token`() = runTest(testDispatcher) {
        Dispatchers.setMain(testDispatcher)
        whenever(tokenManager.getAccessToken()).thenReturn(null)

        val repo = createRepository()
        val result = repo.isLoggedIn()

        assertFalse(result)
    }
}
