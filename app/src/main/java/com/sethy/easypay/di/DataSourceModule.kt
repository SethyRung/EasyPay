package com.sethy.easypay.di

import android.content.Context
import com.sethy.easypay.BuildConfig
import com.sethy.easypay.data.api.AuthApi
import com.sethy.easypay.data.api.NotificationApi
import com.sethy.easypay.data.api.WalletApi
import com.sethy.easypay.data.source.AuthDataSource
import com.sethy.easypay.data.source.MockAuthDataSource
import com.sethy.easypay.data.source.MockDataLoader
import com.sethy.easypay.data.source.MockWalletDataSource
import com.sethy.easypay.data.source.RemoteAuthDataSource
import com.sethy.easypay.data.source.RemoteWalletDataSource
import com.sethy.easypay.data.source.WalletDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataSourceModule {

    @Provides
    @Singleton
    fun provideWalletDataSource(
        @ApplicationContext context: Context,
        walletApi: WalletApi,
        notificationApi: NotificationApi
    ): WalletDataSource = if (BuildConfig.USE_MOCK_DATA) {
        MockWalletDataSource(context)
    } else {
        RemoteWalletDataSource(walletApi, notificationApi)
    }

    @Provides
    @Singleton
    fun provideAuthDataSource(
        @ApplicationContext context: Context,
        authApi: AuthApi
    ): AuthDataSource = if (BuildConfig.USE_MOCK_DATA) {
        MockAuthDataSource(seed = { MockDataLoader.loadUser(context) })
    } else {
        RemoteAuthDataSource(authApi)
    }
}
