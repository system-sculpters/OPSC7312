package com.opsc.opsc7312.model.api.services

import com.opsc.opsc7312.model.data.model.AnalyticsResponse
import com.opsc.opsc7312.model.data.model.Notification
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface NotificationsService {
    // This interface was adapted from medium
    // https://medium.com/quick-code/working-with-restful-apis-in-android-retrofit-volley-okhttp-eb8d3ec71e06
    // Megha Verma
    // https://medium.com/@meghaverma12

    // Retrieves all analytics data for a specified user .
    @GET("notifications/{id}")
    fun getAllNotifications(@Header("Authorization") token: String, @Path("id") id: String): Call<List<Notification>>

}