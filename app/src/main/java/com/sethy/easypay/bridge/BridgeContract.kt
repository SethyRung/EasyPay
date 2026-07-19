package com.sethy.easypay.bridge

sealed interface BridgeStatus {
    data object Initializing : BridgeStatus
    data object Online : BridgeStatus
    data class Offline(val reason: String) : BridgeStatus
}

sealed interface BridgeEvent {
    val method: String
    val timestampMillis: Long
    val id: Long

    data class Received(
        override val method: String,
        val payload: String?,
        override val timestampMillis: Long = System.currentTimeMillis(),
        override val id: Long = System.nanoTime()
    ) : BridgeEvent

    data class Replied(
        override val method: String,
        val ok: Boolean,
        override val timestampMillis: Long = System.currentTimeMillis(),
        override val id: Long = System.nanoTime()
    ) : BridgeEvent

    data class Failed(
        override val method: String,
        val reason: String,
        override val timestampMillis: Long = System.currentTimeMillis(),
        override val id: Long = System.nanoTime()
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

sealed interface PaymentSheetState {
    data object Hidden : PaymentSheetState
    data class Confirming(val request: BridgePaymentRequest) : PaymentSheetState
    data class Processing(val request: BridgePaymentRequest) : PaymentSheetState
    data class Success(val request: BridgePaymentRequest) : PaymentSheetState
    data class InsufficientFunds(val request: BridgePaymentRequest) : PaymentSheetState
    data class Error(val request: BridgePaymentRequest, val message: String) : PaymentSheetState
}