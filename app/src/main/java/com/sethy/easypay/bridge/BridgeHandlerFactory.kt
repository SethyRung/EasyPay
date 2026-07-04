package com.sethy.easypay.bridge

import top.sunhy.component.jsbridge.IBridgeHandler
import top.sunhy.component.jsbridge.JsBridgeHandler
import top.sunhy.component.jsbridge.WebJsBridge
import javax.inject.Inject
import javax.inject.Singleton

fun interface BridgeHandlerFactory {
    fun create(): IBridgeHandler

    companion object {
        val Default: BridgeHandlerFactory = BridgeHandlerFactory {
            JsBridgeHandler().also { WebJsBridge.setBridgeName(BridgeController.BRIDGE_NAME) }
        }
    }
}

@Singleton
class DefaultBridgeHandlerFactory @Inject constructor() : BridgeHandlerFactory {
    override fun create(): IBridgeHandler = BridgeHandlerFactory.Default.create()
}