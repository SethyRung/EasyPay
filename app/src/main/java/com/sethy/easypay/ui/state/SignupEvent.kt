package com.sethy.easypay.ui.state

sealed interface SignupEvent {
    data class NameChanged(val name: String) : SignupEvent
    data class EmailChanged(val email: String) : SignupEvent
    data class PhoneChanged(val phone: String) : SignupEvent
    data class PasswordChanged(val password: String) : SignupEvent
    data class ConfirmPasswordChanged(val confirmPassword: String) : SignupEvent
    data class TermsAcceptedChanged(val accepted: Boolean) : SignupEvent
    data class FieldTouched(val field: SignupField) : SignupEvent
    data object NextStep : SignupEvent
    data object PreviousStep : SignupEvent
    data object Submit : SignupEvent
    data object DismissError : SignupEvent
}

enum class SignupField {
    NAME, EMAIL, PHONE, PASSWORD, CONFIRM_PASSWORD
}
