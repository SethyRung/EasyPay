package com.sethy.easypay.di

import com.sethy.easypay.bridge.AndroidWebViewCookieStore
import com.sethy.easypay.bridge.BridgeHandlerFactory
import com.sethy.easypay.bridge.DefaultBridgeHandlerFactory
import com.sethy.easypay.bridge.WebViewCookieStore
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class BridgeModule {

    @Binds
    @Singleton
    abstract fun bindBridgeHandlerFactory(
        impl: DefaultBridgeHandlerFactory
    ): BridgeHandlerFactory

    @Binds
    @Singleton
    abstract fun bindWebViewCookieStore(
        impl: AndroidWebViewCookieStore
    ): WebViewCookieStore
}