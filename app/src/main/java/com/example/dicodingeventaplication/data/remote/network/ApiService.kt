package com.example.dicodingeventaplication.data.remote.network

import com.example.dicodingeventaplication.data.remote.model.DetailEventResponse
import com.example.dicodingeventaplication.data.remote.model.EventResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    // detail
    @GET("events/{id}")
    suspend fun getEventDetail(
        @Path("id") id: Int
    ): Response<DetailEventResponse>

    // get event aktive
    @GET("events")
    suspend fun getEventActive(
        @Query("active") active: Int
    ): Response<EventResponse>

    // search event
    @GET("events")
    suspend fun searchEvent(
        @Query("active") active: Int = -1,
        @Query("q") query: String
    ): Response<EventResponse>
}