package com.sethy.easypay.ui.state

data class OnboardingUiState(
    val currentStep: Int = 0,
    val totalSteps: Int = 3
) {
    val isLastStep: Boolean get() = currentStep == totalSteps - 1
}