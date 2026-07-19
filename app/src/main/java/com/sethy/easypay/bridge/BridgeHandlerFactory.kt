package com.sethy.easypay.bridge

import top.sunhy.component.jsbridge.IBridgeHandler
import top.sunhy.component.jsbridge.JsBridgeHandler
import top.sunhy.component.jsbridge.WebJsBridge
import javax.inject.Inject
import javax.inject.Singleton

fun interface BridgeHandlerFactory {
    fun create(): IBridgeHandler
}

@Singleton
class DefaultBridgeHandlerFactory @Inject constructor() : BridgeHandlerFactory {

    init {
        WebJsBridge.setBridgeName(BridgeController.BRIDGE_NAME)
    }

    override fun create(): IBridgeHandler = JsBridgeHandler()
}