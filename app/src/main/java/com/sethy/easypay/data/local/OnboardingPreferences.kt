package com.sethy.easypay.data.local

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OnboardingPreferences @Inject constructor(
    @ApplicationContext context: Context
) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    suspend fun hasSeenOnboarding(): Boolean = withContext(Dispatchers.IO) {
        prefs.getBoolean(KEY_SEEN, false)
    }

    suspend fun markSeen() = withContext(Dispatchers.IO) {
        prefs.edit().putBoolean(KEY_SEEN, true).apply()
    }

    companion object {
        private const val PREFS_NAME = "easypay_onboarding"
        private const val KEY_SEEN = "onboarding_seen"
    }
}