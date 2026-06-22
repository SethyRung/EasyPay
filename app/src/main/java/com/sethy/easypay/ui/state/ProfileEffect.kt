package com.sethy.easypay.ui.state

sealed interface ProfileEffect {
    data object NavigateToLogin : ProfileEffect
    data class ShowError(val message: String) : ProfileEffect
    data object NavigateBack : ProfileEffect
}