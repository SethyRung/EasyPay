package com.sethy.easypay.ui.state

sealed interface OnboardingEvent {
    data object Next : OnboardingEvent
    data object Skip : OnboardingEvent
}