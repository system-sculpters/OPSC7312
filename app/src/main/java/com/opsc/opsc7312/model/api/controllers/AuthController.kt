package com.opsc.opsc7312.model.api.controllers

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.opsc.opsc7312.model.api.retrofitclients.RetrofitClient
import com.opsc.opsc7312.model.api.services.AuthService
import com.opsc.opsc7312.model.data.model.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// AuthController class responsible for handling user authentication and registration.
// Extends ViewModel to maintain data across configuration changes and manage UI-related data in a lifecycle-conscious way.
class AuthController : ViewModel() {

    // api: Retrofit client for making API requests related to authentication.
    private var api: AuthService = RetrofitClient.createService<AuthService>()

    // status: LiveData that holds the status of API requests (true for success, false for failure).
    val status: MutableLiveData<Boolean> = MutableLiveData()

    // message: LiveData that contains success or error messages based on API responses.
    val message: MutableLiveData<String> = MutableLiveData()

    // userData: LiveData holding the user information retrieved during registration or login.
    val userData: MutableLiveData<User> = MutableLiveData()

    // Function to handle user registration.
    // Takes a User object, makes a registration API call, and updates LiveData based on the response.
    fun register(user: User) {
        api.register(user).enqueue(object : Callback<User> {

            // Called when the API call receives a response.
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    // If registration is successful, retrieve the created user and update LiveData.
                    val createdUser = response.body()
                    createdUser?.let {
                        status.postValue(true)  // Update status to indicate success.
                        message.postValue("User registered successfully")  // Post success message.
                        userData.postValue(it)  // Post the created user data.
                        Log.d("MainActivity", "User created: $it")
                    }
                } else {
                    // Handle failure when the response is not successful (e.g., non-2xx status code).
                    status.postValue(false)
                    message.postValue("Request failed with code: ${response.code()}: ${response.body()?.error}")
                    Log.e("MainActivity", "Request failed with code: ${response.code()}: ${response.body()?.error}")
                }
            }

            // Called when the API call fails due to network issues or other errors.
            override fun onFailure(call: Call<User>, t: Throwable) {
                Log.e("MainActivity", "Error: ${t.message}")
                status.postValue(false)  // Update status to indicate failure.
                message.postValue(t.message)  // Post failure message.
            }
        })
    }

    // Function to handle user login.
    // Takes a User object, makes a login API call, and updates LiveData based on the response.
    fun login(user: User) {
        api.login(user).enqueue(object : Callback<User> {

            // Called when the API call receives a response.
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    // If login is successful, retrieve the logged-in user and update LiveData.
                    val loggedInUser = response.body()
                    loggedInUser?.let {
                        status.postValue(true)  // Update status to indicate success.
                        message.postValue("User logged in successfully")  // Post success message.
                        userData.postValue(it)  // Post the logged-in user data.
                        Log.d("MainActivity", "User logged in: $it")
                    }
                } else {
                    // Handle failure when the response is not successful.
                    status.postValue(false)
                    message.postValue("Request failed with code: ${response.code()}")
                    Log.e("MainActivity", "Request failed with code: ${response.code()}: ${response.body()?.error}")
                }
            }

            // Called when the API call fails due to network issues or other errors.
            override fun onFailure(call: Call<User>, t: Throwable) {
                Log.e("MainActivity", "Error: ${t.message}")
                status.postValue(false)  // Update status to indicate failure.
                message.postValue(t.message)  // Post failure message.
            }
        })
    }

    // Function to handle user logout.
    // Takes a user token, makes a logout API call, and updates LiveData based on the response.
    fun logout(userToken: String) {
        val token = "Bearer $userToken"
        api.logout(token).enqueue(object : Callback<Void> {

            // Called when the API call receives a response.
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Log.d("Logout", "Logout successful")
                    status.postValue(true)  // Update status to indicate success.
                    message.postValue("User logged out successfully")  // Post success message.
                } else {
                    // Handle failure during logout.
                    val errorBody = response.errorBody()?.string()
                    Log.e("Logout", "Logout failed with code: ${response.code()} - $errorBody")
                    status.postValue(false)
                    message.postValue("Logout failed with code: ${response.code()} - $errorBody")
                }
            }

            // Called when the API call fails due to network issues or other errors.
            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("Logout", "Network error: ${t.message}")
                status.postValue(false)  // Update status to indicate failure.
                message.postValue("Network error: ${t.message}")  // Post failure message.
            }
        })
    }

    // Function to handle Single Sign-On (SSO) registration.
    // Takes a User object, makes a registration API call with SSO, and updates LiveData based on the response.
    fun registerWithSSO(user: User) {
        api.registerWithSSO(user).enqueue(object : Callback<User> {

            // Called when the API call receives a response.
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    // If SSO registration is successful, retrieve the created user.
                    val createdUser = response.body()
                    createdUser?.let {
                        status.postValue(true)  // Update status to indicate success.
                        message.postValue("User registered with SSO successfully")  // Post success message.
                        Log.d("MainActivity", "User created with SSO: $it")
                    }
                } else {
                    // Handle failure during SSO registration.
                    status.postValue(false)
                    message.postValue("Request failed with code: ${response.code()}: ${response.body()?.error}")
                    Log.e("MainActivity", "Request failed with code: ${response.code()}: ${response.body()?.error}")
                }
            }

            // Called when the API call fails due to network issues or other errors.
            override fun onFailure(call: Call<User>, t: Throwable) {
                Log.e("MainActivity", "Error: ${t.message}")
                status.postValue(false)  // Update status to indicate failure.
                message.postValue(t.message)  // Post failure message.
            }
        })
    }

    // Function to handle Single Sign-On (SSO) login.
    // Takes a User object, makes a login API call with SSO, and updates LiveData based on the response.
    fun loginWithSSO(user: User) {
        api.loginWithSSO(user).enqueue(object : Callback<User> {

            // Called when the API call receives a response.
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    // If SSO login is successful, retrieve the logged-in user.
                    val loggedInUser = response.body()
                    loggedInUser?.let {
                        status.postValue(true)  // Update status to indicate success.
                        message.postValue("User logged in with SSO successfully")  // Post success message.
                        userData.postValue(it)  // Post the logged-in user data.
                        Log.d("MainActivity", "User logged in with SSO: $it")
                    }
                } else {
                    // Handle failure during SSO login.
                    status.postValue(false)
                    message.postValue("Request failed with code: ${response.code()}")
                    Log.e("MainActivity", "Request failed with code: ${response.code()}: ${response.body()?.error}")
                }
            }

            // Called when the API call fails due to network issues or other errors.
            override fun onFailure(call: Call<User>, t: Throwable) {
                Log.e("MainActivity", "Error: ${t.message}")
                status.postValue(false)  // Update status to indicate failure.
                message.postValue(t.message)  // Post failure message.
            }
        })
    }
}
