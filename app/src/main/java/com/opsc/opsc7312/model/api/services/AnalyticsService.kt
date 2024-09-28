package com.opsc.opsc7312.model.api.services

import com.opsc.opsc7312.model.data.model.AnalyticsResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

// This interface defines the API endpoints for analytics-related operations.
// It utilizes Retrofit to handle network requests and responses efficiently.
interface AnalyticsService {
    // Retrieves all analytics data for a specified user .
    @GET("analytics/{id}")
    fun getAllAnalytics(@Header("Authorization") token: String, @Path("id") id: String): Call<AnalyticsResponse>

}