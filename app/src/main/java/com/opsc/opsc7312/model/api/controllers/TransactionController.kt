package com.opsc.opsc7312.model.api.controllers

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.opsc.opsc7312.model.api.retrofitclients.RetrofitClient
import com.opsc.opsc7312.model.api.retrofitclients.TransactionRetrofitClient
import com.opsc.opsc7312.model.api.services.AnalyticsService
import com.opsc.opsc7312.model.api.services.TransactionService
import com.opsc.opsc7312.model.data.model.Transaction
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TransactionController : ViewModel() {
    // Instance of the API service used to communicate with the backend for transaction-related operations
    private var api: TransactionService = RetrofitClient.createService<TransactionService>()

    // LiveData object used to monitor the status of transaction operations (true if successful, false otherwise)
    val status: MutableLiveData<Boolean> = MutableLiveData()

    // LiveData object used to hold messages such as success or error messages from operations
    val message: MutableLiveData<String> = MutableLiveData()

    // LiveData object that holds a list of transactions retrieved from the server
    val transactionList: MutableLiveData<List<Transaction>> = MutableLiveData()

    // Function to retrieve all transactions for a user
    fun getAllTransactions(userToken: String, id: String) {
        val token = "Bearer $userToken"  // Prepare the token for authorization
        val call = api.getTransactions(token, id)  // API call to fetch the user's transactions

        // Log the request URL for debugging purposes
        val url = call.request().url.toString()
        Log.d("MainActivity", "Request URL: $url")

        // Execute the API call asynchronously
        call.enqueue(object : Callback<List<Transaction>> {
            // Callback when the response is received successfully
            override fun onResponse(call: Call<List<Transaction>>, response: Response<List<Transaction>>) {
                // Check if the response is successful (status code 200-299)
                if (response.isSuccessful) {
                    val transactions = response.body()
                    transactions?.let {
                        // Update the LiveData with the retrieved list of transactions
                        transactionList.postValue(it)
                        status.postValue(true)  // Mark the operation as successful
                        message.postValue("Transactions retrieved")  // Set a success message
                        Log.d("MainActivity", "Transactions: $it")
                    }
                } else {
                    // Handle the case when the request was not successful (error codes like 400 or 500)
                    Log.e("MainActivity", "Request failed with code: ${response.code()}")
                    transactionList.postValue(listOf())  // Clear the list if the operation fails
                    status.postValue(false)  // Mark the operation as failed
                    message.postValue("Request failed with code: ${response.code()}")  // Set an error message
                }
            }

            // Callback when the API call fails (e.g., due to network issues)
            override fun onFailure(call: Call<List<Transaction>>, t: Throwable) {
                Log.e("MainActivity", "Error: ${t.message}")  // Log the error message
                transactionList.postValue(listOf())  // Clear the transaction list on failure
                status.postValue(false)  // Mark the operation as failed
                message.postValue(t.message)  // Set the error message in LiveData
            }
        })
    }

    // Function to create a new transaction for a user
    fun createTransaction(userToken: String, transaction: Transaction) {
        val token = "Bearer $userToken"  // Prepare the token for authorization
        // Make an API call to create a new transaction
        api.createTransaction(token, transaction).enqueue(object : Callback<Transaction> {
            // Callback when the response is received successfully
            override fun onResponse(call: Call<Transaction>, response: Response<Transaction>) {
                // Check if the response is successful
                if (response.isSuccessful) {
                    val createdTransaction = response.body()
                    createdTransaction?.let {
                        status.postValue(true)  // Mark the operation as successful
                        message.postValue("Transaction created: $it")  // Set a success message
                        Log.d("MainActivity", "Transaction created: $it")
                    }
                } else {
                    // Handle unsuccessful responses
                    status.postValue(false)  // Mark the operation as failed
                    message.postValue("Request failed with code: ${response.code()}")  // Set an error message
                    Log.e("MainActivity", "Request failed with code: ${response.code()}")
                }
            }

            // Callback when the API call fails
            override fun onFailure(call: Call<Transaction>, t: Throwable) {
                Log.e("MainActivity", "Error: ${t.message}")  // Log the error message
                status.postValue(false)  // Mark the operation as failed
                message.postValue(t.message)  // Set the error message in LiveData
            }
        })
    }

    // Function to update an existing transaction by its ID
    fun updateTransaction(userToken: String, id: String, transaction: Transaction) {
        val token = "Bearer $userToken"  // Prepare the token for authorization
        // Make an API call to update an existing transaction
        api.updateTransaction(token, id, transaction).enqueue(object : Callback<Transaction> {
            // Callback when the response is received successfully
            override fun onResponse(call: Call<Transaction>, response: Response<Transaction>) {
                // Check if the response is successful
                if (response.isSuccessful) {
                    val updatedTransaction = response.body()
                    updatedTransaction?.let {
                        status.postValue(true)  // Mark the operation as successful
                        message.postValue("Transaction updated: $it")  // Set a success message
                        Log.d("MainActivity", "Transaction updated: $it")
                    }
                } else {
                    // Handle unsuccessful responses
                    status.postValue(false)  // Mark the operation as failed
                    message.postValue("Request failed with code: ${response.code()}")  // Set an error message
                    Log.e("MainActivity", "Request failed with code: ${response.code()}")
                }
            }

            // Callback when the API call fails
            override fun onFailure(call: Call<Transaction>, t: Throwable) {
                Log.e("MainActivity", "Error: ${t.message}")  // Log the error message
                status.postValue(false)  // Mark the operation as failed
                message.postValue(t.message)  // Set the error message in LiveData
            }
        })
    }

    // Function to delete an existing transaction by its ID
    fun deleteTransaction(userToken: String, id: String) {
        val token = "Bearer $userToken"  // Prepare the token for authorization
        // Make an API call to delete a transaction
        api.deleteTransaction(token, id).enqueue(object : Callback<Void> {
            // Callback when the response is received successfully
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                // Check if the response is successful
                if (response.isSuccessful) {
                    status.postValue(true)  // Mark the operation as successful
                    message.postValue("Transaction deleted successfully.")  // Set a success message
                    Log.d("MainActivity", "Transaction deleted successfully.")
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
}
