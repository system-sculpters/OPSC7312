package com.opsc.opsc7312.model.data.offline.preferences

import android.content.Context
import android.content.SharedPreferences
import com.opsc.opsc7312.model.data.model.User
import java.security.MessageDigest

class UserManager private constructor(context: Context){
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)



    // Save token
    fun saveUser(user: User) {
        val editor = sharedPreferences.edit()
        editor.putString("userid", user.id)
        editor.putString("username", user.username)
        editor.putString("email", user.email)
        editor.apply()
    }

    // Retrieve token
    fun getUser(): User {
        val userid = sharedPreferences.getString("userid", null)
        val username = sharedPreferences.getString("username", null)
        val email = sharedPreferences.getString("email", null)
        var user: User = User()
        if(userid != null && email != null && username != null){
            user = User(id = userid, email = email, username = username)
        }
        return user
    }

    // Clear token (e.g., on logout)
    fun clearUser() {
        val editor = sharedPreferences.edit()
        editor.remove("userid")
        editor.remove("username")
        editor.remove("email")
        editor.apply()
    }

    fun savePassword(password: String) {
        val editor = sharedPreferences.edit()
        editor.putString("password", password) // Save the hashed password
        editor.apply()
    }

    fun getPassword(): String {
        val password = sharedPreferences.getString("password", null)!!
        return password
    }
    companion object {
        @Volatile
        private var INSTANCE: UserManager? = null

        fun getInstance(context: Context): UserManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: UserManager(context).also { INSTANCE = it }
            }
        }
    }
}