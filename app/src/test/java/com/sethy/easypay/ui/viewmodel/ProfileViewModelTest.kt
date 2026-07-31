package com.sethy.easypay.ui.viewmodel

import app.cash.turbine.test
import com.sethy.easypay.data.model.User
import com.sethy.easypay.domain.usecase.GetCurrentUserUseCase
import com.sethy.easypay.domain.usecase.LogoutUseCase
import com.sethy.easypay.ui.state.ProfileEffect
import com.sethy.easypay.ui.state.ProfileEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private val testUser = User(
        id = "user-1",
        name = "Alice Smith",
        email = "alice@example.com",
        phone = "+1234567890",
        balance = 1_000.0
    )

    @Test
    fun initial_load_sets_isLoading_true_then_false_with_user() = runTest {
        val getCurrentUser: GetCurrentUserUseCase = mock()
        whenever(getCurrentUser()).thenReturn(Result.success(testUser))

        val vm = ProfileViewModel(getCurrentUser, mock())
        assertTrue(vm.state.value.isLoading)

        advanceUntilIdle()

        assertFalse(vm.state.value.isLoading)
        assertEquals(null, testUser, vm.state.value.user)
        assertNull(vm.state.value.errorMessage)
    }

    @Test
    fun load_sets_errorMessage_on_failure() = runTest {
        val getCurrentUser: GetCurrentUserUseCase = mock()
        whenever(getCurrentUser())
            .thenReturn(Result.failure(Exception("Failed to load profile")))

        val vm = ProfileViewModel(getCurrentUser, mock())
        advanceUntilIdle()

        assertFalse(vm.state.value.isLoading)
        assertNull(vm.state.value.user)
        assertEquals(null, "Failed to load profile", vm.state.value.errorMessage)
    }

    @Test
    fun LogoutClicked_sets_showLogoutDialog_to_true() = runTest {
        val getCurrentUser: GetCurrentUserUseCase = mock()
        whenever(getCurrentUser()).thenReturn(Result.success(testUser))

        val vm = ProfileViewModel(getCurrentUser, mock())
        advanceUntilIdle()

        vm.onEvent(ProfileEvent.LogoutClicked)

        assertTrue(vm.state.value.showLogoutDialog)
    }

    @Test
    fun DismissLogout_sets_showLogoutDialog_to_false() = runTest {
        val getCurrentUser: GetCurrentUserUseCase = mock()
        whenever(getCurrentUser()).thenReturn(Result.success(testUser))

        val vm = ProfileViewModel(getCurrentUser, mock())
        advanceUntilIdle()

        vm.onEvent(ProfileEvent.LogoutClicked)
        assertTrue(vm.state.value.showLogoutDialog)

        vm.onEvent(ProfileEvent.DismissLogout)
        assertFalse(vm.state.value.showLogoutDialog)
    }

    @Test
    fun ConfirmLogout_emits_NavigateToLogin() = runTest {
        val getCurrentUser: GetCurrentUserUseCase = mock()
        val logoutUseCase: LogoutUseCase = mock()
        whenever(getCurrentUser()).thenReturn(Result.success(testUser))
        whenever(logoutUseCase.invoke()).thenReturn(Result.success(Unit))

        val vm = ProfileViewModel(getCurrentUser, logoutUseCase)
        advanceUntilIdle()

        vm.effect.test {
            vm.onEvent(ProfileEvent.LogoutClicked)
            advanceUntilIdle()
            vm.onEvent(ProfileEvent.ConfirmLogout)
            advanceUntilIdle()
            assertEquals(ProfileEffect.NavigateToLogin, awaitItem())
        }
    }

    @Test
    fun ConfirmLogout_writes_error_to_state_when_server_logout_fails() = runTest {
        val getCurrentUser: GetCurrentUserUseCase = mock()
        val logoutUseCase: LogoutUseCase = mock()
        whenever(getCurrentUser()).thenReturn(Result.success(testUser))
        whenever(logoutUseCase.invoke()).thenReturn(
            Result.failure(Exception("Logout failed: network error"))
        )

        val vm = ProfileViewModel(getCurrentUser, logoutUseCase)
        advanceUntilIdle()

        vm.effect.test {
            vm.onEvent(ProfileEvent.ConfirmLogout)
            testScheduler.runCurrent()
            assertEquals("Logout failed: network error", vm.state.value.errorMessage)
            assertEquals(testUser, vm.state.value.user)
            advanceUntilIdle()
            assertEquals(ProfileEffect.NavigateToLogin, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun ConfirmLogout_resets_state() = runTest {
        val getCurrentUser: GetCurrentUserUseCase = mock()
        val logoutUseCase: LogoutUseCase = mock()
        whenever(getCurrentUser()).thenReturn(Result.success(testUser))
        whenever(logoutUseCase.invoke()).thenReturn(Result.success(Unit))

        val vm = ProfileViewModel(getCurrentUser, logoutUseCase)
        advanceUntilIdle()
        assertNotNull(vm.state.value.user)

        vm.onEvent(ProfileEvent.LogoutClicked)
        advanceUntilIdle()
        vm.onEvent(ProfileEvent.ConfirmLogout)
        advanceUntilIdle()

        assertNull(vm.state.value.user)
        assertFalse(vm.state.value.showLogoutDialog)
    }

    @Test
    fun Back_emits_NavigateBack() = runTest {
        val getCurrentUser: GetCurrentUserUseCase = mock()
        whenever(getCurrentUser()).thenReturn(Result.success(testUser))

        val vm = ProfileViewModel(getCurrentUser, mock())
        advanceUntilIdle()

        vm.effect.test {
            vm.onEvent(ProfileEvent.Back)
            advanceUntilIdle()
            assertEquals(ProfileEffect.NavigateBack, awaitItem())
        }
    }

    @Test
    fun EditProfile_emits_ShowError() = runTest {
        val getCurrentUser: GetCurrentUserUseCase = mock()
        whenever(getCurrentUser()).thenReturn(Result.success(testUser))

        val vm = ProfileViewModel(getCurrentUser, mock())
        advanceUntilIdle()

        vm.effect.test {
            vm.onEvent(ProfileEvent.EditProfile)
            advanceUntilIdle()
            assertEquals(
                ProfileEffect.ShowError("Edit profile not yet implemented"),
                awaitItem()
            )
        }
    }

    @Test
    fun DismissError_clears_errorMessage() = runTest {
        val getCurrentUser: GetCurrentUserUseCase = mock()
        whenever(getCurrentUser())
            .thenReturn(Result.failure(Exception("Failed to load profile")))

        val vm = ProfileViewModel(getCurrentUser, mock())
        advanceUntilIdle()
        assertEquals(null, "Failed to load profile", vm.state.value.errorMessage)

        vm.onEvent(ProfileEvent.DismissError)
        advanceUntilIdle()
        assertNull(vm.state.value.errorMessage)
    }

    @Test
    fun Load_reloads_user() = runTest {
        val getCurrentUser: GetCurrentUserUseCase = mock()
        whenever(getCurrentUser())
            .thenReturn(Result.success(testUser))
            .thenReturn(Result.success(testUser.copy(name = "Bob Jones")))

        val vm = ProfileViewModel(getCurrentUser, mock())
        advanceUntilIdle()
        assertEquals(null, "Alice Smith", vm.state.value.user?.name)

        vm.onEvent(ProfileEvent.Load)
        advanceUntilIdle()
        assertEquals(null, "Bob Jones", vm.state.value.user?.name)
    }
}
