package com.sethy.easypay.ui.state

import com.sethy.easypay.design.components.BottomNavItem

sealed interface HomeEvent {
    data object Refresh : HomeEvent
    data object SendMoneyClick : HomeEvent
    data class TransactionClick(val id: String) : HomeEvent
    data class BottomNavSelected(val item: BottomNavItem) : HomeEvent
    data object DismissError : HomeEvent
}
