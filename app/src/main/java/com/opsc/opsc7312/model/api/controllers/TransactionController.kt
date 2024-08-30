package com.opsc.opsc7312.model.api.controllers

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.opsc.opsc7312.model.api.retrofitclients.TransactionRetrofitClient
import com.opsc.opsc7312.model.data.Transaction
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TransactionController : ViewModel() {
    private var api = TransactionRetrofitClient.apiService

    val status: MutableLiveData<Boolean> = MutableLiveData()
    val message: MutableLiveData<String> = MutableLiveData()

    val transactionList: MutableLiveData<List<Transaction>> = MutableLiveData()

    fun getAllTransactions(id: String){
        val call = api.getTransactions(id)

        val url = call.request().url.toString()
        Log.d("MainActivity", "Request URL: $url")
        call.enqueue(object :
            Callback<List<Transaction>> {
            override fun onResponse(call: Call<List<Transaction>>, response: Response<List<Transaction>>) {
                if (response.isSuccessful) {
                    val transactions = response.body()
                    transactions?.let {

                        transactionList.postValue(it)
                        status.postValue(true)
                        message.postValue("Transactions retrieved")
                        Log.d("MainActivity", "Transactions: $it")
                    }
                } else {
                    Log.e("MainActivity", "Request failed with code: ${response.code()}")
                    transactionList.postValue(listOf())
                    status.postValue(true)
                    message.postValue("Request failed with code: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<Transaction>>, t: Throwable) {
                Log.e("MainActivity", "Error: ${t.message}")
                transactionList.postValue(listOf())
                status.postValue(false)
                message.postValue("Request failed with code: ${t.message }")
            }
        })
    }

    fun createTransaction(Transaction: Transaction){
        api.createTransaction(Transaction).enqueue(object : Callback<Transaction> {
            override fun onResponse(call: Call<Transaction>, response: Response<Transaction>) {
                if (response.isSuccessful) {
                    val createdTransaction = response.body()
                    createdTransaction?.let {
                        status.postValue(true)
                        message.postValue("Request failed with code: ${it}")
                        Log.d("MainActivity", "Transaction created: $it")
                    }
                } else {
                    status.postValue(true)
                    message.postValue("Request failed with code: ${response.code()}")
                    Log.e("MainActivity", "Request failed with code: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Transaction>, t: Throwable) {
                Log.e("MainActivity", "Error: ${t.message}")
                status.postValue(false)
                message.postValue("Request failed with code: ${t.message }")
            }
        })
    }

    fun updateTransaction(id: String, Transaction: Transaction){
        api.updateTransaction(id, Transaction).enqueue(object : Callback<Transaction> {
            override fun onResponse(call: Call<Transaction>, response: Response<Transaction>) {
                if (response.isSuccessful) {
                    val createdTransaction = response.body()
                    createdTransaction?.let {
                        status.postValue(true)
                        message.postValue("Request failed with code: ${it}")
                        Log.d("MainActivity", "Transaction updated: $it")
                    }
                } else {
                    status.postValue(true)
                    message.postValue("Request failed with code: ${response.code()}")
                    Log.e("MainActivity", "Request failed with code: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Transaction>, t: Throwable) {
                Log.e("MainActivity", "Error: ${t.message}")
                status.postValue(false)
                message.postValue("Request failed with code: ${t.message }")
            }
        })
    }

    fun deleteTransaction(id: String) {
        api.deleteTransaction(id).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    // The Transaction was successfully deleted
                    status.postValue(true)
                    message.postValue("Transaction deleted successfully.")
                    Log.d("MainActivity", "Transaction deleted successfully.")
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
                message.postValue("Request failed with error: ${t.message}")
            }
        })
    }

}