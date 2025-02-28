package com.example.dicodingeventaplication.data.network

import com.example.dicodingeventaplication.data.model.DetailEventResponse
import com.example.dicodingeventaplication.data.model.EventResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    // detail
    @GET("events/{id}")
    fun getEventDetail(
        @Path("id") id: Int
    ): Call<DetailEventResponse>

    // get event aktive
    @GET("events")
    fun getEventActive(
        @Query("active") active: Int
    ): Call<EventResponse>

    // search event
    @GET("events")
    fun searchEvent(
        @Query("active") active: Int = -1,
        @Query("q") query: String
    ): Call<EventResponse>
}