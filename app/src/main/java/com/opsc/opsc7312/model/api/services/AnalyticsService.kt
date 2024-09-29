package com.opsc.opsc7312.model.api.services

import com.opsc.opsc7312.model.data.model.AnalyticsResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

// This interface defines the API endpoints for analytics-related operations.
// It utilizes Retrofit to handle network requests and responses efficiently.
interface AnalyticsService {
    // This interface was adapted from medium
    // https://medium.com/quick-code/working-with-restful-apis-in-android-retrofit-volley-okhttp-eb8d3ec71e06
    // Megha Verma
    // https://medium.com/@meghaverma12

    // Retrieves all analytics data for a specified user .
    @GET("analytics/{id}")
    fun getAllAnalytics(@Header("Authorization") token: String, @Path("id") id: String): Call<AnalyticsResponse>

}