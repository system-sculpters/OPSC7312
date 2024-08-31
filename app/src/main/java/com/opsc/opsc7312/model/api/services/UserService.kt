package com.opsc.opsc7312.model.api.services

import com.opsc.opsc7312.model.data.model.User
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface UserService {
    @GET("user/{id}")
    fun getUser(@Path("id") userId: String): Call<User>

    @PUT("user/{uid}/update-email-and-username")
    fun updateEmailAndUsername(@Path("uid") uid: String, @Body user: User): Call<User>

    @PUT("user/{uid}/update-password")
    fun updatePassword(@Path("uid") uid: String, @Body password: String): Call<User>
}