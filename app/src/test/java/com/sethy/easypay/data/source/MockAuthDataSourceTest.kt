package com.sethy.easypay.data.source

import com.sethy.easypay.data.model.User
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MockAuthDataSourceTest {

    private val seededUser = User(
        id = "1",
        name = "Samantha Doe",
        email = "samantha@example.com",
        balance = 4590.00
    )

    private fun createDataSource() = MockAuthDataSource(seed = { seededUser })

    @Test
    fun `login succeeds with seeded demo credentials`() = runTest {
        val ds = createDataSource()

        val result = ds.login("samantha@example.com", MockAuthDataSource.DEMO_PASSWORD)

        assertTrue("expected success, got $result", result.isSuccess)
        val auth = result.getOrThrow()
        assertEquals(seededUser, auth.user)
        assertEquals("mock_access_token", auth.accessToken)
    }

    @Test
    fun `login is case-insensitive on email`() = runTest {
        val ds = createDataSource()

        val result = ds.login("Samantha@Example.COM", MockAuthDataSource.DEMO_PASSWORD)

        assertTrue("expected success, got $result", result.isSuccess)
    }

    @Test
    fun `login rejects unknown email with InvalidCredentialsException`() = runTest {
        val ds = createDataSource()

        val result = ds.login("ghost@example.com", MockAuthDataSource.DEMO_PASSWORD)

        assertTrue("expected failure, got $result", result.isFailure)
        assertTrue(result.exceptionOrNull() is InvalidCredentialsException)
        assertEquals("Invalid email or password", result.exceptionOrNull()?.message)
    }

    @Test
    fun `login rejects wrong password with InvalidCredentialsException`() = runTest {
        val ds = createDataSource()

        val result = ds.login("samantha@example.com", "wrong-password")

        assertTrue("expected failure, got $result", result.isFailure)
        assertTrue(result.exceptionOrNull() is InvalidCredentialsException)
        // Same message as unknown email — callers must not be able to
        // distinguish the two failure modes.
        assertEquals("Invalid email or password", result.exceptionOrNull()?.message)
    }

    @Test
    fun `login rejects blank email`() = runTest {
        val ds = createDataSource()

        val result = ds.login("", MockAuthDataSource.DEMO_PASSWORD)

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalArgumentException)
    }

    @Test
    fun `login rejects blank password`() = runTest {
        val ds = createDataSource()

        val result = ds.login("samantha@example.com", "")

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalArgumentException)
    }

    @Test
    fun `register succeeds and the new account can log in`() = runTest {
        val ds = createDataSource()

        val regResult = ds.register(
            name = "Alice Smith",
            email = "alice@example.com",
            phone = "+1234567890",
            password = "Password123"
        )

        assertTrue("expected success, got $regResult", regResult.isSuccess)
        val registered = regResult.getOrThrow().user
        assertEquals("alice@example.com", registered.email)
        assertEquals("Alice Smith", registered.name)

        val loginResult = ds.login("alice@example.com", "Password123")
        assertTrue("newly registered account should be able to log in", loginResult.isSuccess)
        assertEquals(registered, loginResult.getOrThrow().user)
    }

    @Test
    fun `register rejects duplicate email with UserAlreadyExistsException`() = runTest {
        val ds = createDataSource()

        val result = ds.register(
            name = "Imposter",
            email = "samantha@example.com",
            phone = "",
            password = "Password123"
        )

        assertTrue("expected failure, got $result", result.isFailure)
        assertTrue(result.exceptionOrNull() is UserAlreadyExistsException)
    }

    @Test
    fun `register is case-insensitive when checking duplicate email`() = runTest {
        val ds = createDataSource()

        val result = ds.register(
            name = "Imposter",
            email = "SAMANTHA@Example.com",
            phone = "",
            password = "Password123"
        )

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is UserAlreadyExistsException)
    }

    @Test
    fun `register rejects blank required fields`() = runTest {
        val ds = createDataSource()

        val result = ds.register(name = "", email = "x@example.com", phone = "", password = "Password123")

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalArgumentException)
    }

    @Test
    fun `logout always succeeds`() = runTest {
        val ds = createDataSource()
        assertTrue(ds.logout().isSuccess)
    }

    @Test
    fun `refreshToken returns the seeded demo user`() = runTest {
        val ds = createDataSource()

        val result = ds.refreshToken("any-token")

        assertTrue(result.isSuccess)
        assertEquals(seededUser, result.getOrThrow().user)
    }
}