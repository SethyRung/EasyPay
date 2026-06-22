package com.sethy.easypay.di

import com.sethy.easypay.data.repository.AuthRepository
import com.sethy.easypay.data.repository.DefaultAuthRepository
import com.sethy.easypay.data.repository.DefaultNotificationRepository
import com.sethy.easypay.data.repository.DefaultWalletRepository
import com.sethy.easypay.data.repository.NotificationRepository
import com.sethy.easypay.data.repository.WalletRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        impl: DefaultAuthRepository
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindWalletRepository(
        impl: DefaultWalletRepository
    ): WalletRepository

    @Binds
    @Singleton
    abstract fun bindNotificationRepository(
        impl: DefaultNotificationRepository
    ): NotificationRepository
}
