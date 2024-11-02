package com.opsc.opsc7312.model.api.services

import com.opsc.opsc7312.model.data.model.Goal
import com.opsc.opsc7312.model.data.model.GoalsHolder
import com.opsc.opsc7312.model.data.model.SyncResponse
import com.opsc.opsc7312.model.data.model.TransactionsHolder
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

// Interface defining the API endpoints for managing user goals.
// It provides methods to retrieve, create, update, and delete goals associated with a user.
interface GoalService {
    // This interface was adapted from medium
    // https://medium.com/quick-code/working-with-restful-apis-in-android-retrofit-volley-okhttp-eb8d3ec71e06
    // Megha Verma
    // https://medium.com/@meghaverma12


    // Retrieves a list of goals for a specific user.
    // This function sends a GET request to the "goal/{id}" endpoint.
    // It requires an authorization token in the header and the user ID in the path.
    // The response will be a Call object containing a list of Goal objects associated with the specified user.
    @GET("goal/{id}")
    fun getGoals(@Header("Authorization") token: String, @Path("id") userId: String): Call<List<Goal>>


    // Creates a new goal based on the provided goal details.
    // This function sends a POST request to the "goal/create" endpoint.
    // An authorization token is required in the header.
    // It takes a Goal object as the request body and returns a Call object containing the created Goal.
    @POST("goal/create")
    fun createGoal(@Header("Authorization") token: String, @Body goal: Goal): Call<Goal>

    // Updates an existing goal identified by its ID.
    // This function sends a PUT request to the "goal/{id}" endpoint.
    // It requires an authorization token in the header, the goal ID in the path, and a Goal object in the request body.
    // Returns a Call object containing the updated Goal.
    @PUT("goal/{id}")
    fun updateGoal(@Header("Authorization") token: String, @Path("id") id: String, @Body goal: Goal): Call<Goal>

    // Deletes a goal identified by its ID.
    // This function sends a DELETE request to the "goal/{id}" endpoint.
    // It requires an authorization token in the header and the goal ID in the path.
    // Returns a Call object with no content on successful deletion.
    @DELETE("goal/{id}")
    fun deleteGoal(@Header("Authorization") token: String, @Path("id") id: String): Call<Void>


    // Retrieves a list of categories associated with a specific user.
    // This function sends a GET request to the "category/{id}" endpoint.
    // It requires an authorization token in the header and the user ID in the path.
    // The response will be a Call object containing a list of Category objects.
    @POST("goal/batch-create")
    fun syncGoals(@Header("Authorization") token: String, @Body goals: GoalsHolder): Call<SyncResponse>


}
