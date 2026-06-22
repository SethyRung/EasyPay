package com.sethy.easypay.ui.state

sealed interface TransactionDetailEvent {
    data object Load : TransactionDetailEvent
    data object Refresh : TransactionDetailEvent
    data object ShareReceipt : TransactionDetailEvent
    data object ReportIssue : TransactionDetailEvent
    data object Back : TransactionDetailEvent
    data object DismissError : TransactionDetailEvent
}
