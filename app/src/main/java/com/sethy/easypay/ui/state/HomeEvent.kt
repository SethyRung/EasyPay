package com.sethy.easypay.ui.state

sealed interface HomeEvent {
    data object Refresh : HomeEvent
    data object SendMoneyClick : HomeEvent
    data class TransactionClick(val id: String) : HomeEvent
    data object DismissError : HomeEvent
}