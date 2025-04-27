package com.example.dicodingeventaplication.data.di

import android.content.Context
import com.example.dicodingeventaplication.data.local.database.FavoritEventRoomDatabase
import com.example.dicodingeventaplication.data.remote.network.ApiConfig
import com.example.dicodingeventaplication.data.repository.DicodingEventRepository
//import com.example.dicodingeventaplication.utils.AppExecutors
//import com.example.dicodingeventaplication.data.repository.FavoritEventRepository
//import com.example.dicodingeventaplication.utils.AppExecutors
import com.example.dicodingeventaplication.utils.DefaultResourceProvider
import com.example.dicodingeventaplication.utils.SharedPrefHelper

object Injection {
    fun provideRepository(context: Context): DicodingEventRepository{
        val sharedPrefHelper = SharedPrefHelper(context)
        val resourceProvider = DefaultResourceProvider(context)
        val apiService = ApiConfig.getApiService()
        val database = FavoritEventRoomDatabase.getInstance(context)
        val dao = database.favoritEventDao()
//        val appExecutors = AppExecutors()
        return DicodingEventRepository.getInstance(resourceProvider,sharedPrefHelper, apiService, dao)
    }
}