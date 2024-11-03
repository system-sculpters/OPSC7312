package com.opsc.opsc7312

import com.opsc.opsc7312.model.api.services.StockService
import com.opsc.opsc7312.model.data.model.Stock
import com.opsc.opsc7312.model.data.model.StockHistory
import okhttp3.ResponseBody
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import retrofit2.Call
import retrofit2.Response

class StockControllerTest {
    // This class was adapted from YouTube
    // https://youtu.be/ssF_YPvLRR8?si=OCxgobZF6XAIF5Z-
    // CodingWithPuneet
    // https://www.youtube.com/@codingwithpuneet
    @Mock
    private lateinit var mockApiService: StockService

    @Mock
    private lateinit var mockStockCall: Call<List<Stock>>

    @Mock
    private lateinit var mockStockHistoryCall: Call<List<StockHistory>>

    private val dummyToken = "Bearer your_token_here"
    private val dummySymbol = "AAPL"

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
    }

    @Test
    fun `getStocks should make a request with Authorization header and return stock list`() {
        // This class was adapted from YouTube
        // https://youtu.be/ssF_YPvLRR8?si=OCxgobZF6XAIF5Z-
        // CodingWithPuneet
        // https://www.youtube.com/@codingwithpuneet
        // Arrange
        Mockito.`when`(mockApiService.getStocks(dummyToken)).thenReturn(mockStockCall)
        Mockito.`when`(mockStockCall.execute()).thenReturn(Response.success(listOf(Stock(/*dummy data*/))))

        // Act
        val response = mockApiService.getStocks(dummyToken).execute()

        // Assert
        assert(response.isSuccessful)
        Mockito.verify(mockApiService).getStocks(dummyToken)
        assert(response.body()?.isNotEmpty() == true)
    }

    @Test
    fun `getStocks should fail when unauthorized`() {
        // This class was adapted from YouTube
        // https://youtu.be/ssF_YPvLRR8?si=OCxgobZF6XAIF5Z-
        // CodingWithPuneet
        // https://www.youtube.com/@codingwithpuneet
        // Arrange
        Mockito.`when`(mockApiService.getStocks(dummyToken)).thenReturn(mockStockCall)
        Mockito.`when`(mockStockCall.execute()).thenReturn(Response.error(401, ResponseBody.create(null, "Unauthorized")))

        // Act
        val response = mockApiService.getStocks(dummyToken).execute()

        // Assert
        assert(!response.isSuccessful)
        assert(response.code() == 401)
    }

    @Test
    fun `getStockHistory should make a request with Authorization header and symbol query parameter`() {
        // This class was adapted from YouTube
        // https://youtu.be/ssF_YPvLRR8?si=OCxgobZF6XAIF5Z-
        // CodingWithPuneet
        // https://www.youtube.com/@codingwithpuneet
        // Arrange
        Mockito.`when`(mockApiService.getStockHistory(dummyToken, dummySymbol)).thenReturn(mockStockHistoryCall)
        Mockito.`when`(mockStockHistoryCall.execute()).thenReturn(Response.success(listOf(StockHistory(/*dummy data*/))))

        // Act
        val response = mockApiService.getStockHistory(dummyToken, dummySymbol).execute()

        // Assert
        assert(response.isSuccessful)
        Mockito.verify(mockApiService).getStockHistory(dummyToken, dummySymbol)
        assert(response.body()?.isNotEmpty() == true)
    }

    @Test
    fun `getStockHistory should fail when symbol is invalid`() {
        // This class was adapted from YouTube
        // https://youtu.be/ssF_YPvLRR8?si=OCxgobZF6XAIF5Z-
        // CodingWithPuneet
        // https://www.youtube.com/@codingwithpuneet
        // Arrange
        Mockito.`when`(mockApiService.getStockHistory(dummyToken, "INVALID")).thenReturn(mockStockHistoryCall)
        Mockito.`when`(mockStockHistoryCall.execute()).thenReturn(Response.error(404, ResponseBody.create(null, "Not Found")))

        // Act
        val response = mockApiService.getStockHistory(dummyToken, "INVALID").execute()

        // Assert
        assert(!response.isSuccessful)
        assert(response.code() == 404)
    }
}