package com.sethy.easypay.ui.state

sealed interface ProfileEffect {
    data object NavigateToOnboarding : ProfileEffect
    data class ShowError(val message: String) : ProfileEffect
    data object NavigateBack : ProfileEffect
}