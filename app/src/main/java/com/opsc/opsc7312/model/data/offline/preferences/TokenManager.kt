package com.opsc.opsc7312.model.data.offline.preferences

import android.content.Context
import android.content.SharedPreferences


class TokenManager private constructor(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    // Save token and its expiration time
    fun saveToken(token: String, expiresIn: Long) {
        val expirationTime = System.currentTimeMillis() + expiresIn
        val editor = sharedPreferences.edit()
        editor.putString("auth_token", token)
        editor.putLong("token_expiration_time", expirationTime)
        editor.apply()
    }

    // Retrieve token
    fun getToken(): String? {
        val expirationTime = sharedPreferences.getLong("token_expiration_time", 0L)
        if (isTokenExpired(expirationTime)) {
            // Token is expired or about to expire
            return null
        }
        return sharedPreferences.getString("auth_token", null)
    }

    // Check if token is expired
    private fun isTokenExpired(expirationTime: Long): Boolean {
        return System.currentTimeMillis() > expirationTime
    }

    // Clear token and expiration time (e.g., on logout)
    fun clearToken() {
        val editor = sharedPreferences.edit()
        editor.remove("auth_token")
        editor.remove("token_expiration_time")
        editor.apply()
    }

    companion object {
        @Volatile
        private var INSTANCE: TokenManager? = null

        fun getInstance(context: Context): TokenManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: TokenManager(context).also { INSTANCE = it }
            }
        }
    }
}