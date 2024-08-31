package com.opsc.opsc7312.model.api.services

import com.opsc.opsc7312.model.data.model.Transaction
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface TransactionService {
    @GET("transaction/{id}")
    fun getTransactions(@Header("Authorization") token: String,  @Path("id") userId: String): Call<List<Transaction>>

    @POST("transaction/create")
    fun createTransaction(@Header("Authorization") token: String, @Body transaction: Transaction): Call<Transaction>

    @PUT("transaction/{id}")
    fun updateTransaction(@Header("Authorization") token: String, @Path("id") id: String, @Body transaction: Transaction): Call<Transaction>

    @DELETE("transaction/{id}")
    fun deleteTransaction(@Header("Authorization") token: String, @Path("id") id: String): Call<Void>
}