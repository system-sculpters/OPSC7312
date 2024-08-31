package com.opsc.opsc7312.model.api.services

import com.opsc.opsc7312.model.data.model.Goal
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface GoalService {
    @GET("goal/{id}")
    fun getGoals(@Header("Authorization") token: String, @Path("id") userId: String): Call<List<Goal>>

    @POST("goal/create")
    fun createGoal(@Header("Authorization") token: String, @Body goal: Goal): Call<Goal>

    @PUT("goal/{id}")
    fun updateGoal(@Header("Authorization") token: String, @Path("id") id: String, @Body goal: Goal): Call<Goal>

    @DELETE("goal/{id}")
    fun deleteGoal(@Header("Authorization") token: String, @Path("id") id: String): Call<Void>
}