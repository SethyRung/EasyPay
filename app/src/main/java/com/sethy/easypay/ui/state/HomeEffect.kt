package com.sethy.easypay.ui.state

sealed interface HomeEffect {
    data object NavigateToSendMoney : HomeEffect
    data class NavigateToTransactionDetail(val id: String) : HomeEffect
    data object NavigateToNotifications : HomeEffect
    data object NavigateToProfile : HomeEffect
    data object NavigateToCalendar : HomeEffect
}
