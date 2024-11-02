package com.opsc.opsc7312.model.api.controllers

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.opsc.opsc7312.model.api.retrofitclients.RetrofitClient
import com.opsc.opsc7312.model.api.services.CategoryService
import com.opsc.opsc7312.model.api.services.StockService
import com.opsc.opsc7312.model.data.model.Category
import com.opsc.opsc7312.model.data.model.Stock
import com.opsc.opsc7312.model.data.model.StockHistory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StockController : ViewModel() {

    // Retrofit API service instance for category-related network requests
    private var api: StockService = RetrofitClient.createService<StockService>()


    // MutableLiveData to track the success or failure status of API requests
    val status: MutableLiveData<Boolean> = MutableLiveData()

    // MutableLiveData to store the response messages or errors from API calls
    val message: MutableLiveData<String> = MutableLiveData()

    // MutableLiveData holding a list of categories fetched from the backend
    val stockList: MutableLiveData<List<Stock>> = MutableLiveData()

    val stockHistory: MutableLiveData<List<StockHistory>> = MutableLiveData()

    // Fetches all categories associated with a specific user, identified by `id`.
    // Requires an authentication token and the user's ID.
    // Updates the `categoryList`, `status`, and `message` based on the response.
    fun getAllStocks(userToken: String) {
        // This method was adapted from medium
        // https://medium.com/quick-code/working-with-restful-apis-in-android-retrofit-volley-okhttp-eb8d3ec71e06
        // Megha Verma
        // https://medium.com/@meghaverma12
        val token = "Bearer $userToken"
        val call = api.getStocks(token)

        // Logging the request URL for debugging purposes
        val url = call.request().url.toString()
        //Log.d("MainActivity", "Request URL: $url")

        // Asynchronously executes the API call to retrieve categories
        call.enqueue(object : Callback<List<Stock>> {
            // Called when the server responds to the request
            override fun onResponse(call: Call<List<Stock>>, response: Response<List<Stock>>) {
                if (response.isSuccessful) {
                    // If the response is successful, update the category list and status
                    val categories = response.body()
                    categories?.let {
                        stockList.postValue(it)
                        status.postValue(true)
                        message.postValue("Categories retrieved")
                        //Log.d("MainActivity", "Categories: $it")
                    }
                } else {
                    // Handle unsuccessful responses, e.g., a 4xx or 5xx status code
                    stockList.postValue(listOf())
                    //Log.e("MainActivity", "Request failed with code: ${response.code()}")
                    status.postValue(false)
                    message.postValue("Request failed with code: ${response.code()}")
                }
            }

            // Called when the API call fails, e.g., due to network issues
            override fun onFailure(call: Call<List<Stock>>, t: Throwable) {
                stockList.postValue(listOf())
                //Log.e("MainActivity", "Error: ${t.message}")
                status.postValue(false)
                message.postValue(t.message)
            }
        })
    }

    // Fetches all stockHistory associated with a specific user, identified by `id`.
    // Requires an authentication token and the user's ID.
    // Updates the `stockHistory`, `status`, and `message` based on the response.
    fun getStockHistory(userToken: String, symbol: String) {
        // This method was adapted from medium
        // https://medium.com/quick-code/working-with-restful-apis-in-android-retrofit-volley-okhttp-eb8d3ec71e06
        // Megha Verma
        // https://medium.com/@meghaverma12
        val token = "Bearer $userToken"
        val call = api.getStockHistory(token, symbol)

        // Logging the request URL for debugging purposes
        val url = call.request().url.toString()
        //Log.d("MainActivity", "Request URL: $url")

        // Asynchronously executes the API call to retrieve categories
        call.enqueue(object : Callback<List<StockHistory>> {
            // Called when the server responds to the request
            override fun onResponse(call: Call<List<StockHistory>>, response: Response<List<StockHistory>>) {
                if (response.isSuccessful) {
                    // If the response is successful, update the category list and status
                    val categories = response.body()
                    categories?.let {
                        stockHistory.postValue(it)
                        status.postValue(true)
                        message.postValue("Categories retrieved")
                        //Log.d("MainActivity", "Categories: $it")
                    }
                } else {
                    status.postValue(false)
                    message.postValue("Request failed with code: ${response.code()}")
                }
            }

            // Called when the API call fails, e.g., due to network issues
            override fun onFailure(call: Call<List<StockHistory>>, t: Throwable) {
                status.postValue(false)
                message.postValue(t.message)
            }
        })
    }
}