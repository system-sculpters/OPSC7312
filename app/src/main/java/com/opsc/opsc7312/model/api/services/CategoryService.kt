package com.opsc.opsc7312.model.api.services

import com.opsc.opsc7312.model.data.model.CategoriesHolder
import com.opsc.opsc7312.model.data.model.Category
import com.opsc.opsc7312.model.data.model.SyncResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

// Interface defining the API endpoints related to category management.
// It provides functions to retrieve, create, update, and delete categories.
interface CategoryService {
    // This interface was adapted from medium
    // https://medium.com/quick-code/working-with-restful-apis-in-android-retrofit-volley-okhttp-eb8d3ec71e06
    // Megha Verma
    // https://medium.com/@meghaverma12


    // Retrieves a list of categories associated with a specific user.
    // This function sends a GET request to the "category/{id}" endpoint.
    // It requires an authorization token in the header and the user ID in the path.
    // The response will be a Call object containing a list of Category objects.
    @GET("category/{id}")
    fun getCategories(@Header("Authorization") token: String, @Path("id") userId: String): Call<List<Category>>

    // Retrieves a list of categories associated with a specific user.
    // This function sends a GET request to the "category/{id}" endpoint.
    // It requires an authorization token in the header and the user ID in the path.
    // The response will be a Call object containing a list of Category objects.
    @POST("category/batch-create")
    fun syncCategories(@Header("Authorization") token: String, @Body categories: CategoriesHolder): Call<SyncResponse>


    // Creates a new category based on the provided category details.
    // This function sends a POST request to the "category/create" endpoint.
    // An authorization token is required in the header.
    // It takes a Category object as the request body and returns a Call object containing the created Category.
    @POST("category/create")
    fun createCategory(@Header("Authorization") token: String, @Body category: Category): Call<Category>

    // Updates an existing category identified by its ID.
    // This function sends a PUT request to the "category/{id}" endpoint.
    // It requires an authorization token in the header, the category ID in the path, and a Category object in the request body.
    // Returns a Call object containing the updated Category.
    @PUT("category/{id}")
    fun updateCategory(@Header("Authorization") token: String, @Path("id") id: String, @Body category: Category): Call<Category>

    // Deletes a category identified by its ID.
    // This function sends a DELETE request to the "category/{id}" endpoint.
    // It requires an authorization token in the header and the category ID in the path.
    // Returns a Call object with no content on successful deletion.
    @DELETE("category/{id}")
    fun deleteCategory(@Header("Authorization") token: String, @Path("id") id: String): Call<Void>
}
