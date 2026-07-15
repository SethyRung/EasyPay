package com.sethy.easypay.ui.viewmodel

import com.sethy.easypay.data.auth.AuthSessionNotifier
import com.sethy.easypay.data.local.AuthTokenManager
import com.sethy.easypay.data.local.OnboardingPreferences
import com.sethy.easypay.data.model.User
import com.sethy.easypay.data.repository.AuthRepository
import com.sethy.easypay.navigation.AppSessionViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class AppSessionViewModelTest {

    private val authRepository: AuthRepository = mock()
    private val tokenManager: AuthTokenManager = mock()
    private val authSessionNotifier = AuthSessionNotifier()
    private val onboardingPreferences: OnboardingPreferences = mock()
    private val testDispatcher = StandardTestDispatcher()

    private val stubUser = User(
        id = "user-1",
        name = "Alice Smith",
        email = "alice@example.com",
        balance = 1_000.0
    )

    private fun createViewModel() = AppSessionViewModel(
        authRepository = authRepository,
        tokenManager = tokenManager,
        authSessionNotifier = authSessionNotifier,
        onboardingPreferences = onboardingPreferences
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        runBlocking { whenever(onboardingPreferences.hasSeenOnboarding()).thenReturn(true) }
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun refreshSession_success_sets_isAuthenticated_true() = runTest(testDispatcher) {
        whenever(authRepository.refreshSession()).thenReturn(Result.success(stubUser))

        val vm = createViewModel()
        advanceUntilIdle()

        assertEquals(true, vm.isAuthenticated.value)
    }

    @Test
    fun refreshSession_failure_clearsTokens_and_sets_isAuthenticated_false() = runTest(testDispatcher) {
        whenever(authRepository.refreshSession()).thenReturn(Result.failure(Exception("session expired")))

        val vm = createViewModel()
        advanceUntilIdle()

        verify(tokenManager).clearTokens()
        assertEquals(false, vm.isAuthenticated.value)
    }

    @Test
    fun notifier_event_clearsTokens_and_sets_isAuthenticated_false() = runTest(testDispatcher) {
        whenever(authRepository.refreshSession()).thenReturn(Result.success(stubUser))

        val vm = createViewModel()
        advanceUntilIdle()

        authSessionNotifier.notifyExpired()
        advanceUntilIdle()

        verify(tokenManager).clearTokens()
        assertEquals(false, vm.isAuthenticated.value)
    }
}
