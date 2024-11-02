package com.opsc.opsc7312.model.api.services

import com.opsc.opsc7312.model.data.model.Stock
import com.opsc.opsc7312.model.data.model.StockHistory
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface StockService {

    // Retrieves a list of goals for a specific user.
    // This function sends a GET request to the "goal/{id}" endpoint.
    // It requires an authorization token in the header and the user ID in the path.
    // The response will be a Call object containing a list of Goal objects associated with the specified user.
    @GET("stocks/top/batch-stocks")
    fun getStocks(@Header("Authorization") token: String): Call<List<Stock>>


    // Retrieves a list of goals for a specific user.
    // This function sends a GET request to the "goal/{id}" endpoint.
    // It requires an authorization token in the header and the user ID in the path.
    // The response will be a Call object containing a list of Goal objects associated with the specified user.
    @GET("stocks/history")
    fun getStockHistory(@Header("Authorization") token: String,@Query("symbol") symbol: String): Call<List<StockHistory>>
}