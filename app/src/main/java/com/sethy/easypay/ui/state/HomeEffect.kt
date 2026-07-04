package com.sethy.easypay.ui.state

sealed interface HomeEffect {
    data object NavigateToStore : HomeEffect
    data object NavigateToSendMoney : HomeEffect
    data class NavigateToTransactionDetail(val id: String) : HomeEffect
}