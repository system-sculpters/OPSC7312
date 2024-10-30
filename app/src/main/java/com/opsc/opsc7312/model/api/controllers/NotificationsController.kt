package com.opsc.opsc7312.model.api.controllers

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.opsc.opsc7312.model.api.retrofitclients.RetrofitClient
import com.opsc.opsc7312.model.api.services.AnalyticsService
import com.opsc.opsc7312.model.api.services.NotificationsService
import com.opsc.opsc7312.model.data.model.AnalyticsResponse
import com.opsc.opsc7312.model.data.model.Notification
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NotificationsController : ViewModel() {

    // api: Retrofit client for making API requests to retrieve analytics data.
    var api: NotificationsService = RetrofitClient.createService<NotificationsService>()

    // status: LiveData that holds the status of the API request (true for success, false for failure).
    val status: MutableLiveData<Boolean> = MutableLiveData()

    // message: LiveData for displaying any message regarding the API call (success/failure messages).
    val message: MutableLiveData<String> = MutableLiveData()

    // analytics: LiveData that holds the response data from the API, containing analytics information.
    val notifications: MutableLiveData<List<Notification>> = MutableLiveData()

    // Function to fetch all analytics data using the provided user token and user ID.
    // Makes an API call to retrieve analytics data, and updates LiveData based on the response.

    // This method was adapted from medium
    // https://medium.com/quick-code/working-with-restful-apis-in-android-retrofit-volley-okhttp-eb8d3ec71e06
    // Megha Verma
    // https://medium.com/@meghaverma12

    fun fetchAllNotifications(userToken: String, id: String) {
        // Create a Bearer token using the provided user token.
        val token = "Bearer $userToken"

        // Make the API call to retrieve all analytics for the given user ID.
        val call = api.getAllNotifications(token, id)

        // Enqueue the API call to execute asynchronously.
        call.enqueue(object : Callback<List<Notification>> {

            // Called when the API call receives a response.
            override fun onResponse(call: Call<List<Notification>>, response: Response<List<Notification>>) {
                if (response.isSuccessful) {
                    // If the response is successful, extract the analytics data.
                    val apiResponse = response.body()

                    // If the response body is not null, update the LiveData objects.
                    apiResponse?.let {
                        // Post the API response data to the analytics LiveData.
                        notifications.postValue(it)

                        // Indicate the request was successful by posting 'true' to status.
                        status.postValue(true)

                        // Post a success message to message LiveData.
                        message.postValue("Notifications retrieved")

                        // Log example: Print the first month's income and label.
                        //Log.d("api response", "Month: ${transactionsByMonth[0].label}, Income: ${transactionsByMonth[0].income}")
                    }
                } else {
                    // If the response failed (e.g., non-2xx status code), log and handle the error.
                    //Log.e("MainActivity", "Request failed with code: ${response.code()}")

                    // Post 'false' to status LiveData to indicate failure.
                    status.postValue(false)

                    // Post the failure message to message LiveData.
                    message.postValue("Request failed with code: ${response.code()}")

                    // Log the error response code.
                    //Log.d("analytics error", "Error: ${response.code()}")
                }
            }

            // Called when the API call fails due to network issues or other reasons.
            override fun onFailure(call: Call<List<Notification>>, t: Throwable) {
                // Log the failure reason.
                //Log.d("AnalyticsController onFailure", "API call failed: ${t.message}")

                // Post 'false' to status LiveData to indicate failure.
                status.postValue(false)

                // Post the failure message (e.g., network error) to message LiveData.
                message.postValue(t.message)
            }
        })
    }
}