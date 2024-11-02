package com.opsc.opsc7312.model.api.controllers

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.opsc.opsc7312.model.api.retrofitclients.RetrofitClient
import com.opsc.opsc7312.model.api.services.InvestmentService
import com.opsc.opsc7312.model.api.services.StockService
import com.opsc.opsc7312.model.data.model.Investment
import com.opsc.opsc7312.model.data.model.InvestmentResponse
import com.opsc.opsc7312.model.data.model.Stock
import com.opsc.opsc7312.model.data.model.SyncResponse
import com.opsc.opsc7312.model.data.model.Trade
import com.opsc.opsc7312.model.data.model.Transaction
import kotlinx.coroutines.suspendCancellableCoroutine
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.resume

class InvestmentController:ViewModel() {
    // Retrofit API service instance for category-related network requests
    private var api: InvestmentService = RetrofitClient.createService<InvestmentService>()


    // MutableLiveData to track the success or failure status of API requests
    val status: MutableLiveData<Boolean> = MutableLiveData()

    // MutableLiveData to store the response messages or errors from API calls
    val message: MutableLiveData<String> = MutableLiveData()

    // MutableLiveData holding a list of categories fetched from the backend
    val investmentList: MutableLiveData<InvestmentResponse> = MutableLiveData()


    // Fetches all categories associated with a specific user, identified by `id`.
    // Requires an authentication token and the user's ID.
    // Updates the `categoryList`, `status`, and `message` based on the response.
    fun getUserInvestments(userToken: String, userId: String) {
        // This method was adapted from medium
        // https://medium.com/quick-code/working-with-restful-apis-in-android-retrofit-volley-okhttp-eb8d3ec71e06
        // Megha Verma
        // https://medium.com/@meghaverma12
        val token = "Bearer $userToken"
        val call = api.getInvestments(token, userId)

        // Logging the request URL for debugging purposes
        val url = call.request().url.toString()
        //Log.d("MainActivity", "Request URL: $url")

        // Asynchronously executes the API call to retrieve categories
        call.enqueue(object : Callback<InvestmentResponse> {
            // Called when the server responds to the request
            override fun onResponse(call: Call<InvestmentResponse>, response: Response<InvestmentResponse>) {
                if (response.isSuccessful) {
                    // If the response is successful, update the category list and status
                    val investments = response.body()
                    investments?.let {
                        investmentList.postValue(it)
                        status.postValue(true)
                        message.postValue("Categories retrieved")
                        //Log.d("MainActivity", "Categories: $it")
                    }
                } else {
                    // Handle unsuccessful responses, e.g., a 4xx or 5xx status code
                    status.postValue(false)
                    message.postValue("Request failed with code: ${response.code()}")
                }
            }

            // Called when the API call fails, e.g., due to network issues
            override fun onFailure(call: Call<InvestmentResponse>, t: Throwable) {
                //Log.e("MainActivity", "Error: ${t.message}")
                status.postValue(false)
                message.postValue(t.message)
            }
        })
    }


    fun buyInvestment(userToken: String, trade: Trade) {
        // This method was adapted from medium
        // https://medium.com/quick-code/working-with-restful-apis-in-android-retrofit-volley-okhttp-eb8d3ec71e06
        // Megha Verma
        // https://medium.com/@meghaverma12
        val token = "Bearer $userToken"  // Prepare the token for authorization
        // Make an API call to create a new transaction
        api.buyInvestment(token, trade).enqueue(object : Callback<Void> {
            // Callback when the response is received successfully
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                // Check if the response is successful
                if (response.isSuccessful) {
                    status.postValue(true)  // Mark the operation as successful
                    message.postValue("investment buy successful")  // Set a success message
                    Log.d("MainActivity", "investment buy successful")

                } else {
                    // Handle unsuccessful responses
                    status.postValue(false)  // Mark the operation as failed
                    message.postValue("Request failed with code: ${response.code()}")  // Set an error message
                    Log.e("MainActivity", "Request failed with code: ${response.code()}")
                }
            }

            // Callback when the API call fails
            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("MainActivity", "Error: ${t.message}")  // Log the error message
                status.postValue(false)  // Mark the operation as failed
                message.postValue(t.message)  // Set the error message in LiveData
            }
        })
    }


    fun sellInvestment(userToken: String, trade: Trade) {
        // This method was adapted from medium
        // https://medium.com/quick-code/working-with-restful-apis-in-android-retrofit-volley-okhttp-eb8d3ec71e06
        // Megha Verma
        // https://medium.com/@meghaverma12
        val token = "Bearer $userToken"  // Prepare the token for authorization
        // Make an API call to create a new transaction
        api.sellInvestment(token, trade).enqueue(object : Callback<Void> {
            // Callback when the response is received successfully
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                // Check if the response is successful
                if (response.isSuccessful) {
                    status.postValue(true)  // Mark the operation as successful
                    message.postValue("investment sell successful")  // Set a success message
                    Log.d("MainActivity", "investment sell successful")

                } else {
                    // Handle unsuccessful responses
                    status.postValue(false)  // Mark the operation as failed
                    message.postValue("Request failed with code: ${response.code()}")  // Set an error message
                    Log.e("MainActivity", "Request failed with code: ${response.code()}")
                }
            }

            // Callback when the API call fails
            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("MainActivity", "Error: ${t.message}")  // Log the error message
                status.postValue(false)  // Mark the operation as failed
                message.postValue(t.message)  // Set the error message in LiveData
            }
        })
    }

    // This method was adapted from medium
    // https://medium.com/quick-code/working-with-restful-apis-in-android-retrofit-volley-okhttp-eb8d3ec71e06
    // Megha Verma
    // https://medium.com/@meghaverma12
    suspend fun getUserInvestment(userToken: String, userId: String, symbol: String): Investment? {
        val token = "Bearer $userToken"  // Format the token for authorization
        val call = api.getUserInvestment(token, userId, symbol)  // Make the API call to fetch goals

        return suspendCancellableCoroutine { continuation ->
            // Log the request URL for debugging
            val url = call.request().url.toString()
            Log.d("MainActivity", "Request URL: $url")

            // Execute the API call asynchronously
            call.enqueue(object : Callback<Investment> {
                override fun onResponse(call: Call<Investment>, response: Response<Investment>) {
                    if (response.isSuccessful) {
                        // Resume coroutine with the response body
                        continuation.resume(response.body())
                        message.postValue("investment retrieved")
                    } else {
                        // Parse and log error, then resume with null
                        val errorMessage = if (response.errorBody() != null) {
                            try {
                                val errorResponse = Gson().fromJson(response.errorBody()?.string(), SyncResponse::class.java)
                                "Error syncing transactions: ${errorResponse.message}"
                            } catch (e: Exception) {
                                "Request failed with code: ${response.code()}, but failed to parse error response."
                            }
                        } else {
                            "Request failed with code: ${response.code()}, message: ${response.message()}"
                        }

                        Log.e("get investment", errorMessage)
                        message.postValue(errorMessage)
                        continuation.resume(null)
                    }
                }

                override fun onFailure(call: Call<Investment>, t: Throwable) {
                    // Log the error and resume coroutine with null
                    val errorMessage = "Sync failed: ${t.message ?: "Unknown error"}\nCall: $call"
                    Log.e("TransactionSync", errorMessage)
                    message.postValue(t.message)
                    continuation.resume(null)
                }
            })

            // Cancel call if the coroutine is cancelled
            continuation.invokeOnCancellation {
                call.cancel()
            }
        }
    }
}