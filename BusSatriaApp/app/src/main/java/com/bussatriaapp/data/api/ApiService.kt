package com.bussatriaapp.data.api

import com.bussatriaapp.data.model.Bus
import com.bussatriaapp.data.model.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {
    @POST("/register")
    suspend fun registerUser(@Body user: User): Response<User>

    @POST("/login")
    suspend fun loginUser(@Body credentials: Map<String, String>): Response<Map<String, String>>

    @GET("/buses")
    suspend fun getBuses(): Response<List<Bus>>

    @PUT("/buses/{id}/location")
    suspend fun updateBusLocation(@Path("id") id: Int, @Body location: Map<String, Double>): Response<Void>
}