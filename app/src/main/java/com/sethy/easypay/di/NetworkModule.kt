package com.sethy.easypay.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.sethy.easypay.BuildConfig
import com.sethy.easypay.data.api.ApiProvider
import com.sethy.easypay.data.api.AuthApi
import com.sethy.easypay.data.api.AuthInterceptor
import com.sethy.easypay.data.api.NotificationApi
import com.sethy.easypay.data.api.WalletApi
import com.sethy.easypay.data.local.AuthTokenManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

    @Provides
    @Singleton
    fun provideAuthInterceptor(
        tokenManager: AuthTokenManager,
        @Named("authOnly") authApi: AuthApi
    ): AuthInterceptor = AuthInterceptor(tokenManager, authApi)

    @Provides
    @Singleton
    @Named("default")
    fun provideOkHttpClient(
        authInterceptor: AuthInterceptor,
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    @Provides
    @Singleton
    @Named("authOnly")
    fun provideAuthOnlyOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    @OptIn(ExperimentalSerializationApi::class)
    @Provides
    @Singleton
    @Named("default")
    fun provideRetrofit(
        json: Json,
        @Named("default") okHttpClient: OkHttpClient
    ): Retrofit = Retrofit.Builder()
        .baseUrl(ApiProvider.BASE_URL)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .client(okHttpClient)
        .build()

    @OptIn(ExperimentalSerializationApi::class)
    @Provides
    @Singleton
    @Named("authOnly")
    fun provideAuthOnlyRetrofit(
        json: Json,
        @Named("authOnly") okHttpClient: OkHttpClient
    ): Retrofit = Retrofit.Builder()
        .baseUrl(ApiProvider.BASE_URL)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .client(okHttpClient)
        .build()

    @Provides
    @Singleton
    fun provideAuthApi(@Named("default") retrofit: Retrofit): AuthApi =
        retrofit.create(AuthApi::class.java)

    @Provides
    @Singleton
    @Named("authOnly")
    fun provideAuthOnlyAuthApi(@Named("authOnly") retrofit: Retrofit): AuthApi =
        retrofit.create(AuthApi::class.java)

    @Provides
    @Singleton
    fun provideWalletApi(@Named("default") retrofit: Retrofit): WalletApi =
        retrofit.create(WalletApi::class.java)

    @Provides
    @Singleton
    fun provideNotificationApi(@Named("default") retrofit: Retrofit): NotificationApi =
        retrofit.create(NotificationApi::class.java)

    @Provides
    @Singleton
    @GlitchHost
    fun provideGlitchHost(): String = BuildConfig.GLITCH_HOST
}
