package com.opsc.opsc7312.model.api.retrofitclients

import com.opsc.opsc7312.AppConstants
import com.opsc.opsc7312.model.api.services.AnalyticsService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object AnalyticsRetrofitClient {
    private const val URL = AppConstants.BASE_URL

    val apiService: AnalyticsService by lazy {
        Retrofit.Builder()
            .baseUrl(URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AnalyticsService::class.java)
    }
}