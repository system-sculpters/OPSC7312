package com.opsc.opsc7312.model.api.services

import com.opsc.opsc7312.model.data.model.Category
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface CategoryService {
    @GET("category/{id}")
    fun getCategories(@Path("id") userId: String): Call<List<Category>>

    @POST("category/create")
    fun createCategory(@Body category: Category): Call<Category>

    @PUT("category/{id}")
    fun updateCategory(@Path("id") id: String, @Body category: Category): Call<Category>

    @DELETE("category/{id}")
    fun deleteCategory(@Path("id") id: String): Call<Void>
}
