package com.opsc.opsc7312.model.api.retrofitclients

import com.opsc.opsc7312.AppConstants
import com.opsc.opsc7312.model.api.services.CategoryService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object CategoryRetrofitClient {
    private const val URL = AppConstants.BASE_URL

    val apiService: CategoryService by lazy {
        Retrofit.Builder()
            .baseUrl(URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CategoryService::class.java)
    }
}