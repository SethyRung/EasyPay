package com.sethy.easypay.data.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class Transaction(
    val id: String,
    @SerialName("recipientName")
    val recipientName: String,
    val amount: Double,
    val type: TransactionType,
    @SerialName("timestamp")
    val timestamp: Long = System.currentTimeMillis(),
    @SerialName("avatarUrl")
    val avatarUrl: String? = null
)

@Serializable
enum class TransactionType {
    @SerialName("SENT")
    SENT,
    @SerialName("RECEIVED")
    RECEIVED
}
