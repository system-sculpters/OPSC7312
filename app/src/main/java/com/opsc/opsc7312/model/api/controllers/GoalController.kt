package com.opsc.opsc7312.model.api.controllers

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.opsc.opsc7312.model.api.retrofitclients.GoalRetrofitClient
import com.opsc.opsc7312.model.data.model.Goal
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GoalController : ViewModel() {
    private var api = GoalRetrofitClient.apiService

    val status: MutableLiveData<Boolean> = MutableLiveData()
    val message: MutableLiveData<String> = MutableLiveData()

    val goalList: MutableLiveData<List<Goal>> = MutableLiveData()

    fun getAllGoals(userToken: String, id: String){
        val token = "Bearer $userToken"
        val call = api.getGoals(token, id)

        val url = call.request().url.toString()
        Log.d("MainActivity", "Request URL: $url")
        call.enqueue(object :
            Callback<List<Goal>> {
            override fun onResponse(call: Call<List<Goal>>, response: Response<List<Goal>>) {
                if (response.isSuccessful) {
                    val Goals = response.body()
                    Goals?.let {
                        goalList.postValue(it)
                        status.postValue(true)
                        message.postValue("Goals retrieved")
                        Log.d("MainActivity", "Goals: $it")
                    }
                } else {
                    Log.e("MainActivity", "Request failed with code: ${response.code()}")
                    goalList.postValue(listOf())
                    status.postValue(false)
                    message.postValue("Request failed with code: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<Goal>>, t: Throwable) {
                Log.e("MainActivity", "Error: ${t.message}")
                goalList.postValue(listOf())
                status.postValue(false)
                message.postValue(t.message)
            }
        })
    }

    fun createGoal(userToken: String, Goal: Goal){
        val token = "Bearer $userToken"
        api.createGoal(token, Goal).enqueue(object : Callback<Goal> {
            override fun onResponse(call: Call<Goal>, response: Response<Goal>) {
                if (response.isSuccessful) {
                    val createdGoal = response.body()
                    createdGoal?.let {
                        status.postValue(true)
                        message.postValue("Request failed with code: ${it}")
                        Log.d("MainActivity", "Goal created: $it")
                    }
                } else {
                    status.postValue(false)
                    message.postValue("Request failed with code: ${response.code()}")
                    Log.e("MainActivity", "Request failed with code: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Goal>, t: Throwable) {
                Log.e("MainActivity", "Error: ${t.message}")
                status.postValue(false)
                message.postValue(t.message)
            }
        })
    }

    fun updateGoal(userToken: String,id: String, Goal: Goal){
        val token = "Bearer $userToken"
        api.updateGoal(token, id, Goal).enqueue(object : Callback<Goal> {
            override fun onResponse(call: Call<Goal>, response: Response<Goal>) {
                if (response.isSuccessful) {
                    val createdGoal = response.body()
                    createdGoal?.let {
                        status.postValue(true)
                        message.postValue("Request failed with code: ${it}")
                        Log.d("MainActivity", "Goal updated: $it")
                    }
                } else {
                    status.postValue(false)
                    message.postValue("Request failed with code: ${response.code()}")
                    Log.e("MainActivity", "Request failed with code: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Goal>, t: Throwable) {
                Log.e("MainActivity", "Error: ${t.message}")
                status.postValue(false)
                message.postValue(t.message)
            }
        })
    }

    fun deleteGoal(userToken: String, id: String) {
        val token = "Bearer $userToken"
        api.deleteGoal(token, id).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    // The Goal was successfully deleted
                    status.postValue(true)
                    message.postValue("Goal deleted successfully.")
                    Log.d("MainActivity", "Goal deleted successfully.")
                } else {
                    // The request was not successful, handle the error
                    status.postValue(false)
                    message.postValue("Request failed with code: ${response.code()}")
                    Log.e("MainActivity", "Request failed with code: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                // Handle failure scenario, like network issues
                Log.e("MainActivity", "Error: ${t.message}")
                status.postValue(false)
                message.postValue(t.message)
            }
        })
    }
}