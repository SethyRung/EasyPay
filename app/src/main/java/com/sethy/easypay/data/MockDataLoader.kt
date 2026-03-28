package com.sethy.easypay.data

import android.content.Context
import com.sethy.easypay.data.model.Transaction
import com.sethy.easypay.data.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

/**
 * Centralized loader for mock data from assets folder.
 * All mock JSON files should be placed in app/src/main/assets/data/
 */
object MockDataLoader {
    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    suspend fun loadUser(context: Context): User = withContext(Dispatchers.IO) {
        val jsonString = readAssetFile(context, "data/user.json")
        json.decodeFromString<User>(jsonString)
    }

    suspend fun loadTransactions(context: Context): List<Transaction> = withContext(Dispatchers.IO) {
        val jsonString = readAssetFile(context, "data/transactions.json")
        json.decodeFromString<List<Transaction>>(jsonString)
    }

    private fun readAssetFile(context: Context, path: String): String {
        return context.assets.open(path).bufferedReader().use { it.readText() }
    }
}
