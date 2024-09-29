package com.opsc.opsc7312.model.api.services

import com.opsc.opsc7312.model.data.model.User
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

// Interface defining authentication-related API endpoints for user management.
// It outlines functions for user registration, login, logout, and reauthentication.
interface AuthService {
    // This interface was adapted from medium
    // https://medium.com/quick-code/working-with-restful-apis-in-android-retrofit-volley-okhttp-eb8d3ec71e06
    // Megha Verma
    // https://medium.com/@meghaverma12


    // Registers a new user with the provided user details.
    // It sends a POST request to the "auth/signup" endpoint.
    // The function returns a Call object containing the User data of the newly registered user.
    @POST("auth/signup")
    fun register(@Body user: User): Call<User>

    // Authenticates a user using their login credentials.
    // This function sends a POST request to the "auth/signin" endpoint.
    // Upon success, it returns a Call object containing the User data for the logged-in user.
    @POST("auth/signin")
    fun login(@Body user: User): Call<User>

    // Registers a new user via Single Sign-On (SSO) with the provided user details.
    // It sends a POST request to the "auth/signup-sso" endpoint.
    // Returns a Call object containing the User data of the newly registered user.
    @POST("auth/signup-sso")
    fun registerWithSSO(@Body user: User): Call<User>

    // Authenticates a user via Single Sign-On (SSO) using their credentials.
    // This function sends a POST request to the "auth/signin-sso" endpoint.
    // Returns a Call object containing the User data for the logged-in user.
    @POST("auth/signin-sso")
    fun loginWithSSO(@Body user: User): Call<User>

    // Logs out the currently authenticated user.
    // It requires an authorization token and sends a POST request to the "auth/logout" endpoint.
    // This function returns a Call object with no content on successful logout.
    @POST("auth/logout")
    fun logout(@Header("Authorization") token: String): Call<Void>

}
