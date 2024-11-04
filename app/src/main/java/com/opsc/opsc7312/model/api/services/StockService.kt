package com.opsc.opsc7312.model.api.services

import com.opsc.opsc7312.model.data.model.Stock
import com.opsc.opsc7312.model.data.model.StockHistory
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface StockService {


    // API endpoint to fetch a list of top stocks in a batch request
    // Requires an "Authorization" header with a token for authentication
    @GET("stocks/top/batch-stocks")
    fun getStocks(@Header("Authorization") token: String): Call<List<Stock>>

    // API endpoint to retrieve the historical data for a specific stock
    // Requires an "Authorization" header with a token for authentication
    // Also takes a "symbol" parameter as a query to specify the stock symbol
    @GET("stocks/history")
    fun getStockHistory(@Header("Authorization") token: String, @Query("symbol") symbol: String): Call<List<StockHistory>>

}