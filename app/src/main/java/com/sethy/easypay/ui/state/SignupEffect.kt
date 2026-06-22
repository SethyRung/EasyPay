package com.sethy.easypay.ui.state

sealed interface SignupEffect {
    data object NavigateToHome : SignupEffect
    data class ShowError(val message: String) : SignupEffect
}