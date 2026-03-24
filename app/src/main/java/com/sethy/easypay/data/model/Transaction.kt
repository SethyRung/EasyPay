package com.sethy.easypay.data.model

data class Transaction(
    val id: String,
    val recipientName: String,
    val amount: Double,
    val type: TransactionType,
    val timestamp: Long = System.currentTimeMillis(),
    val avatarUrl: String? = null
)

enum class TransactionType {
    SENT, RECEIVED
}
