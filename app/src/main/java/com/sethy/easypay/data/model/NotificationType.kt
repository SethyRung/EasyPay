package com.sethy.easypay.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class NotificationType {
    @SerialName("INFO")
    INFO,
    @SerialName("ALERT")
    ALERT,
    @SerialName("RECEIPT")
    RECEIPT,
    @SerialName("PROMO")
    PROMO
}
