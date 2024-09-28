package com.opsc.opsc7312.model.api.retrofitclients

import com.opsc.opsc7312.AppConstants
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// A singleton object that serves as a generic Retrofit client for various API services.
object RetrofitClient {

    // Base URL for the API, sourced from application constants to ensure uniformity.
    private const val BASE_URL = AppConstants.BASE_URL

    // Lazily initializes the Retrofit instance with specified configurations for API communication.
    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // A generic function to create an instance of the specified service class.
    inline fun <reified T> createService(): T {
        return retrofit.create(T::class.java)
    }
}