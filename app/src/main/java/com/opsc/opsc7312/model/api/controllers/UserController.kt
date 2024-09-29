package com.opsc.opsc7312.model.api.controllers

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.opsc.opsc7312.model.api.retrofitclients.RetrofitClient
import com.opsc.opsc7312.model.api.retrofitclients.UserRetrofitClient
import com.opsc.opsc7312.model.api.services.AnalyticsService
import com.opsc.opsc7312.model.api.services.UserService
import com.opsc.opsc7312.model.data.model.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserController : ViewModel() {

    // Initializes the API service from Retrofit for making user-related HTTP requests
    private var api: UserService = RetrofitClient.createService<UserService>()

    // LiveData to track the status of network operations (true for success, false for failure)
    val status: MutableLiveData<Boolean> = MutableLiveData()

    // LiveData to store the message returned from an API operation, such as success or error details
    val message: MutableLiveData<String> = MutableLiveData()

    // LiveData to store the user object, either retrieved from the server or updated
    val user: MutableLiveData<User> = MutableLiveData()

    // Retrieves the user's data from the server based on a userToken (for authentication) and user ID

    // This method was adapted from medium
    // https://medium.com/quick-code/working-with-restful-apis-in-android-retrofit-volley-okhttp-eb8d3ec71e06
    // Megha Verma
    // https://medium.com/@meghaverma12
    fun getUser(userToken: String, id: String) {
        // Prepare the authorization token by appending "Bearer" prefix to the user's token
        val token = "Bearer $userToken"

        // Make an asynchronous API call to retrieve user data
        api.getUser(token, id).enqueue(object : Callback<User> {
            // If the API call is successful and returns a response
            override fun onResponse(call: Call<User>, response: Response<User>) {
                // Check if the response status code indicates success (2xx range)
                if (response.isSuccessful) {
                    // Extract the user data from the response body, if available
                    val createdTransaction = response.body()
                    createdTransaction?.let {
                        // If the user data exists, update the LiveData to reflect the success status
                        status.postValue(true)
                        // Post a message indicating that the user was found successfully
                        message.postValue("User found: ${it}")
                        Log.d("MainActivity", "User found: $it")
                    }
                } else {
                    // If the response was not successful, log the failure with the status code
                    status.postValue(false)
                    message.postValue("Request failed with code: ${response.code()}")
                    Log.e("MainActivity", "Request failed with code: ${response.code()}")
                }
            }

            // If the API call fails due to network or other issues
            override fun onFailure(call: Call<User>, t: Throwable) {
                // Log the error message and update the status LiveData to reflect the failure
                Log.e("MainActivity", "Error: ${t.message}")
                status.postValue(false)
                // Store the error message in the LiveData to inform the UI
                message.postValue(t.message)
            }
        })
    }

    // Updates the user's email and username by sending a request to the server

    // This method was adapted from medium
    // https://medium.com/quick-code/working-with-restful-apis-in-android-retrofit-volley-okhttp-eb8d3ec71e06
    // Megha Verma
    // https://medium.com/@meghaverma12
    fun updateEmailAndUsername(userToken: String, id: String, user: User) {
        // Prepare the authorization token
        val token = "Bearer $userToken"

        // Initiate the API call to update the user's email and username, passing the user object
        val call = api.updateEmailAndUsername(token, id, user)

        // Log the request URL for debugging purposes
        val url = call.request().url.toString()
        Log.d("MainActivity", "Request URL: $url")

        // Enqueue the request to execute it asynchronously
        call.enqueue(object : Callback<User> {
            // Handle the server's response when the request succeeds
            override fun onResponse(call: Call<User>, response: Response<User>) {
                // If the response indicates success, update the user information in LiveData
                if (response.isSuccessful) {
                    val updatedUser = response.body()
                    updatedUser?.let {
                        // Update the status to true to indicate success and post the updated user data
                        status.postValue(true)
                        message.postValue("User email and username updated: $it")
                        Log.d("MainActivity", "User email and username updated: $it")
                    }
                } else {
                    // If the request fails, log the error code and update the LiveData accordingly
                    status.postValue(false)
                    message.postValue("Request failed with code: ${response.code()}")
                    Log.e("MainActivity", "Request failed with code: ${response.code()}")
                }
            }

            // Handle cases where the request fails due to network errors or other issues
            override fun onFailure(call: Call<User>, t: Throwable) {
                // Log the error and update the LiveData to reflect the failure
                Log.e("MainActivity", "Error: ${t.message}")
                status.postValue(false)
                message.postValue(t.message)
            }
        })
    }

    // Updates the user's password by sending a request with the new password to the server

    // This method was adapted from medium
    // https://medium.com/quick-code/working-with-restful-apis-in-android-retrofit-volley-okhttp-eb8d3ec71e06
    // Megha Verma
    // https://medium.com/@meghaverma12
    fun updatePassword(userToken: String, id: String, password: String) {
        // Prepare the authorization token
        val token = "Bearer $userToken"

        // Prepare the API call to update the password for the specified user
        val call = api.updatePassword(token, id, password)

        // Log the request URL for debugging purposes
        val url = call.request().url.toString()
        Log.d("MainActivity", "Request URL: $url")

        // Enqueue the request to execute it asynchronously
        call.enqueue(object : Callback<User> {
            // Handle the server's response when the request is successful
            override fun onResponse(call: Call<User>, response: Response<User>) {
                // If the response indicates success, update the LiveData to reflect the change
                if (response.isSuccessful) {
                    val updatedPassword = response.body()
                    updatedPassword?.let {
                        // Update the status and message to reflect successful password update
                        status.postValue(true)
                        message.postValue("User password updated: $it")
                        Log.d("MainActivity", "User password updated: $it")
                    }
                } else {
                    // Log the error and update the LiveData to indicate the request failed
                    status.postValue(false)
                    message.postValue("Request failed with code: ${response.code()}")
                    Log.e("MainActivity", "Request failed with code: ${response.code()}")
                }
            }

            // Handle cases where the request fails due to network errors or other issues
            override fun onFailure(call: Call<User>, t: Throwable) {
                // Log the error and update the LiveData to reflect the failure
                Log.e("MainActivity", "Error: ${t.message}")
                status.postValue(false)
                message.postValue(t.message)
            }
        })
    }
}
