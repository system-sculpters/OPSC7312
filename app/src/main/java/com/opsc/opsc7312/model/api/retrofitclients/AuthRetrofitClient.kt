package com.opsc.opsc7312.model.api.retrofitclients

import com.opsc.opsc7312.AppConstants
import com.opsc.opsc7312.model.api.services.AuthService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object AuthRetrofitClient {
    // This object was adapted from medium
    // https://medium.com/quick-code/working-with-restful-apis-in-android-retrofit-volley-okhttp-eb8d3ec71e06
    // Megha Verma
    // https://medium.com/@meghaverma12
    // Base URL of the API, fetched from the application constants for consistency.
    private const val URL = AppConstants.BASE_URL

    val apiService: AuthService by lazy {
        Retrofit.Builder()
            .baseUrl(URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AuthService::class.java)
    }
}