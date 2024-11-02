package com.opsc.opsc7312.model.api.services

import com.opsc.opsc7312.model.data.model.Investment
import com.opsc.opsc7312.model.data.model.InvestmentResponse
import com.opsc.opsc7312.model.data.model.Trade
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface InvestmentService {

    // Retrieves a list of categories associated with a specific user.
    // This function sends a GET request to the "category/{id}" endpoint.
    // It requires an authorization token in the header and the user ID in the path.
    // The response will be a Call object containing a list of Category objects.
    @GET("investment/{id}")
    fun getInvestments(@Header("Authorization") token: String, @Path("id") userId: String): Call<InvestmentResponse>

    // Creates a new transaction based on the provided transaction details.
    // This method sends a POST request to the "transaction/create" endpoint.
    // An authorization token is required in the header.
    // It takes a Transaction object as the request body and returns a Call object containing the created Transaction.
    @POST("investment/buy")
    fun buyInvestment(@Header("Authorization") token: String, @Body trade: Trade): Call<Void>


    // Creates a new transaction based on the provided transaction details.
    // This method sends a POST request to the "transaction/create" endpoint.
    // An authorization token is required in the header.
    // It takes a Transaction object as the request body and returns a Call object containing the created Transaction.
    @POST("investment/sell")
    fun sellInvestment(@Header("Authorization") token: String, @Body trade: Trade): Call<Void>


    // Retrieves a list of categories associated with a specific user.
    // This function sends a GET request to the "category/{id}" endpoint.
    // It requires an authorization token in the header and the user ID in the path.
    // The response will be a Call object containing a list of Category objects.
    @GET("investment/user/stock")
    fun getUserInvestment(@Header("Authorization") token: String, @Query("userid") userId: String, @Query("symbol") symbol: String): Call<Investment>

}