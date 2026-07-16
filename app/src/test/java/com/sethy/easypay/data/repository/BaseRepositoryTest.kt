package com.sethy.easypay.data.repository

import com.sethy.easypay.data.auth.AuthSessionNotifier
import com.sethy.easypay.data.dto.ApiResponse
import com.sethy.easypay.data.dto.Status
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class BaseRepositoryTest {

    // Test-only subclass of BaseRepository — exposes safeApiCall as a plain
    // suspend function so the test can drive it without depending on any of
    // the production data sources.
    private class TestableRepository(
        notifier: AuthSessionNotifier
    ) : BaseRepository(notifier) {
        suspend fun callIt(call: suspend () -> ApiResponse<String>): Result<String> =
            safeApiCall { call() }
    }

    private fun errorEnvelope(code: String): ApiResponse<String> = ApiResponse(
        status = Status(
            code = code,
            message = "message for $code",
            requestId = "req-1",
            requestTime = 0L
        ),
        data = null
    )

    private fun successEnvelope(data: String): ApiResponse<String> = ApiResponse(
        status = Status(
            code = "SUCCESS",
            message = "ok",
            requestId = "req-1",
            requestTime = 0L
        ),
        data = data
    )

    @Test
    fun `safeApiCall emits on AuthSessionNotifier when envelope is UNAUTHORIZED`() = runTest {
        val notifier = AuthSessionNotifier()
        val repo = TestableRepository(notifier)

        var emitted = false
        val job = launch { notifier.events.collect { emitted = true } }
        // Drain so the collector actually subscribes before notifyExpired fires
        // (safeApiCall runs on Dispatchers.IO, which is not this test scheduler).
        advanceUntilIdle()

        val result = repo.callIt { errorEnvelope("UNAUTHORIZED") }
        advanceUntilIdle()

        assertTrue("result should be failure", result.isFailure)
        assertTrue("notifier should emit on UNAUTHORIZED envelope", emitted)
        job.cancel()
    }

    @Test
    fun `safeApiCall does NOT emit when envelope is a non-UNAUTHORIZED failure`() = runTest {
        val notifier = AuthSessionNotifier()
        val repo = TestableRepository(notifier)

        var emitted = false
        val job = launch { notifier.events.collect { emitted = true } }
        advanceUntilIdle()

        val result = repo.callIt { errorEnvelope("VALIDATION_ERROR") }
        advanceUntilIdle()

        assertTrue("result should be failure", result.isFailure)
        assertFalse("notifier should not emit on non-UNAUTHORIZED envelopes", emitted)
        job.cancel()
    }

    @Test
    fun `safeApiCall does NOT emit on SUCCESS envelope`() = runTest {
        val notifier = AuthSessionNotifier()
        val repo = TestableRepository(notifier)

        var emitted = false
        val job = launch { notifier.events.collect { emitted = true } }
        advanceUntilIdle()

        val result = repo.callIt { successEnvelope("ok") }
        advanceUntilIdle()

        assertTrue(result.isSuccess)
        assertFalse("notifier should not emit on SUCCESS", emitted)
        job.cancel()
    }

    @Test
    fun `safeApiCall fails SUCCESS-with-null-data with a useful message instead of the status message`() = runTest {
        val notifier = AuthSessionNotifier()
        val repo = TestableRepository(notifier)

        val result = repo.callIt { errorEnvelope("SUCCESS") }
        advanceUntilIdle()

        assertTrue("expected failure for SUCCESS/null data, got $result", result.isFailure)
        val ex = result.exceptionOrNull()
        assertTrue("expected ApiException, got ${ex!!::class.simpleName}", ex is BaseRepository.ApiException)
        val message = ex.message.orEmpty()
        assertFalse(
            "fallback message must not echo the misleading backend 'Success' status text, got '$message'",
            message.equals("Success", ignoreCase = true)
        )
        assertTrue(
            "fallback message should mention 'no data', got '$message'",
            message.contains("no data", ignoreCase = true)
        )
    }
}
