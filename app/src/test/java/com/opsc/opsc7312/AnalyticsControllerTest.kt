package com.opsc.opsc7312

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.opsc.opsc7312.model.api.controllers.AnalyticsController
import com.opsc.opsc7312.model.api.retrofitclients.RetrofitClient
import com.opsc.opsc7312.model.api.services.AnalyticsService
import com.opsc.opsc7312.model.data.model.AnalyticsResponse
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AnalyticsControllerTest {
    // This class was adapted from YouTube
    // https://youtu.be/ssF_YPvLRR8?si=OCxgobZF6XAIF5Z-
    // CodingWithPuneet
    // https://www.youtube.com/@codingwithpuneet
    var userid = "0AqZwpqX2seecn6855llTwlnr9J2"
    var token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1aWQiOiIwQXFad3BxWDJzZWVjbjY4NTVsbFR3bG5yOUoyIiwiZW1haWwiOiJ1c2VyQGdtYWlsLmNvbSIsImlhdCI6MTcyNzU2NTQzMiwiZXhwIjoxNzM2MjA1NDMyfQ.kRS7sAtT1PYpG8M9wCC65yqbAvR5yeBpgfsdxGU4cR4"

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private val analyticsService: AnalyticsService = Mockito.mock(AnalyticsService::class.java)
    private lateinit var analyticsController: AnalyticsController

    @Before
    fun setup() {
        // This method was adapted from YouTube
        // https://youtu.be/ssF_YPvLRR8?si=OCxgobZF6XAIF5Z-
        // CodingWithPuneet
        // https://www.youtube.com/@codingwithpuneet
        //
        // Initialize the controller with the mocked service
        analyticsController = AnalyticsController()
        analyticsController.api = analyticsService
    }

    @Test
    fun testFetchAllAnalytics_Success() {
        // This method was adapted from YouTube
        // https://youtu.be/ssF_YPvLRR8?si=OCxgobZF6XAIF5Z-
        // CodingWithPuneet
        // https://www.youtube.com/@codingwithpuneet
        // Arrange
        val mockResponse = createMockAnalyticsResponse()
        val call: Call<AnalyticsResponse> = Mockito.mock(Call::class.java) as Call<AnalyticsResponse>

        // Define behavior for the mock API call
        Mockito.`when`(analyticsService.getAllAnalytics(Mockito.anyString(), Mockito.anyString())).thenReturn(call)

        // Enqueue the response
        Mockito.doAnswer {
            val callback: Callback<AnalyticsResponse> = it.getArgument(0)
            callback.onResponse(call, Response.success(mockResponse))
        }.`when`(call).enqueue(Mockito.any())

        // Act
        analyticsController.fetchAllAnalytics(token, userid)

        // Assert
        assertTrue(analyticsController.status.value == true)
        assertEquals("Goals retrieved", analyticsController.message.value)
        assertEquals(mockResponse, analyticsController.analytics.value)
    }

    private fun createMockAnalyticsResponse(): AnalyticsResponse {
        return AnalyticsResponse(listOf(), listOf(), listOf(), listOf())
    }

    @Test
    fun testFetchAllAnalytics_Failure() {
        // This method was adapted from YouTube
        // https://youtu.be/ssF_YPvLRR8?si=OCxgobZF6XAIF5Z-
        // CodingWithPuneet
        // https://www.youtube.com/@codingwithpuneet
        // Arrange
        val call: Call<AnalyticsResponse> = Mockito.mock(Call::class.java) as Call<AnalyticsResponse>

        // Define behavior for the mock API call
        Mockito.`when`(analyticsService.getAllAnalytics(Mockito.anyString(), Mockito.anyString())).thenReturn(call)

        // Enqueue a failure response
        Mockito.doAnswer {
            val callback: Callback<AnalyticsResponse> = it.getArgument(0)
            callback.onFailure(call, Throwable("Network error"))
        }.`when`(call).enqueue(Mockito.any())

        // Act
        analyticsController.fetchAllAnalytics(token, userid)

        // Assert
        assertTrue(analyticsController.status.value == false)
        assertEquals("Network error", analyticsController.message.value)
    }
}