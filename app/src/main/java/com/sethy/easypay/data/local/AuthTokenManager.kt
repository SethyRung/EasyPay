package com.sethy.easypay.data.local

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthTokenManager @Inject constructor(
    @ApplicationContext context: Context
) {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val sharedPreferences = EncryptedSharedPreferences.create(
        context,
        "auth_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    @Volatile private var cachedAccessToken: String? = null
    @Volatile private var cachedRefreshToken: String? = null
    @Volatile private var cachedExpiry: Long = 0L

    init {
        cachedAccessToken = sharedPreferences.getString(KEY_ACCESS_TOKEN, null)
        cachedRefreshToken = sharedPreferences.getString(KEY_REFRESH_TOKEN, null)
        cachedExpiry = sharedPreferences.getLong(KEY_TOKEN_EXPIRY, 0L)
    }

    fun getAccessTokenSync(): String? = cachedAccessToken

    fun getRefreshTokenSync(): String? = cachedRefreshToken

    fun isAccessTokenExpiredSync(): Boolean = System.currentTimeMillis() >= cachedExpiry

    fun clearTokensBlocking() {
        cachedAccessToken = null
        cachedRefreshToken = null
        cachedExpiry = 0L
        sharedPreferences.edit()
            .remove(KEY_ACCESS_TOKEN)
            .remove(KEY_REFRESH_TOKEN)
            .apply()
    }

    suspend fun getAccessToken(): String? = withContext(Dispatchers.IO) {
        cachedAccessToken
    }

    suspend fun getRefreshToken(): String? = withContext(Dispatchers.IO) {
        cachedRefreshToken
    }

    suspend fun isAccessTokenExpired(): Boolean = withContext(Dispatchers.IO) {
        isAccessTokenExpiredSync()
    }

    suspend fun saveTokens(accessToken: String, refreshToken: String) = withContext(Dispatchers.IO) {
        sharedPreferences.edit()
            .putString(KEY_ACCESS_TOKEN, accessToken)
            .putString(KEY_REFRESH_TOKEN, refreshToken)
            .apply()
        cachedAccessToken = accessToken
        cachedRefreshToken = refreshToken
    }

    suspend fun clearTokens() = withContext(Dispatchers.IO) {
        sharedPreferences.edit()
            .remove(KEY_ACCESS_TOKEN)
            .remove(KEY_REFRESH_TOKEN)
            .apply()
        cachedAccessToken = null
        cachedRefreshToken = null
        cachedExpiry = 0L
    }

    suspend fun setTokenExpiry(timestamp: Long) = withContext(Dispatchers.IO) {
        sharedPreferences.edit()
            .putLong(KEY_TOKEN_EXPIRY, timestamp)
            .apply()
        cachedExpiry = timestamp
    }

    companion object {
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
        private const val KEY_TOKEN_EXPIRY = "token_expiry"
    }
}
