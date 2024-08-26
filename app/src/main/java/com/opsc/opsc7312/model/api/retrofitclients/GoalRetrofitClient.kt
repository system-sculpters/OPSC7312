package com.opsc.opsc7312.model.api.retrofitclients

import com.opsc.opsc7312.AppConstants
import com.opsc.opsc7312.model.api.services.CategoryService
import com.opsc.opsc7312.model.api.services.GoalService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object GoalRetrofitClient {
    private const val URL = AppConstants.BASE_URL

    val apiService: GoalService by lazy {
        Retrofit.Builder()
            .baseUrl(URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GoalService::class.java)
    }
}