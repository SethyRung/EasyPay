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

    init {
        cachedAccessToken = sharedPreferences.getString(KEY_ACCESS_TOKEN, null)
    }

    fun getAccessTokenSync(): String? = cachedAccessToken

    suspend fun getAccessToken(): String? = withContext(Dispatchers.IO) {
        cachedAccessToken
    }

    suspend fun saveTokens(accessToken: String) = withContext(Dispatchers.IO) {
        sharedPreferences.edit()
            .putString(KEY_ACCESS_TOKEN, accessToken)
            .apply()
        cachedAccessToken = accessToken
    }

    suspend fun clearTokens() = withContext(Dispatchers.IO) {
        sharedPreferences.edit()
            .remove(KEY_ACCESS_TOKEN)
            .apply()
        cachedAccessToken = null
    }

    companion object {
        private const val KEY_ACCESS_TOKEN = "access_token"
    }
}
