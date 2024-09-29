package com.opsc.opsc7312.model.api.retrofitclients

import com.opsc.opsc7312.AppConstants
import com.opsc.opsc7312.model.api.services.AnalyticsService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// Object that provides a singleton Retrofit client for making API requests related to analytics.
object AnalyticsRetrofitClient {
    // This object was adapted from medium
    // https://medium.com/quick-code/working-with-restful-apis-in-android-retrofit-volley-okhttp-eb8d3ec71e06
    // Megha Verma
    // https://medium.com/@meghaverma12
    // Base URL of the API, fetched from the application constants for consistency.
    private const val URL = AppConstants.BASE_URL

    // Lazily initialized Retrofit service interface for analytics-related API operations.
    // The 'by lazy' ensures that the Retrofit instance is created only when it is first accessed.
    val apiService: AnalyticsService by lazy {
        // Configures the Retrofit builder to connect to the specified base URL.
        Retrofit.Builder()
            // Sets the base URL for all API requests made using this Retrofit instance.
            .baseUrl(URL)
            // Specifies that Gson will be used to convert between JSON data and Kotlin objects.
            .addConverterFactory(GsonConverterFactory.create())
            // Builds the Retrofit instance and creates the implementation of the AnalyticsService interface,
            // which defines the available API endpoints and request methods.
            .build()
            .create(AnalyticsService::class.java)
    }
}
