package com.sethy.easypay.ui.state

sealed interface SignupEvent {
    data class NameChanged(val name: String) : SignupEvent
    data class EmailChanged(val email: String) : SignupEvent
    data class PhoneChanged(val phone: String) : SignupEvent
    data class PasswordChanged(val password: String) : SignupEvent
    data class ConfirmPasswordChanged(val confirmPassword: String) : SignupEvent
    data class TermsAcceptedChanged(val accepted: Boolean) : SignupEvent
    data object Submit : SignupEvent
    data object DismissError : SignupEvent
}