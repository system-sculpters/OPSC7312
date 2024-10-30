package com.opsc.opsc7312.model.api.services

import com.opsc.opsc7312.model.data.model.CategoriesHolder
import com.opsc.opsc7312.model.data.model.SyncResponse
import com.opsc.opsc7312.model.data.model.Transaction
import com.opsc.opsc7312.model.data.model.TransactionsHolder
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

// Interface defining the API endpoints for managing transactions.
// It provides methods to retrieve, create, update, and delete transactions associated with a user.
interface TransactionService {
    // This interface was adapted from medium
    // https://medium.com/quick-code/working-with-restful-apis-in-android-retrofit-volley-okhttp-eb8d3ec71e06
    // Megha Verma
    // https://medium.com/@meghaverma12


    // Retrieves a list of transactions for a specific user.
    // This method sends a GET request to the "transaction/{id}" endpoint.
    // It requires an authorization token in the header and the user ID in the path.
    // The response will be a Call object containing a list of Transaction objects associated with the specified user.
    @GET("transaction/{id}")
    fun getTransactions(@Header("Authorization") token: String, @Path("id") userId: String): Call<List<Transaction>>

    // Creates a new transaction based on the provided transaction details.
    // This method sends a POST request to the "transaction/create" endpoint.
    // An authorization token is required in the header.
    // It takes a Transaction object as the request body and returns a Call object containing the created Transaction.
    @POST("transaction/create")
    fun createTransaction(@Header("Authorization") token: String, @Body transaction: Transaction): Call<Transaction>

    // Updates an existing transaction identified by its ID.
    // This method sends a PUT request to the "transaction/{id}" endpoint.
    // It requires an authorization token in the header, the transaction ID in the path, and a Transaction object in the request body.
    // Returns a Call object containing the updated Transaction.
    @PUT("transaction/{id}")
    fun updateTransaction(@Header("Authorization") token: String, @Path("id") id: String, @Body transaction: Transaction): Call<Transaction>

    // Deletes a transaction identified by its ID.
    // This method sends a DELETE request to the "transaction/{id}" endpoint.
    // It requires an authorization token in the header and the transaction ID in the path.
    // Returns a Call object with no content on successful deletion.
    @DELETE("transaction/{id}")
    fun deleteTransaction(@Header("Authorization") token: String, @Path("id") id: String): Call<Void>


    // Retrieves a list of categories associated with a specific user.
    // This function sends a GET request to the "category/{id}" endpoint.
    // It requires an authorization token in the header and the user ID in the path.
    // The response will be a Call object containing a list of Category objects.
    @POST("transaction/batch-create")
    fun syncTransactions(@Header("Authorization") token: String, @Body transactions: TransactionsHolder): Call<SyncResponse>
}
