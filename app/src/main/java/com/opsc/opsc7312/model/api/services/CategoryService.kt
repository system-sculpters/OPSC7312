package com.opsc.opsc7312.model.api.services

import com.opsc.opsc7312.model.data.model.Category
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface CategoryService {
    @GET("category/{id}")
    fun getCategories(@Header("Authorization") token: String, @Path("id") userId: String): Call<List<Category>>

    @POST("category/create")
    fun createCategory(@Header("Authorization") token: String, @Body category: Category): Call<Category>

    @PUT("category/{id}")
    fun updateCategory(@Header("Authorization") token: String, @Path("id") id: String, @Body category: Category): Call<Category>

    @DELETE("category/{id}")
    fun deleteCategory(@Header("Authorization") token: String, @Path("id") id: String): Call<Void>
}
