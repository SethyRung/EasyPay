package com.sethy.easypay.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class TransactionStatus {
    @SerialName("PENDING")
    PENDING,
    @SerialName("COMPLETED")
    COMPLETED,
    @SerialName("FAILED")
    FAILED
}
