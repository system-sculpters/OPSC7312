package com.opsc.opsc7312.model.api.services

import com.opsc.opsc7312.model.data.model.User
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PUT
import retrofit2.http.Path

interface UserService {
    @GET("user/{id}")
    fun getUser(@Header("Authorization") token: String,  @Path("id") userId: String): Call<User>

    @PUT("user/{id}/update-email-and-username")
    fun updateEmailAndUsername(@Header("Authorization") token: String, @Path("id") uid: String, @Body user: User): Call<User>

    @PUT("user/{id}/update-password")
    fun updatePassword(@Header("Authorization") token: String, @Path("id") uid: String, @Body password: String): Call<User>
}