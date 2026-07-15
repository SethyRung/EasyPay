package com.sethy.easypay.data.source

import com.sethy.easypay.data.api.AuthApi
import com.sethy.easypay.data.repository.BaseRepository
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class RemoteAuthDataSourceTest {

    private val authApi: AuthApi = mock()

    private fun createDataSource() = RemoteAuthDataSource(authApi)

    @Test
    fun `logout returns success on 2xx`() = runTest {
        whenever(authApi.signOut()).thenReturn(Unit)

        val result = createDataSource().logout()

        assertTrue(result.isSuccess)
    }

    @Test
    fun `logout returns failure with ApiException on HttpException`() = runTest {
        val errorBody = "Unauthorized".toResponseBody("application/json".toMediaType())
        whenever(authApi.signOut()).thenThrow(
            HttpException(Response.error<Any>(401, errorBody))
        )

        val result = createDataSource().logout()

        assertTrue("expected failure, got $result", result.isFailure)
        val ex = result.exceptionOrNull()
        assertNotNull(ex)
        assertTrue("expected ApiException, got ${ex!!::class.simpleName}", ex is BaseRepository.ApiException)
        assertEquals("HTTP_401", (ex as BaseRepository.ApiException).code)
    }

    @Test
    fun `logout returns failure with NetworkException on IOException`() = runTest {
        whenever(authApi.signOut()).thenAnswer { throw IOException("connection refused") }

        val result = createDataSource().logout()

        assertTrue("expected failure, got $result", result.isFailure)
        val ex = result.exceptionOrNull()
        assertNotNull(ex)
        assertTrue("expected NetworkException, got ${ex!!::class.simpleName}", ex is BaseRepository.NetworkException)
    }

    @Test
    fun `logout propagates unexpected exceptions instead of swallowing them`() = runTest {
        whenever(authApi.signOut()).thenThrow(IllegalStateException("boom"))

        val thrown = runCatching { createDataSource().logout() }

        assertTrue("expected exception to propagate", thrown.isFailure)
        assertTrue(thrown.exceptionOrNull() is IllegalStateException)
    }
}
