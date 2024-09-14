package com.opsc.opsc7312.model.api.controllers

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.opsc.opsc7312.model.api.retrofitclients.UserRetrofitClient
import com.opsc.opsc7312.model.data.model.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserController : ViewModel() {
    private var api = UserRetrofitClient.apiService

    val status: MutableLiveData<Boolean> = MutableLiveData()
    val message: MutableLiveData<String> = MutableLiveData()

    val user: MutableLiveData<User> = MutableLiveData()


    fun getUser(userToken: String, id: String){
        val token = "Bearer $userToken"
        api.getUser(token, id).enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    val createdTransaction = response.body()
                    createdTransaction?.let {
                        status.postValue(true)
                        message.postValue("Request failed with code: ${it}")
                        Log.d("MainActivity", "User found: $it")
                    }
                } else {
                    status.postValue(false)
                    message.postValue("Request failed with code: ${response.code()}")
                    Log.e("MainActivity", "Request failed with code: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Log.e("MainActivity", "Error: ${t.message}")
                status.postValue(false)
                message.postValue(t.message)
            }
        })
    }

    fun updateEmailAndUsername(userToken: String, id: String, user: User){
        val token = "Bearer $userToken"
        api.updateEmailAndUsername(token, id, user).enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    val updatedUser = response.body()
                    updatedUser?.let {
                        status.postValue(true)
                        message.postValue("Request failed with code: ${it}")
                        Log.d("MainActivity", "User email and password updated: $it")
                    }
                } else {
                    status.postValue(false)
                    message.postValue("Request failed with code: ${response.code()}")
                    Log.e("MainActivity", "Request failed with code: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Log.e("MainActivity", "Error: ${t.message}")
                status.postValue(false)
                message.postValue(t.message)
            }
        })
    }

    fun updatePassword(userToken: String, id: String, password: String){
        val token = "Bearer $userToken"
        api.updatePassword(token, id, password).enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    val updatedPassword = response.body()
                    updatedPassword?.let {
                        status.postValue(true)
                        message.postValue("Request failed with code: ${it}")
                        Log.d("MainActivity", "User email and password updated: $it")
                    }
                } else {
                    status.postValue(false)
                    message.postValue("Request failed with code: ${response.code()}")
                    Log.e("MainActivity", "Request failed with code: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Log.e("MainActivity", "Error: ${t.message}")
                status.postValue(false)
                message.postValue(t.message)
            }
        })
    }

}