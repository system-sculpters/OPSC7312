package com.opsc.opsc7312.model.data.offline.preferences

import android.content.Context
import android.content.SharedPreferences
import android.util.Log


class TokenManager private constructor(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    // Save token and its expiration time
    // This method was adapted from stackoverflow
    // https://stackoverflow.com/questions/3624280/how-to-use-sharedpreferences-in-android-to-store-fetch-and-edit-values
    // Harneet Kaur
    // https://stackoverflow.com/users/1444525/harneet-kaur
    // Ziem
    // https://stackoverflow.com/posts/11027631/revisions
    fun saveToken(token: String, expiresIn: Long) {
        // Create an editor object to make changes to sharedPreferences
        val editor = sharedPreferences.edit()

        // Store the provided token in sharedPreferences with the key "auth_token"
        editor.putString("auth_token", token)

        // Store the expiration time of the token in sharedPreferences with the key "token_expiration_time"
        editor.putLong("token_expiration_time", expiresIn)

        // Apply the changes to sharedPreferences asynchronously
        editor.apply()
    }


    // Retrieve token
    // This method was adapted from stackoverflow
    // https://stackoverflow.com/questions/3624280/how-to-use-sharedpreferences-in-android-to-store-fetch-and-edit-values
    // Harneet Kaur
    // https://stackoverflow.com/users/1444525/harneet-kaur
    // Ziem
    // https://stackoverflow.com/posts/11027631/revisions
    fun getToken(): String? {
        // Retrieve the token expiration time from sharedPreferences, defaulting to 0 if not found
        val expirationTime = sharedPreferences.getLong("token_expiration_time", 0L)

        // Check if the token is expired using the helper function `isTokenExpired`
        if (isTokenExpired(expirationTime)) {
            // Return null if the token is expired or about to expire
            return null
        }

        // Return the stored authentication token from sharedPreferences (or null if not found)
        return sharedPreferences.getString("auth_token", null)
    }

    private fun isTokenExpired(expirationTime: Long): Boolean {
        // Check if the current time has passed the expiration time (i.e., the token is expired)
        return System.currentTimeMillis() >= expirationTime
    }


    // Clear token and expiration time (e.g., on logout)
    // Save token and its expiration time
    // This method was adapted from stackoverflow
    // https://stackoverflow.com/questions/3624280/how-to-use-sharedpreferences-in-android-to-store-fetch-and-edit-values
    // Harneet Kaur
    // https://stackoverflow.com/users/1444525/harneet-kaur
    // Ziem
    // https://stackoverflow.com/posts/11027631/revisions
    fun clearToken() {
        // Get an editor object to modify the sharedPreferences
        val editor = sharedPreferences.edit()

        // Remove the stored authentication token from sharedPreferences
        editor.remove("auth_token")

        // Remove the stored token expiration time from sharedPreferences
        editor.remove("token_expiration_time")

        // Apply the changes to sharedPreferences asynchronously (saves the changes in the background)
        editor.apply()
    }


    // Save token and its expiration time
    // This method was adapted from stackoverflow
    // https://stackoverflow.com/questions/3624280/how-to-use-sharedpreferences-in-android-to-store-fetch-and-edit-values
    // Harneet Kaur
    // https://stackoverflow.com/users/1444525/harneet-kaur
    // Ziem
    // https://stackoverflow.com/posts/11027631/revisions
    fun getTokenExpirationTime(): Long{
        return sharedPreferences.getLong("token_expiration_time", 0L)
    }

    companion object {
        // The Volatile keyword ensures that the instance is visible to all threads and changes are immediately reflected across them.

        // This object was adapted from stackoverflow
        // https://stackoverflow.com/questions/40398072/singleton-with-parameter-in-kotlin
        // aminography
        // https://stackoverflow.com/users/1631967/aminography
        @Volatile
        private var INSTANCE: TokenManager? = null

        //Retrieves the singleton instance of the TokenManager.
        //This method ensures that only one instance of TokenManager is created across the application. If the instance doesn't exist,

        fun getInstance(context: Context): TokenManager {
            // Check if INSTANCE is null; if so, enter synchronized block to ensure thread safety
            return INSTANCE ?: synchronized(this) {
                // Double-check if INSTANCE is still null before creating a new instance of TokenManager
                INSTANCE ?: TokenManager(context).also { INSTANCE = it }  // Initialize the instance and set INSTANCE to it
            }
        }
    }

}