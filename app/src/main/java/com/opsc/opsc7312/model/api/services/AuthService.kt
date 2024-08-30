package com.opsc.opsc7312.model.api.services

import com.opsc.opsc7312.model.data.User
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface AuthService {
    @POST("auth/signup")
    fun register(@Body user: User): Call<User>

    @POST("auth/signin")
    fun login(@Body user: User): Call<User>

    @POST("auth/logout")
    fun logout(@Header("Authorization") token: String): Call<Void>
}