package com.sethy.easypay.ui.state

sealed interface OnboardingEvent {
    data object GetStartedClicked : OnboardingEvent
    data object LoginClicked : OnboardingEvent
}
