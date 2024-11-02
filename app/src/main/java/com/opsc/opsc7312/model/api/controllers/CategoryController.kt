package com.opsc.opsc7312.model.api.controllers

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.opsc.opsc7312.model.api.retrofitclients.RetrofitClient
import com.opsc.opsc7312.model.api.services.CategoryService
import com.opsc.opsc7312.model.data.model.CategoriesHolder
import com.opsc.opsc7312.model.data.model.Category
import com.opsc.opsc7312.model.data.model.IdMapping
import com.opsc.opsc7312.model.data.model.SyncResponse
import kotlinx.coroutines.suspendCancellableCoroutine
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.resume

// CategoryController is a ViewModel responsible for managing category-related API operations.
// It interacts with the Retrofit client to fetch, create, update, and delete categories from a backend server.
// It uses MutableLiveData to observe the status of API requests and hold category-related data.
class CategoryController: ViewModel() {

    // Retrofit API service instance for category-related network requests
    private var api: CategoryService = RetrofitClient.createService<CategoryService>()


    // MutableLiveData to track the success or failure status of API requests
    val status: MutableLiveData<Boolean> = MutableLiveData()

    // MutableLiveData to store the response messages or errors from API calls
    val message: MutableLiveData<String> = MutableLiveData()

    // MutableLiveData holding a list of categories fetched from the backend
    val categoryList: MutableLiveData<List<Category>> = MutableLiveData()

    // Fetches all categories associated with a specific user, identified by `id`.
    // Requires an authentication token and the user's ID.
    // Updates the `categoryList`, `status`, and `message` based on the response.

    // This method was adapted from medium
    // https://medium.com/quick-code/working-with-restful-apis-in-android-retrofit-volley-okhttp-eb8d3ec71e06
    // Megha Verma
    // https://medium.com/@meghaverma12
    fun getAllCategories(userToken: String, id: String) {
        val token = "Bearer $userToken"
        val call = api.getCategories(token, id)

        // Logging the request URL for debugging purposes
        val url = call.request().url.toString()
        //Log.d("MainActivity", "Request URL: $url")

        // Asynchronously executes the API call to retrieve categories
        call.enqueue(object : Callback<List<Category>> {
            // Called when the server responds to the request
            override fun onResponse(call: Call<List<Category>>, response: Response<List<Category>>) {
                if (response.isSuccessful) {
                    // If the response is successful, update the category list and status
                    val categories = response.body()
                    categories?.let {
                        categoryList.postValue(it)
                        status.postValue(true)
                        message.postValue("Categories retrieved")
                        //Log.d("MainActivity", "Categories: $it")
                    }
                } else {
                    // Handle unsuccessful responses, e.g., a 4xx or 5xx status code
                    categoryList.postValue(listOf())
                    //Log.e("MainActivity", "Request failed with code: ${response.code()}")
                    status.postValue(false)
                    message.postValue("Request failed with code: ${response.code()}")
                }
            }

            // Called when the API call fails, e.g., due to network issues
            override fun onFailure(call: Call<List<Category>>, t: Throwable) {
                categoryList.postValue(listOf())
                //Log.e("MainActivity", "Error: ${t.message}")
                status.postValue(false)
                message.postValue(t.message)
            }
        })
    }

    // Sends a request to create a new category for the user.
    // Takes a user token for authentication and a Category object.
    // Updates the `status` and `message` based on the success of the request.

    // This method was adapted from medium
    // https://medium.com/quick-code/working-with-restful-apis-in-android-retrofit-volley-okhttp-eb8d3ec71e06
    // Megha Verma
    // https://medium.com/@meghaverma12
    fun createCategory(userToken: String, category: Category) {
        val token = "Bearer $userToken"
        api.createCategory(token, category).enqueue(object : Callback<Category> {
            override fun onResponse(call: Call<Category>, response: Response<Category>) {
                if (response.isSuccessful) {
                    // On successful category creation, update the status and message
                    val createdCategory = response.body()
                    createdCategory?.let {
                        status.postValue(true)
                        message.postValue("Category created: $it")
                        Log.d("MainActivity", "Category created: $it")
                    }
                } else {
                    // Handle the failure of the category creation
                    status.postValue(false)
                    message.postValue("Request failed with code: ${response.code()}")
                    Log.e("MainActivity", "Request failed with code: ${response.code()}")
                }
            }

            // Handles network or other request failures
            override fun onFailure(call: Call<Category>, t: Throwable) {
                Log.e("MainActivity", "Error: ${t.message}")
                status.postValue(false)
                message.postValue(t.message)
            }
        })
    }

    // Sends a request to update an existing category identified by `id`.
    // Takes a user token, category ID, and the updated Category object.
    // Updates the `status` and `message` based on the success of the request.

    // This method was adapted from medium
    // https://medium.com/quick-code/working-with-restful-apis-in-android-retrofit-volley-okhttp-eb8d3ec71e06
    // Megha Verma
    // https://medium.com/@meghaverma12
    fun updateCategory(userToken: String, id: String, category: Category) {
        val token = "Bearer $userToken"
        api.updateCategory(token, id, category).enqueue(object : Callback<Category> {
            override fun onResponse(call: Call<Category>, response: Response<Category>) {
                if (response.isSuccessful) {
                    // On successful category update, update the status and message
                    val updatedCategory = response.body()
                    updatedCategory?.let {
                        status.postValue(true)
                        message.postValue("Category updated: $it")
                        Log.d("MainActivity", "Category updated: $it")
                    }
                } else {
                    // Handle the failure of the category update request
                    status.postValue(false)
                    message.postValue("Request failed with code: ${response.code()}")
                    Log.e("MainActivity", "Request failed with code: ${response.code()}")
                }
            }

            // Handles network or other request failures
            override fun onFailure(call: Call<Category>, t: Throwable) {
                Log.e("MainActivity", "Error: ${t.message}")
                status.postValue(false)
                message.postValue(t.message)
            }
        })
    }

    // Sends a request to delete a category identified by `id`.
    // Takes a user token and category ID.
    // Updates the `status` and `message` based on the success of the request.

    // This method was adapted from medium
    // https://medium.com/quick-code/working-with-restful-apis-in-android-retrofit-volley-okhttp-eb8d3ec71e06
    // Megha Verma
    // https://medium.com/@meghaverma12
    fun deleteCategory(userToken: String, id: String) {
        val token = "Bearer $userToken"
        api.deleteCategory(token, id).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    // On successful category deletion, update the status and message
                    status.postValue(true)
                    message.postValue("Category deleted successfully.")
                    Log.d("MainActivity", "Category deleted successfully.")
                } else {
                    // Handle the failure of the category deletion request
                    status.postValue(false)
                    message.postValue("Request failed with code: ${response.code()}")
                    Log.e("MainActivity", "Request failed with code: ${response.code()}")
                }
            }

            // Handles network or other request failures
            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("MainActivity", "Error: ${t.message}")
                status.postValue(false)
                message.postValue(t.message)
            }
        })
    }

    // Sends a request to create a new category for the user.
    // Takes a user token for authentication and a Category object.
    // Updates the `status` and `message` based on the success of the request.

    // This method was adapted from medium
    // https://medium.com/quick-code/working-with-restful-apis-in-android-retrofit-volley-okhttp-eb8d3ec71e06
    // Megha Verma
    // https://medium.com/@meghaverma12

    suspend fun syncCategoriesSuspend(userToken: String, unSyncedCategories: List<Category>): Pair<Boolean, List<IdMapping>?> {
        val token = "Bearer $userToken"  // Ensure proper token formatting
        val catHolder = CategoriesHolder(categories = unSyncedCategories)
        val call = api.syncCategories(token, catHolder)  // Make the API call to fetch goals

        return suspendCancellableCoroutine { continuation ->
            call.enqueue(object : Callback<SyncResponse> {
                override fun onResponse(call: Call<SyncResponse>, response: Response<SyncResponse>) {
                    if (response.isSuccessful) {
                        // Resume coroutine with success and the ID mappings
                        continuation.resume(Pair(true, response.body()?.ids))
                        message.postValue("Categories synced successfully.")
                    } else {
                        // Log and parse error, then resume with failure and null ID mappings
                        val errorMessage = if (response.errorBody() != null) {
                            try {
                                val errorResponse = Gson().fromJson(response.errorBody()?.string(), SyncResponse::class.java)
                                "Error syncing goals: ${errorResponse.message}"
                            } catch (e: Exception) {
                                "Request failed with code: ${response.code()}, but failed to parse error response."
                            }
                        } else {
                            "Request failed with code: ${response.code()}, message: ${response.message()}"
                        }

                        Log.e("CategorySync", errorMessage)
                        message.postValue(errorMessage)
                        continuation.resume(Pair(false, null))
                    }
                }

                override fun onFailure(call: Call<SyncResponse>, t: Throwable) {
                    // Log error and resume coroutine with failure
                    val errorMessage = "Sync failed: ${t.message ?: "Unknown error"}\nCall: $call"
                    Log.e("CategorySync", errorMessage)
                    message.postValue(t.message)
                    continuation.resume(Pair(false, null))
                }
            })

            // Cancel call if the coroutine is cancelled
            continuation.invokeOnCancellation {
                call.cancel()
            }
        }
    }


    // Sends a request to create a new category for the user.
    // Takes a user token for authentication and a Category object.
    // Updates the `status` and `message` based on the success of the request.

    // This method was adapted from medium
    // https://medium.com/quick-code/working-with-restful-apis-in-android-retrofit-volley-okhttp-eb8d3ec71e06
    // Megha Verma
    // https://medium.com/@meghaverma12
    suspend fun getRemoteCategoriesSuspend(userToken: String, userId: String): List<Category>? {
        val token = "Bearer $userToken"  // Format the token for authorization
        val call = api.getCategories(token, userId)  // Make the API call to fetch goals

        return suspendCancellableCoroutine { continuation ->
            // Log the request URL for debugging
            val url = call.request().url.toString()
            Log.d("MainActivity", "Request URL: $url")

            // Execute the API call asynchronously
            call.enqueue(object : Callback<List<Category>> {
                override fun onResponse(call: Call<List<Category>>, response: Response<List<Category>>) {
                    if (response.isSuccessful) {
                        // Resume coroutine with the response body
                        continuation.resume(response.body())
                        message.postValue("Categories retrieved")
                    } else {
                        // Parse and log error, then resume with null
                        val errorMessage = if (response.errorBody() != null) {
                            try {
                                val errorResponse = Gson().fromJson(response.errorBody()?.string(), SyncResponse::class.java)
                                "Error syncing goals: ${errorResponse.message}"
                            } catch (e: Exception) {
                                "Request failed with code: ${response.code()}, but failed to parse error response."
                            }
                        } else {
                            "Request failed with code: ${response.code()}, message: ${response.message()}"
                        }

                        Log.e("GoalSync", errorMessage)
                        message.postValue(errorMessage)
                        continuation.resume(null)
                    }
                }

                override fun onFailure(call: Call<List<Category>>, t: Throwable) {
                    // Log the error and resume coroutine with null
                    val errorMessage = "Sync failed: ${t.message ?: "Unknown error"}\nCall: $call"
                    Log.e("GoalSync", errorMessage)
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
