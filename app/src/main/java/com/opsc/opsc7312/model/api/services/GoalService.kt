package com.opsc.opsc7312.model.api.services

import com.opsc.opsc7312.model.data.Goal
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface GoalService {
    @GET("goal/{id}")
    fun getGoals(@Path("id") userId: String): Call<List<Goal>>

    @POST("goal/create")
    fun createGoal(@Body goal: Goal): Call<Goal>

    @PUT("goal/{id}")
    fun updateGoal(@Path("id") id: String, @Body goal: Goal): Call<Goal>

    @DELETE("goal/{id}")
    fun deleteGoal(@Path("id") id: String): Call<Void>
}