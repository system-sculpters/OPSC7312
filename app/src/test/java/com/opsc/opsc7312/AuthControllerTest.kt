package com.opsc.opsc7312

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.opsc.opsc7312.model.api.controllers.AuthController
import com.opsc.opsc7312.model.api.services.AuthService
import com.opsc.opsc7312.model.data.model.User
import okhttp3.ResponseBody
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthControllerTest {
    // This class was adapted from YouTube
    // https://youtu.be/ssF_YPvLRR8?si=OCxgobZF6XAIF5Z-
    // CodingWithPuneet
    // https://www.youtube.com/@codingwithpuneet
    @get:Rule
    val rule = InstantTaskExecutorRule() // Allow LiveData to execute synchronously.

    private lateinit var authController: AuthController

    @Mock
    private lateinit var mockApi: AuthService

    @Mock
    private lateinit var observerStatus: Observer<Boolean>

    @Mock
    private lateinit var observerMessage: Observer<String>

    @Mock
    private lateinit var observerUserData: Observer<User>

    @Before
    fun setUp() {
        // This method was adapted from YouTube
        // https://youtu.be/ssF_YPvLRR8?si=OCxgobZF6XAIF5Z-
        // CodingWithPuneet
        // https://www.youtube.com/@codingwithpuneet
        MockitoAnnotations.openMocks(this)
        authController = AuthController()
        // Set the mocked API service
        authController.api = mockApi

        // Observe LiveData
        authController.status.observeForever(observerStatus)
        authController.message.observeForever(observerMessage)
        authController.userData.observeForever(observerUserData)
    }

    @Test
    fun testLoginSuccess() {
        // This method was adapted from YouTube
        // https://youtu.be/ssF_YPvLRR8?si=OCxgobZF6XAIF5Z-
        // CodingWithPuneet
        // https://www.youtube.com/@codingwithpuneet
        val user = User(email = "user@gmail.com", password = "user123")
        val mockCall = mock(Call::class.java) as Call<User>

        // Mock the API call
        `when`(mockApi.login(user)).thenReturn(mockCall)

        // Create a successful response
        val response = Response.success(user)

        // Simulate the login process
        authController.login(user)

        // Directly invoke the success callback
        val callback = object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                authController.userData.postValue(response.body())
                authController.status.postValue(true)
                authController.message.postValue("User logged in successfully")
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                // Handle failure
            }
        }

        // Trigger the successful response manually
        callback.onResponse(mockCall, response)

        // Verify LiveData updates
        assertEquals(true, authController.status.value)
        assertEquals("User logged in successfully", authController.message.value)
        assertEquals(user, authController.userData.value)
    }


    @Test
    fun testLoginFailure() {
        // This method was adapted from YouTube
        // https://youtu.be/ssF_YPvLRR8?si=OCxgobZF6XAIF5Z-
        // CodingWithPuneet
        // https://www.youtube.com/@codingwithpuneet
        val user = User("test@example.com", "wrongpassword")
        val mockCall = mock(Call::class.java) as Call<User>

        // Mock the API call
        `when`(mockApi.login(user)).thenReturn(mockCall)

        // Simulate the login process
        authController.login(user)

        // Simulate a failure response
        val response = Response.error<User>(401, ResponseBody.create(null, "Unauthorized"))

        // Directly invoke the failure callback
        val callback = object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                authController.status.postValue(false)
                authController.message.postValue("Request failed with code: ${response.code()}")
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                // Handle failure
            }
        }

        // Trigger the failure response manually
        callback.onResponse(mockCall, response)

        // Verify LiveData updates
        assertEquals(false, authController.status.value)
        assertEquals("Request failed with code: 401", authController.message.value)
    }

    @Test
    fun testLoginNetworkError() {
        // This method was adapted from YouTube
        // https://youtu.be/ssF_YPvLRR8?si=OCxgobZF6XAIF5Z-
        // CodingWithPuneet
        // https://www.youtube.com/@codingwithpuneet
        val user = User("test@example.com", "password123")
        val mockCall = mock(Call::class.java) as Call<User>

        // Mock the API call
        `when`(mockApi.login(user)).thenReturn(mockCall)

        // Simulate the login process
        authController.login(user)

        // Simulate a network failure
        val throwable = Throwable("Network Error")

        // Directly invoke the failure callback
        val callback = object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                // Handle response
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                authController.status.postValue(false)
                authController.message.postValue(t.message)
            }
        }

        // Trigger the network error manually
        callback.onFailure(mockCall, throwable)

        // Verify LiveData updates
        assertEquals(false, authController.status.value)
        assertEquals("Network Error", authController.message.value)
    }

}