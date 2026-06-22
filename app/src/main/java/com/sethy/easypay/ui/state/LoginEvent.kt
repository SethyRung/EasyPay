package com.sethy.easypay.ui.state

sealed interface LoginEvent {
    data class EmailChanged(val email: String) : LoginEvent
    data class PasswordChanged(val password: String) : LoginEvent
    data object EmailTouched : LoginEvent
    data object PasswordTouched : LoginEvent
    data object Submit : LoginEvent
    data object DismissError : LoginEvent
}
