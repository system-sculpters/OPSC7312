package com.opsc.opsc7312.model.api.controllers

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.opsc.opsc7312.model.api.retrofitclients.CategoryRetrofitClient
import com.opsc.opsc7312.model.data.model.Category
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CategoryController: ViewModel() {
    private var api = CategoryRetrofitClient.apiService

    val status: MutableLiveData<Boolean> = MutableLiveData()
    val message: MutableLiveData<String> = MutableLiveData()

    val categoryList: MutableLiveData<List<Category>> = MutableLiveData()

    fun getAllCategories(id: String){
        val call = api.getCategories(id)

        val url = call.request().url.toString()
        Log.d("MainActivity", "Request URL: $url")
        call.enqueue(object :
            Callback<List<Category>> {
            override fun onResponse(call: Call<List<Category>>, response: Response<List<Category>>) {
                if (response.isSuccessful) {
                    val categories = response.body()
                    categories?.let {
                        val cat0 = Category(isCreateButton = true)

                        // Create a new list with cat0 as the first element
                        val updatedCategories = listOf(cat0) + it

                        categoryList.postValue(updatedCategories)
                        status.postValue(true)
                        message.postValue("categories retrieved")
                        Log.d("MainActivity", "Categories: $it")
                    }
                } else {
                    Log.e("MainActivity", "Request failed with code: ${response.code()}")
                    status.postValue(true)
                    message.postValue("Request failed with code: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<Category>>, t: Throwable) {
                Log.e("MainActivity", "Error: ${t.message}")
                status.postValue(false)
                message.postValue("Request failed with code: ${t.message }")
            }
        })
    }

    fun createCategory(category: Category){
        api.createCategory(category).enqueue(object : Callback<Category> {
            override fun onResponse(call: Call<Category>, response: Response<Category>) {
                if (response.isSuccessful) {
                    val createdCategory = response.body()
                    createdCategory?.let {
                        status.postValue(true)
                        message.postValue("Request failed with code: ${it}")
                        Log.d("MainActivity", "Category created: $it")
                    }
                } else {
                    status.postValue(true)
                    message.postValue("Request failed with code: ${response.code()}")
                    Log.e("MainActivity", "Request failed with code: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Category>, t: Throwable) {
                Log.e("MainActivity", "Error: ${t.message}")
                status.postValue(false)
                message.postValue("Request failed with code: ${t.message }")
            }
        })
    }

    fun updateCategory(id: String, category: Category){
        api.updateCategory(id, category).enqueue(object : Callback<Category> {
            override fun onResponse(call: Call<Category>, response: Response<Category>) {
                if (response.isSuccessful) {
                    val createdCategory = response.body()
                    createdCategory?.let {
                        status.postValue(true)
                        message.postValue("Request failed with code: ${it}")
                        Log.d("MainActivity", "Category updated: $it")
                    }
                } else {
                    status.postValue(true)
                    message.postValue("Request failed with code: ${response.code()}")
                    Log.e("MainActivity", "Request failed with code: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Category>, t: Throwable) {
                Log.e("MainActivity", "Error: ${t.message}")
                status.postValue(false)
                message.postValue("Request failed with code: ${t.message }")
            }
        })
    }

    fun deleteCategory(id: String) {
        api.deleteCategory(id).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    // The category was successfully deleted
                    status.postValue(true)
                    message.postValue("Category deleted successfully.")
                    Log.d("MainActivity", "Category deleted successfully.")
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