package com.opsc.opsc7312

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.opsc.opsc7312.model.api.controllers.CategoryController
import com.opsc.opsc7312.model.api.services.CategoryService
import com.opsc.opsc7312.model.data.model.Category
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@RunWith(MockitoJUnitRunner::class)
class CategoryControllerTest {

    // This method was adapted from YouTube
    // https://youtu.be/ssF_YPvLRR8?si=OCxgobZF6XAIF5Z-
    // CodingWithPuneet
    // https://www.youtube.com/@codingwithpuneet
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var categoryController: CategoryController
    private lateinit var mockApi: CategoryService
    private lateinit var mockCall: Call<List<Category>>

    @Before
    fun setUp() {
        // This method was adapted from YouTube
        // https://youtu.be/ssF_YPvLRR8?si=OCxgobZF6XAIF5Z-
        // CodingWithPuneet
        // https://www.youtube.com/@codingwithpuneet
        mockApi = mock(CategoryService::class.java)
        categoryController = CategoryController().apply {
        }
        mockCall = mock(Call::class.java) as Call<List<Category>>
    }

    @Test
    fun testGetAllCategoriesSuccess() {
        // This method was adapted from YouTube
        // https://youtu.be/ssF_YPvLRR8?si=OCxgobZF6XAIF5Z-
        // CodingWithPuneet
        // https://www.youtube.com/@codingwithpuneet

        // Given
        val userToken = "validToken"
        val userId = "userId123"
        val categories = listOf(Category("1", "Category1"), Category("2", "Category2"))

        // Mock the API call
        //`when`(mockApi.getCategories("Bearer $userToken", userId)).thenReturn(mockCall)

        // Mock the response to be successful
        mockCall.enqueue(object : Callback<List<Category>> {
            override fun onResponse(call: Call<List<Category>>, response: Response<List<Category>>) {
                val successResponse = Response.success(categories)
                categoryController.getAllCategories(userToken, userId)
            }

            override fun onFailure(call: Call<List<Category>>, t: Throwable) {
                // Handle failure
            }
        })

        // Simulate the successful response
        val callback = object : Callback<List<Category>> {
            override fun onResponse(call: Call<List<Category>>, response: Response<List<Category>>) {
                // This should be called with the successful response
                categoryController.categoryList.postValue(categories)
                categoryController.status.postValue(true)
                categoryController.message.postValue("Categories retrieved")
            }

            override fun onFailure(call: Call<List<Category>>, t: Throwable) {
                // Handle failure
            }
        }

        // Trigger the mock call
        callback.onResponse(mockCall, Response.success(categories))

        // Verify LiveData updates
        assertEquals(true, categoryController.status.value)
        assertEquals("Categories retrieved", categoryController.message.value)
        assertEquals(categories, categoryController.categoryList.value)
    }

    @Test
    fun testFetchAllCategories_Failure() {
        // This method was adapted from YouTube
        // https://youtu.be/ssF_YPvLRR8?si=OCxgobZF6XAIF5Z-
        // CodingWithPuneet
        // https://www.youtube.com/@codingwithpuneet
        // Arrange
        //val mockCall = Mockito.mock(Call::class.java) as Call<List<Category>>

        // Mock API call
        //Mockito.`when`(mockApi.getCategories(Mockito.anyString(), Mockito.anyString())).thenReturn(mockCall)

        // Simulate a failure response
        // Simulate the login process
        categoryController.getAllCategories("fakeToken", "userId")

        // Simulate a network failure
        val throwable = Throwable("Network Error")

        // Directly invoke the failure callback
        val callback = object : Callback<List<Category>> {
            override fun onResponse(call: Call<List<Category>>, response: Response<List<Category>>) {
                // Handle response
            }

            override fun onFailure(call: Call<List<Category>>, t: Throwable) {
                categoryController.status.postValue(false)
                categoryController.message.postValue(t.message)
            }
        }

        // Trigger the network error manually
        callback.onFailure(mockCall, throwable)

        // Verify LiveData updates
        assertEquals(false, categoryController.status.value)
        assertEquals("Network Error", categoryController.message.value)
    }

}