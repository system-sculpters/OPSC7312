package com.opsc.opsc7312.model.api.services

import com.opsc.opsc7312.model.data.model.User
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PUT
import retrofit2.http.Path

// Interface defining the API endpoints for managing user-related operations.
// It includes methods to retrieve user information and update user email, username, and password.
interface UserService {
    // This interface was adapted from medium
    // https://medium.com/quick-code/working-with-restful-apis-in-android-retrofit-volley-okhttp-eb8d3ec71e06
    // Megha Verma
    // https://medium.com/@meghaverma12


    // Retrieves the details of a user based on their unique user ID.
    // This method sends a GET request to the "user/{id}" endpoint.
    // It requires an authorization token in the header for access and the user ID in the path.
    // Returns a Call object containing the User object associated with the specified user ID.
    @GET("user/{id}")
    fun getUser(@Header("Authorization") token: String, @Path("id") userId: String): Call<User>

    // Updates the email and username of a specific user identified by their user ID.
    // This method sends a PUT request to the "user/{id}/update-email-and-username" endpoint.
    // It requires an authorization token in the header, the user ID in the path, and a User object in the request body
    // containing the new email and username.
    // Returns a Call object containing the updated User object.
    @PUT("user/{id}/update-email-and-username")
    fun updateEmailAndUsername(@Header("Authorization") token: String, @Path("id") uid: String, @Body user: User): Call<User>

    // Updates the password for a specific user identified by their user ID.
    // This method sends a PUT request to the "user/{id}/update-password" endpoint.
    // It requires an authorization token in the header, the user ID in the path, and a String containing the new password
    // in the request body.
    // Returns a Call object containing the User object after the password update.
    @PUT("user/{id}/update-password")
    fun updatePassword(@Header("Authorization") token: String, @Path("id") uid: String, @Body password: String): Call<User>
}
