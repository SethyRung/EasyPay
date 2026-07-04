package com.sethy.easypay.ui.viewmodel

import app.cash.turbine.test
import com.sethy.easypay.data.local.OnboardingPreferences
import com.sethy.easypay.ui.state.OnboardingEffect
import com.sethy.easypay.ui.state.OnboardingEvent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

class OnboardingViewModelTest {

    private fun createViewModel(): Pair<OnboardingViewModel, OnboardingPreferences> {
        val prefs: OnboardingPreferences = mock()
        return OnboardingViewModel(prefs) to prefs
    }

    @Test
    fun next_advances_to_step_1_from_step_0() = runTest {
        val (vm, _) = createViewModel()

        vm.onEvent(OnboardingEvent.Next)

        assertEquals(1, vm.state.value.currentStep)
    }

    @Test
    fun next_advances_to_step_2_from_step_1() = runTest {
        val (vm, _) = createViewModel()

        vm.onEvent(OnboardingEvent.Next)
        vm.onEvent(OnboardingEvent.Next)

        assertEquals(2, vm.state.value.currentStep)
    }

    @Test
    fun next_on_last_step_emits_NavigateToLogin_and_marks_seen() = runTest {
        val (vm, prefs) = createViewModel()

        vm.effect.test {
            repeat(3) { vm.onEvent(OnboardingEvent.Next) }

            val effect = awaitItem()
            assertEquals(OnboardingEffect.NavigateToLogin, effect)
            verify(prefs).markSeen()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun skip_emits_NavigateToLogin_and_marks_seen_without_advancing() = runTest {
        val (vm, prefs) = createViewModel()

        vm.effect.test {
            vm.onEvent(OnboardingEvent.Skip)

            assertEquals(0, vm.state.value.currentStep)
            val effect = awaitItem()
            assertEquals(OnboardingEffect.NavigateToLogin, effect)
            verify(prefs).markSeen()
            cancelAndIgnoreRemainingEvents()
        }
    }
}