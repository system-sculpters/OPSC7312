package com.opsc.opsc7312.model.api.controllers

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.opsc.opsc7312.model.api.retrofitclients.GoalRetrofitClient
import com.opsc.opsc7312.model.api.retrofitclients.RetrofitClient
import com.opsc.opsc7312.model.api.services.AnalyticsService
import com.opsc.opsc7312.model.api.services.GoalService
import com.opsc.opsc7312.model.data.model.Goal
import com.opsc.opsc7312.model.data.model.GoalsHolder
import com.opsc.opsc7312.model.data.model.IdMapping
import com.opsc.opsc7312.model.data.model.SyncResponse
import com.opsc.opsc7312.model.data.model.Transaction
import com.opsc.opsc7312.model.data.model.TransactionsHolder
import kotlinx.coroutines.suspendCancellableCoroutine
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.resume

//ViewModel class to manage goal-related operations such as retrieving, creating,
//updating, and deleting goals by making API calls using Retrofit.
class GoalController : ViewModel() {
    // Instance of the API service to interact with backend endpoints
    private var api: GoalService = RetrofitClient.createService<GoalService>()

    // LiveData to track the status of operations (e.g., success or failure)
    val status: MutableLiveData<Boolean> = MutableLiveData()

    // LiveData to store messages (e.g., success or error messages)
    val message: MutableLiveData<String> = MutableLiveData()

    // LiveData that holds the list of goals fetched from the server
    val goalList: MutableLiveData<List<Goal>> = MutableLiveData()

    // Retrieves all goals for the specified user

    // This method was adapted from medium
    // https://medium.com/quick-code/working-with-restful-apis-in-android-retrofit-volley-okhttp-eb8d3ec71e06
    // Megha Verma
    // https://medium.com/@meghaverma12
    fun getAllGoals(userToken: String, id: String) {
        val token = "Bearer $userToken"  // Format the token for authorization
        val call = api.getGoals(token, id)  // Make the API call to fetch goals

        // Log the request URL for debugging
        val url = call.request().url.toString()
        Log.d("MainActivity", "Request URL: $url")

        // Execute the API call asynchronously
        call.enqueue(object : Callback<List<Goal>> {
            override fun onResponse(call: Call<List<Goal>>, response: Response<List<Goal>>) {
                // If the response is successful, update the goal list
                if (response.isSuccessful) {
                    val goals = response.body()
                    goals?.let {
                        goalList.postValue(it)  // Post the list of goals to the LiveData
                        status.postValue(true)  // Mark the operation as successful
                        message.postValue("Goals retrieved")  // Set a success message
                        Log.d("MainActivity", "Goals: $it")
                    }
                } else {
                    // Handle non-successful responses, like error codes
                    Log.e("MainActivity", "Request failed with code: ${response.code()}")
                    goalList.postValue(listOf())  // Empty the goal list on failure
                    status.postValue(false)  // Mark the operation as failed
                    message.postValue("Request failed with code: ${response.code()}")  // Set an error message
                }
            }

            override fun onFailure(call: Call<List<Goal>>, t: Throwable) {
                // Log the error if the request fails
                Log.e("MainActivity", "Error: ${t.message}")
                goalList.postValue(listOf())  // Empty the goal list on failure
                status.postValue(false)  // Mark the operation as failed
                message.postValue(t.message)  // Set an error message
            }
        })
    }

    // Sends a request to create a new goal for the user

    // This method was adapted from medium
    // https://medium.com/quick-code/working-with-restful-apis-in-android-retrofit-volley-okhttp-eb8d3ec71e06
    // Megha Verma
    // https://medium.com/@meghaverma12
    fun createGoal(userToken: String, goal: Goal) {
        val token = "Bearer $userToken"  // Format the token for authorization

        // Make the API call to create a new goal
        api.createGoal(token, goal).enqueue(object : Callback<Goal> {
            override fun onResponse(call: Call<Goal>, response: Response<Goal>) {
                // If the response is successful, log and update the status
                if (response.isSuccessful) {
                    val createdGoal = response.body()
                    createdGoal?.let {
                        status.postValue(true)  // Mark the operation as successful
                        message.postValue("Goal created: $it")  // Set a success message
                        Log.d("MainActivity", "Goal created: $it")
                    }
                } else {
                    // Handle error scenarios
                    status.postValue(false)  // Mark the operation as failed
                    message.postValue("Request failed with code: ${response.code()}")  // Set an error message
                    Log.e("MainActivity", "Request failed with code: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Goal>, t: Throwable) {
                // Log the error if the request fails
                Log.e("MainActivity", "Error: ${t.message}")
                status.postValue(false)  // Mark the operation as failed
                message.postValue(t.message)  // Set an error message
            }
        })
    }

    // Updates an existing goal with new data

    // This method was adapted from medium
    // https://medium.com/quick-code/working-with-restful-apis-in-android-retrofit-volley-okhttp-eb8d3ec71e06
    // Megha Verma
    // https://medium.com/@meghaverma12
    fun updateGoal(userToken: String, id: String, goal: Goal) {
        val token = "Bearer $userToken"  // Format the token for authorization

        // Make the API call to update an existing goal
        api.updateGoal(token, id, goal).enqueue(object : Callback<Goal> {
            override fun onResponse(call: Call<Goal>, response: Response<Goal>) {
                // If the response is successful, log and update the status
                if (response.isSuccessful) {
                    val updatedGoal = response.body()
                    updatedGoal?.let {
                        status.postValue(true)  // Mark the operation as successful
                        message.postValue("Goal updated: $it")  // Set a success message
                        Log.d("MainActivity", "Goal updated: $it")
                    }
                } else {
                    // Handle error scenarios
                    status.postValue(false)  // Mark the operation as failed
                    message.postValue("Request failed with code: ${response.code()}")  // Set an error message
                    Log.e("MainActivity", "Request failed with code: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Goal>, t: Throwable) {
                // Log the error if the request fails
                Log.e("MainActivity", "Error: ${t.message}")
                status.postValue(false)  // Mark the operation as failed
                message.postValue(t.message)  // Set an error message
            }
        })
    }

    // Deletes an existing goal by its ID

    // This method was adapted from medium
    // https://medium.com/quick-code/working-with-restful-apis-in-android-retrofit-volley-okhttp-eb8d3ec71e06
    // Megha Verma
    // https://medium.com/@meghaverma12
    fun deleteGoal(userToken: String, id: String) {
        val token = "Bearer $userToken"  // Format the token for authorization

        // Make the API call to delete the goal
        api.deleteGoal(token, id).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                // If the response is successful, log and update the status
                if (response.isSuccessful) {
                    status.postValue(true)  // Mark the operation as successful
                    message.postValue("Goal deleted successfully.")  // Set a success message
                    Log.d("MainActivity", "Goal deleted successfully.")
                } else {
                    // Handle error scenarios
                    status.postValue(false)  // Mark the operation as failed
                    message.postValue("Request failed with code: ${response.code()}")  // Set an error message
                    Log.e("MainActivity", "Request failed with code: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                // Log the error if the request fails
                Log.e("MainActivity", "Error: ${t.message}")
                status.postValue(false)  // Mark the operation as failed
                message.postValue(t.message)  // Set an error message
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
    suspend fun syncGoalsSuspend(userToken: String, unSyncedGoals: List<Goal>): Pair<Boolean, List<IdMapping>?> {
        val token = "Bearer $userToken"  // Ensure proper token formatting
        val transactionHolder = GoalsHolder(goals = unSyncedGoals)
        val call = api.syncGoals(token, transactionHolder)  // Make the API call to sync goals

        return suspendCancellableCoroutine { continuation ->
            call.enqueue(object : Callback<SyncResponse> {
                override fun onResponse(call: Call<SyncResponse>, response: Response<SyncResponse>) {
                    if (response.isSuccessful) {
                        // Resume coroutine with success and the ID mappings
                        continuation.resume(Pair(true, response.body()?.ids))
                        message.postValue("Goals synced successfully.")
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

                        Log.e("GoalSync", errorMessage)
                        message.postValue(errorMessage)
                        continuation.resume(Pair(false, null))
                    }
                }

                override fun onFailure(call: Call<SyncResponse>, t: Throwable) {
                    // Log error and resume coroutine with failure
                    val errorMessage = "Sync failed: ${t.message ?: "Unknown error"}\nCall: $call"
                    Log.e("GoalSync", errorMessage)
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
    suspend fun getRemoteGoalsSuspend(userToken: String, userId: String): List<Goal>? {
        val token = "Bearer $userToken"  // Format the token for authorization
        val call = api.getGoals(token, userId)  // Make the API call to fetch goals

        // Log the request URL for debugging
        val url = call.request().url.toString()
        Log.d("MainActivity", "Request URL: $url")

        return suspendCancellableCoroutine { continuation ->
            call.enqueue(object : Callback<List<Goal>> {
                override fun onResponse(call: Call<List<Goal>>, response: Response<List<Goal>>) {
                    // If the response is successful, return the goal list
                    if (response.isSuccessful) {
                        message.postValue("Goals retrieved")  // Set a success message
                        continuation.resume(response.body()) // Resume with the list of goals
                    } else {
                        continuation.resume(null) // Resume with null on error

                        val errorMessage = if (response.errorBody() != null) {
                            try {
                                // Parse the error body to get the SyncResponse object
                                val errorResponse = Gson().fromJson(response.errorBody()?.string(), SyncResponse::class.java)
                                "Error retrieving goals: ${errorResponse.message}"
                            } catch (e: Exception) {
                                "Request failed with code: ${response.code()}, but failed to parse error response."
                            }
                        } else {
                            "Request failed with code: ${response.code()}, message: ${response.message()}"
                        }

                        message.postValue(errorMessage)
                        Log.e("GoalSync", errorMessage)
                    }
                }

                override fun onFailure(call: Call<List<Goal>>, t: Throwable) {
                    // Log the error if the request fails
                    val errorMessage = "Sync failed: ${t.message ?: "Unknown error"}\nCall: $call"
                    Log.e("GoalSync", errorMessage)
                    continuation.resume(null) // Resume with null on error
                    message.postValue(t.message)
                }
            })

            // Cancel call if the coroutine is cancelled
            continuation.invokeOnCancellation {
                call.cancel()
            }
        }
    }

}
