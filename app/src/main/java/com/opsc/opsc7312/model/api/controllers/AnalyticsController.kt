package com.opsc.opsc7312.model.api.controllers

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.opsc.opsc7312.model.api.retrofitclients.AnalyticsRetrofitClient
import com.opsc.opsc7312.model.data.model.AnalyticsResponse
import com.opsc.opsc7312.model.data.model.Goal
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AnalyticsController: ViewModel() {
    private var api = AnalyticsRetrofitClient.apiService

    val status: MutableLiveData<Boolean> = MutableLiveData()
    val message: MutableLiveData<String> = MutableLiveData()

    val analytics: MutableLiveData<AnalyticsResponse> = MutableLiveData()

    fun fetchAllAnalytics(userToken: String, id: String) {
        val token = "Bearer $userToken"
        val call = api.getAllAnalytics(token, id)

        call.enqueue(object : Callback<AnalyticsResponse> {
            override fun onResponse(call: Call<AnalyticsResponse>, response: Response<AnalyticsResponse>) {
                if (response.isSuccessful) {
                    // Handle the successful response
                    val apiResponse = response.body()

                    apiResponse?.let {
                        val transactionsByMonth = it.transactionsByMonth
                        val dailyTransactions = it.dailyTransactions
                        val categoryStats = it.categoryStats
                        analytics.postValue(it)
                        status.postValue(true)
                        message.postValue("Goals retrieved")
                        // Example: Print out the first month's income
                        Log.d("api response","Month: ${transactionsByMonth[0].label}, Income: ${transactionsByMonth[0].income}")
                    }
                } else {
                    Log.e("MainActivity", "Request failed with code: ${response.code()}")
                    //analytics.postValue(null)
                    status.postValue(false)
                    message.postValue("Request failed with code: ${response.code()}")
                    // Handle unsuccessful response (like 4xx or 5xx error)
                    Log.d("analytics error","Error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<AnalyticsResponse>, t: Throwable) {
                // Handle network or other errors
                Log.d("AnalyticsController onFailure", "API call failed: ${t.message}")
                //analytics.postValue(null)
                status.postValue(false)
                message.postValue(t.message)
            }
        })
    }
}