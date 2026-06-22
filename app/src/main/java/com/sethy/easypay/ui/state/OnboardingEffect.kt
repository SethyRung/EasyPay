package com.sethy.easypay.ui.state

sealed interface OnboardingEffect {
    data object NavigateToSignup : OnboardingEffect
    data object NavigateToLogin : OnboardingEffect
}
