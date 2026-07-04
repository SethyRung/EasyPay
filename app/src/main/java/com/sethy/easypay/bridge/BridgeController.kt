package com.sethy.easypay.bridge

import android.webkit.WebView
import com.sethy.easypay.data.model.User
import com.sethy.easypay.domain.usecase.GetBalanceUseCase
import com.sethy.easypay.domain.usecase.GetCurrentUserUseCase
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
import top.sunhy.component.jsbridge.IBridgeHandler
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BridgeController @Inject constructor(
    private val getCurrentUser: GetCurrentUserUseCase,
    private val getBalance: GetBalanceUseCase,
    private val handlerFactory: BridgeHandlerFactory
) {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    private val _status = MutableStateFlow<BridgeStatus>(BridgeStatus.Initializing)
    val status: StateFlow<BridgeStatus> = _status.asStateFlow()

    private val _events = MutableSharedFlow<BridgeEvent>(extraBufferCapacity = 64)
    val events: SharedFlow<BridgeEvent> = _events.asSharedFlow()

    private var handler: IBridgeHandler? = null

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

        bridge.registerBridger(SHOW_TOAST) { data, callback ->
            record(BridgeEvent.Received(SHOW_TOAST, data))
            callback?.invoke("""{"ok":true}""")
        }

        bridge.registerBridger(CLOSE) { _, callback ->
            record(BridgeEvent.Received(CLOSE, null))
            callback?.invoke("""{"ok":true}""")
        }

        _status.value = BridgeStatus.Online
    }

    fun detach() {
        handler?.detach()
        handler = null
        _status.value = BridgeStatus.Initializing
    }

    fun markOffline(reason: String) {
        _status.value = BridgeStatus.Offline(reason)
    }

    private fun record(event: BridgeEvent) {
        scope.launch { _events.emit(event) }
    }

    private fun encodeBridgeUser(user: User): String =
        """{"id":"${user.id}","name":"${escape(user.name)}","email":"${escape(user.email)}"}"""

    private fun encodeBridgeBalance(balance: Double): String {
        val minor = (balance * 100).toLong()
        return """{"currency":"USD","balanceMinor":$minor,"balance":${balance}}"""
    }

    private fun failurePayload(method: String, code: String, message: String?): String {
        val safe = message?.let { escape(it) } ?: "Unknown error"
        return """{"ok":false,"error":{"code":"$code","message":"$safe"}}"""
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
    }
}