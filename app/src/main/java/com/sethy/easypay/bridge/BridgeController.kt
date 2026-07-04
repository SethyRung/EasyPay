package com.sethy.easypay.bridge

import android.webkit.WebView
import com.sethy.easypay.data.model.User
import com.sethy.easypay.domain.usecase.GetBalanceUseCase
import com.sethy.easypay.domain.usecase.GetCurrentUserUseCase
import com.sethy.easypay.domain.usecase.PayBillUseCase
import com.sethy.easypay.domain.usecase.TopUpUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import top.sunhy.component.jsbridge.IBridgeHandler
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BridgeController @Inject constructor(
    private val getCurrentUser: GetCurrentUserUseCase,
    private val getBalance: GetBalanceUseCase,
    private val payBill: PayBillUseCase,
    val topUp: TopUpUseCase,
    private val handlerFactory: BridgeHandlerFactory
) {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    private val _status = MutableStateFlow<BridgeStatus>(BridgeStatus.Initializing)
    val status: StateFlow<BridgeStatus> = _status.asStateFlow()

    private val _events = MutableSharedFlow<BridgeEvent>(extraBufferCapacity = 64)
    val events: SharedFlow<BridgeEvent> = _events.asSharedFlow()

    private val _eventLog = MutableStateFlow<List<BridgeEvent>>(emptyList())
    val eventLog: StateFlow<List<BridgeEvent>> = _eventLog.asStateFlow()

    private val _sessionStartedAt = MutableStateFlow<Long?>(null)
    val sessionStartedAt: StateFlow<Long?> = _sessionStartedAt.asStateFlow()

    private val _pendingPayment = MutableStateFlow<BridgePaymentRequest?>(null)
    val pendingPayment: StateFlow<BridgePaymentRequest?> = _pendingPayment.asStateFlow()

    private val _paymentSheetState = MutableStateFlow<PaymentSheetState>(PaymentSheetState.Hidden)
    val paymentSheetState: StateFlow<PaymentSheetState> = _paymentSheetState.asStateFlow()

    private var handler: IBridgeHandler? = null
    private var pendingPaymentCallback: ((String) -> Unit)? = null

    fun attach(webView: WebView) {
        if (handler != null) return
        val bridge = handlerFactory.create().also { handler = it }
        bridge.attach(webView)

        bridge.registerBridger(GET_USER) { _, callback ->
            record(BridgeEvent.Received(GET_USER, null))
            scope.launch {
                val user = getCurrentUser().getOrNull()
                if (user != null) {
                    val payload = encodeBridgeUser(user)
                    record(BridgeEvent.Replied(GET_USER, true))
                    callback?.invoke(payload)
                } else {
                    val err = """{"ok":false,"error":{"code":"UNAUTHENTICATED","message":"No user"}}"""
                    record(BridgeEvent.Failed(GET_USER, "No user"))
                    callback?.invoke(err)
                }
            }
        }

        bridge.registerBridger(GET_BALANCE) { _, callback ->
            record(BridgeEvent.Received(GET_BALANCE, null))
            scope.launch {
                val result = getBalance()
                result.fold(
                    onSuccess = { balance ->
                        val payload = encodeBridgeBalance(balance)
                        record(BridgeEvent.Replied(GET_BALANCE, true))
                        callback?.invoke(payload)
                    },
                    onFailure = { e ->
                        record(BridgeEvent.Failed(GET_BALANCE, e.message ?: "Unknown"))
                        callback?.invoke(failurePayload(GET_BALANCE, "NETWORK", e.message))
                    }
                )
            }
        }

        bridge.registerBridger(REQUEST_PAYMENT) { data, callback ->
            record(BridgeEvent.Received(REQUEST_PAYMENT, data))
            val request = parsePaymentRequest(data)
            if (request == null) {
                callback?.invoke(failurePayload(REQUEST_PAYMENT, "INVALID", "Bad payload"))
                record(BridgeEvent.Failed(REQUEST_PAYMENT, "Invalid payload"))
                return@registerBridger
            }
            pendingPaymentCallback = callback
            _pendingPayment.value = request
            _paymentSheetState.value = PaymentSheetState.Confirming(request)
        }

        bridge.registerBridger(SHOW_TOAST) { data, callback ->
            record(BridgeEvent.Received(SHOW_TOAST, data))
            callback?.invoke("""{"ok":true}""")
        }

        bridge.registerBridger(CLOSE) { _, callback ->
            record(BridgeEvent.Received(CLOSE, null))
            callback?.invoke("""{"ok":true}""")
        }

        _status.value = BridgeStatus.Online
        if (_sessionStartedAt.value == null) {
            _sessionStartedAt.value = System.currentTimeMillis()
        }
    }

    fun detach() {
        handler?.detach()
        handler = null
        _status.value = BridgeStatus.Initializing
        _sessionStartedAt.value = null
        _eventLog.value = emptyList()
        clearPendingPayment()
    }

    fun markOffline(reason: String) {
        _status.value = BridgeStatus.Offline(reason)
    }

    fun confirmPayment() {
        val request = _pendingPayment.value
        val callback = pendingPaymentCallback
        if (request == null || callback == null) {
            clearPendingPayment()
            return
        }
        _paymentSheetState.value = PaymentSheetState.Processing(request)

        scope.launch {
            val result = payBill(
                billerCode = request.billerCode,
                accountNumber = request.accountNumber,
                amountMajor = request.amountMajor,
                note = request.note
            )
            result.fold(
                onSuccess = { payment ->
                    val payload = encodePaymentSuccess(request, payment.balanceAfterMinor, payment.transactionId)
                    record(BridgeEvent.Replied(REQUEST_PAYMENT, true))
                    callback(payload)
                    _paymentSheetState.value = PaymentSheetState.Success(request)
                },
                onFailure = { e ->
                    val (code, _) = classifyPaymentFailure(e)
                    if (code == "INSUFFICIENT_FUNDS") {
                        _paymentSheetState.value = PaymentSheetState.InsufficientFunds(request)
                    } else {
                        _paymentSheetState.value = PaymentSheetState.Error(request, e.message ?: "Unknown")
                    }
                    val payload = failurePayload(REQUEST_PAYMENT, code, e.message)
                    record(BridgeEvent.Failed(REQUEST_PAYMENT, e.message ?: "Unknown"))
                    callback(payload)
                }
            )
        }
    }

    fun declinePayment(code: String = "USER_CANCELLED", message: String = "Cancelled by user") {
        val request = _pendingPayment.value
        val callback = pendingPaymentCallback
        if (callback != null) {
            callback(failurePayload(REQUEST_PAYMENT, code, message))
            record(BridgeEvent.Failed(REQUEST_PAYMENT, message))
        }
        clearPendingPayment()
        _paymentSheetState.value = PaymentSheetState.Hidden
    }

    fun dismissPaymentSheet() {
        if (_pendingPayment.value == null) {
            _paymentSheetState.value = PaymentSheetState.Hidden
        }
    }

    private fun clearPendingPayment() {
        pendingPaymentCallback = null
        _pendingPayment.value = null
    }

    private fun record(event: BridgeEvent) {
        scope.launch {
            _events.emit(event)
            val updated = (_eventLog.value + event).takeLast(MAX_LOG_ENTRIES)
            _eventLog.value = updated
        }
    }

    private fun encodeBridgeUser(user: User): String =
        """{"id":"${user.id}","name":"${escape(user.name)}","email":"${escape(user.email)}"}"""

    private fun encodeBridgeBalance(balance: Double): String {
        val minor = (balance * 100).toLong()
        return """{"currency":"USD","balanceMinor":$minor,"balance":$balance}"""
    }

    private fun encodePaymentSuccess(
        request: BridgePaymentRequest,
        balanceAfterMinor: Long,
        transactionId: String
    ): String {
        val amountMinor = (request.amountMajor * 100).toLong()
        return buildString {
            append('{')
            append(""""ok":true,""")
            append(""""merchantRef":"${escape(request.merchantRef)}",""")
            append(""""transactionId":"${escape(transactionId)}",""")
            append(""""amountMinor":$amountMinor,""")
            append(""""balanceAfterMinor":$balanceAfterMinor""")
            append('}')
        }
    }

    private fun failurePayload(method: String, code: String, message: String?): String {
        val safe = message?.let { escape(it) } ?: "Unknown error"
        return """{"ok":false,"error":{"code":"$code","message":"$safe"}}"""
    }

    private fun classifyPaymentFailure(error: Throwable): Pair<String, String> {
        val message = error.message.orEmpty()
        return when {
            message.contains("Insufficient", ignoreCase = true) -> "INSUFFICIENT_FUNDS" to message
            else -> "NETWORK" to message
        }
    }

    private fun parsePaymentRequest(raw: String?): BridgePaymentRequest? {
        val payload = raw?.takeIf { it.isNotBlank() } ?: return null
        return runCatching {
            val json = JSONObject(payload)
            val merchantRef = json.optString("merchantRef").takeIf { it.isNotBlank() } ?: return@runCatching null
            val billerCode = json.optString("billerCode", "glitch").ifBlank { "glitch" }
            val accountNumber = json.optString("accountNumber")
            val amountMajor = json.optDouble("amount", 0.0)
            val currency = json.optString("currency", "USD").ifBlank { "USD" }
            val note = json.optString("note", merchantRef)
            val itemsArray = json.optJSONArray("items")
            val items = if (itemsArray != null) parsePaymentItems(itemsArray) else emptyList()
            BridgePaymentRequest(
                merchantRef = merchantRef,
                billerCode = billerCode,
                accountNumber = accountNumber,
                amountMajor = amountMajor,
                currency = currency,
                note = note,
                items = items
            )
        }.getOrNull()
    }

    private fun parsePaymentItems(array: JSONArray): List<BridgePaymentItem> = buildList {
        for (i in 0 until array.length()) {
            val obj = array.optJSONObject(i) ?: continue
            add(
                BridgePaymentItem(
                    gameId = obj.optString("gameId"),
                    name = obj.optString("name"),
                    imageUrl = obj.optString("imageUrl").takeIf { it.isNotBlank() },
                    quantity = obj.optInt("quantity", 1),
                    priceMajor = obj.optDouble("price", 0.0)
                )
            )
        }
    }

    private fun escape(value: String): String =
        value.replace("\\", "\\\\").replace("\"", "\\\"")

    companion object {
        const val BRIDGE_NAME = "WKWebViewJavascriptBridge"

        const val GET_USER = "wallet.getUser"
        const val GET_BALANCE = "wallet.getBalance"
        const val REQUEST_PAYMENT = "wallet.requestPayment"
        const val SHOW_TOAST = "wallet.showToast"
        const val CLOSE = "wallet.close"

        const val MAX_LOG_ENTRIES = 200
    }
}

sealed interface PaymentSheetState {
    data object Hidden : PaymentSheetState
    data class Confirming(val request: BridgePaymentRequest) : PaymentSheetState
    data class Processing(val request: BridgePaymentRequest) : PaymentSheetState
    data class Success(val request: BridgePaymentRequest) : PaymentSheetState
    data class InsufficientFunds(val request: BridgePaymentRequest) : PaymentSheetState
    data class Error(val request: BridgePaymentRequest, val message: String) : PaymentSheetState
}