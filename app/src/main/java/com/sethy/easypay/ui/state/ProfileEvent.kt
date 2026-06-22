package com.sethy.easypay.ui.state

sealed interface ProfileEvent {
    data object Load : ProfileEvent
    data object EditProfile : ProfileEvent
    data object LogoutClicked : ProfileEvent
    data object ConfirmLogout : ProfileEvent
    data object DismissLogout : ProfileEvent
    data object Back : ProfileEvent
    data object DismissError : ProfileEvent
}