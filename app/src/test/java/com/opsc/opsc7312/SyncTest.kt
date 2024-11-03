package com.opsc.opsc7312

import com.opsc.opsc7312.model.api.services.TransactionService
import com.opsc.opsc7312.model.api.services.GoalService
import com.opsc.opsc7312.model.api.services.CategoryService
import com.opsc.opsc7312.model.data.model.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import retrofit2.Call
import retrofit2.Response

@RunWith(MockitoJUnitRunner::class)
class SyncTest {

    // Mocked services
    @Mock
    private lateinit var transactionService: TransactionService

    @Mock
    private lateinit var goalService: GoalService

    @Mock
    private lateinit var categoryService: CategoryService

    // Mocked calls for sync responses
    @Mock
    private lateinit var syncTransactionsCall: Call<SyncResponse>

    @Mock
    private lateinit var syncGoalsCall: Call<SyncResponse>

    @Mock
    private lateinit var syncCategoriesCall: Call<SyncResponse>

    // Sample token and holder data
    private val token = "Bearer sampleToken"
    private val transactionsHolder = TransactionsHolder(emptyList())
    private val goalsHolder = GoalsHolder(emptyList())
    private val categoriesHolder = CategoriesHolder(emptyList())

    @Before
    fun setUp() {
        // This class was adapted from YouTube
        // https://youtu.be/ssF_YPvLRR8?si=OCxgobZF6XAIF5Z-
        // CodingWithPuneet
        // https://www.youtube.com/@codingwithpuneet
        // Set up mock responses for each sync call
        `when`(syncTransactionsCall.execute()).thenReturn(Response.success(SyncResponse( "Transactions synced successfully", true)))
        `when`(syncGoalsCall.execute()).thenReturn(Response.success(SyncResponse( "Goals synced successfully", true)))
        `when`(syncCategoriesCall.execute()).thenReturn(Response.success(SyncResponse( "Categories synced successfully", true)))

        // Set up service mock behaviors for sync calls
        `when`(transactionService.syncTransactions(token, transactionsHolder)).thenReturn(syncTransactionsCall)
        `when`(goalService.syncGoals(token, goalsHolder)).thenReturn(syncGoalsCall)
        `when`(categoryService.syncCategories(token, categoriesHolder)).thenReturn(syncCategoriesCall)
    }

    @Test
    fun `test syncTransactions success`() {
        // This class was adapted from YouTube
        // https://youtu.be/ssF_YPvLRR8?si=OCxgobZF6XAIF5Z-
        // CodingWithPuneet
        // https://www.youtube.com/@codingwithpuneet
        // Execute the syncTransactions call and verify response
        val response = transactionService.syncTransactions(token, transactionsHolder).execute()
        assert(response.isSuccessful)
        assert(response.body()?.success == true)
        assert(response.body()?.message == "Transactions synced successfully")
    }

    @Test
    fun `test syncGoals success`() {
        // This class was adapted from YouTube
        // https://youtu.be/ssF_YPvLRR8?si=OCxgobZF6XAIF5Z-
        // CodingWithPuneet
        // https://www.youtube.com/@codingwithpuneet
        // Execute the syncGoals call and verify response
        val response = goalService.syncGoals(token, goalsHolder).execute()
        assert(response.isSuccessful)
        assert(response.body()?.success == true)
        assert(response.body()?.message == "Goals synced successfully")
    }

    @Test
    fun `test syncCategories success`() {
        // This class was adapted from YouTube
        // https://youtu.be/ssF_YPvLRR8?si=OCxgobZF6XAIF5Z-
        // CodingWithPuneet
        // https://www.youtube.com/@codingwithpuneet
        // Execute the syncCategories call and verify response
        val response = categoryService.syncCategories(token, categoriesHolder).execute()
        assert(response.isSuccessful)
        assert(response.body()?.success == true)
        assert(response.body()?.message == "Categories synced successfully")
    }
}