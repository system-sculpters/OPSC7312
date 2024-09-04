package com.opsc.opsc7312.model.api.controllers

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.opsc.opsc7312.model.api.retrofitclients.AuthRetrofitClient
import com.opsc.opsc7312.model.data.model.TokenResponse
import com.opsc.opsc7312.model.data.model.User
import com.opsc.opsc7312.model.data.offline.preferences.TokenManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthController : ViewModel() {

    private var api = AuthRetrofitClient.apiService

    val status: MutableLiveData<Boolean> = MutableLiveData()
    val message: MutableLiveData<String> = MutableLiveData()
    val code: MutableLiveData<Int> = MutableLiveData()
    val userData: MutableLiveData<User> = MutableLiveData()
    val newToken: MutableLiveData<TokenResponse> = MutableLiveData()



    fun register(user: User){
        api.register(user).enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    val createdUser = response.body()
                    createdUser?.let {
                        status.postValue(true)
                        message.postValue("Request failed with code: ${it}")
                        Log.d("MainActivity", "User created: $it")
                    }
                } else {
                    status.postValue(false)
                    message.postValue("Request failed with code: ${response.code()}: ${response.body()?.error}")
                    Log.e("MainActivity", "Request failed with code: ${response.code()}: ${response.body()?.error}")
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Log.e("MainActivity", "Error: ${t.message}")
                status.postValue(false)
                message.postValue("Request failed with code: ${t.message }")
            }
        })
    }

    fun login(user: User){
        api.login(user).enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    val loggedInUser = response.body()
                    //tokenManager.saveToken(token)
                    loggedInUser?.let {
                        status.postValue(true)
                        message.postValue("Request failed with code: ${it}")
                        userData.postValue(it)
                        Log.d("MainActivity", "User created: $it")
                    }
                } else {
                    status.postValue(false)
                    message.postValue("Request failed with code: ${response.code()}")
                    Log.e("MainActivity", "Request failed with code: ${response.code()}:  ${response.body()?.error}")
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Log.e("MainActivity", "Error: ${t.message}")
                status.postValue(false)
                message.postValue("Request failed with code: ${t.message }")
            }
        })
    }

    fun logout(userToken: String){
        val token = "Bearer $userToken"
        api.logout(token).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    val createdUser = response.body()
                    createdUser?.let {
                        status.postValue(true)
                        message.postValue("Request failed with code: ${it}")
                        Log.d("MainActivity", "User created: $it")
                    }
                } else {
                    status.postValue(false)
                    message.postValue("Request failed with code: ${response.code()}")
                    Log.e("MainActivity", "Request failed with code: ${response.code()} ${response.body()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("MainActivity", "Error: ${t.message}")
                status.postValue(false)
                message.postValue("Request failed with code: ${t.message }")
            }
        })
    }

    fun reauthenticate(user: User){
        api.reauthenticate(user).enqueue(object : Callback<TokenResponse> {
            override fun onResponse(call: Call<TokenResponse>, response: Response<TokenResponse>) {
                if (response.isSuccessful) {
                    val res = response.body()
                    res?.let {
                        status.postValue(true)
                        message.postValue("Response: ${it}")
                        newToken.postValue(it)
                        Log.d("MainActivity", "User created: $it")
                    }

                    val responseCode = response.code()
                    code.postValue(responseCode)
                } else {
                    val responseCode = response.code()
                    code.postValue(responseCode)
                    status.postValue(false)
                    message.postValue("Request failed with code: ${response.code()}")
                    Log.e("MainActivity", "Request failed with code: ${response.code()} ${response.body()}")
                }
            }

            override fun onFailure(call: Call<TokenResponse>, t: Throwable) {
                Log.e("MainActivity", "Error: ${t.message}")
                status.postValue(false)
                message.postValue("Request failed with code: ${t.message }")
            }
        })
    }


}