package com.sethy.easypay.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sethy.easypay.data.local.OnboardingPreferences
import com.sethy.easypay.ui.state.OnboardingEffect
import com.sethy.easypay.ui.state.OnboardingEvent
import com.sethy.easypay.ui.state.OnboardingUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val onboardingPreferences: OnboardingPreferences
) : ViewModel() {

    private val _state = MutableStateFlow(OnboardingUiState())
    val state: StateFlow<OnboardingUiState> = _state.asStateFlow()

    private val _effect = Channel<OnboardingEffect>(Channel.BUFFERED)
    val effect: Flow<OnboardingEffect> = _effect.receiveAsFlow()

    fun onEvent(event: OnboardingEvent) {
        when (event) {
            OnboardingEvent.Next -> advance()
            OnboardingEvent.Skip -> complete()
        }
    }

    private fun advance() {
        val current = _state.value
        if (current.isLastStep) {
            complete()
        } else {
            _state.value = current.copy(currentStep = current.currentStep + 1)
        }
    }

    private fun complete() {
        viewModelScope.launch {
            onboardingPreferences.markSeen()
            _effect.send(OnboardingEffect.NavigateToLogin)
        }
    }
}