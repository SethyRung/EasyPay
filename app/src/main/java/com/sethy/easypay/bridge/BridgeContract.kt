package com.sethy.easypay.bridge

sealed interface BridgeStatus {
    data object Initializing : BridgeStatus
    data object Online : BridgeStatus
    data class Offline(val reason: String) : BridgeStatus
}

sealed interface BridgeEvent {
    val method: String
    val timestampMillis: Long

    data class Received(
        override val method: String,
        val payload: String?,
        override val timestampMillis: Long = System.currentTimeMillis()
    ) : BridgeEvent

    data class Replied(
        override val method: String,
        val ok: Boolean,
        override val timestampMillis: Long = System.currentTimeMillis()
    ) : BridgeEvent

    data class Failed(
        override val method: String,
        val reason: String,
        override val timestampMillis: Long = System.currentTimeMillis()
    ) : BridgeEvent
}

data class BridgeUser(
    val id: String,
    val name: String,
    val email: String
)

data class BridgeBalance(
    val currency: String,
    val balanceMinor: Long,
    val balance: Double
)

data class BridgePaymentItem(
    val gameId: String,
    val name: String,
    val imageUrl: String?,
    val quantity: Int,
    val priceMajor: Double
)

data class BridgePaymentRequest(
    val merchantRef: String,
    val billerCode: String,
    val accountNumber: String,
    val amountMajor: Double,
    val currency: String,
    val note: String,
    val items: List<BridgePaymentItem>
)