package com.opsc.opsc7312.model.api.services

import com.opsc.opsc7312.model.data.model.AnalyticsResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface AnalyticsService {
    @GET("analytics/{id}")
    fun getAllAnalytics(@Header("Authorization") token: String, @Path("id") id: String): Call<AnalyticsResponse>

}