package com.example.dicodingeventaplication.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.dicodingeventaplication.data.di.Injection
import com.example.dicodingeventaplication.data.repository.DicodingEventRepository
//import com.example.dicodingeventaplication.data.repository.FavoritEventRepository
import com.example.dicodingeventaplication.ui.detailEvent.DetailEventViewModel
import com.example.dicodingeventaplication.ui.favorite.FavoritViewModel
import com.example.dicodingeventaplication.ui.finished.FinishedViewModel
import com.example.dicodingeventaplication.ui.home.HomeViewModel
import com.example.dicodingeventaplication.ui.search.SearchViewModel
import com.example.dicodingeventaplication.ui.upcoming.UpcomingViewModel

class EventViewModelFactory(
    private val repository: DicodingEventRepository,
//    private val id: Int? = null
) : ViewModelProvider.NewInstanceFactory() {
    companion object{
        @Volatile
        private var instance: EventViewModelFactory? = null

        fun getInstance(context: Context): EventViewModelFactory =
            instance?: synchronized(this){
                instance ?: EventViewModelFactory(Injection.provideRepository(context))
            }
    }

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return when (modelClass) {
            SearchViewModel::class.java -> SearchViewModel(repository)
            HomeViewModel::class.java -> HomeViewModel(repository)
            UpcomingViewModel::class.java -> UpcomingViewModel(repository)
            FinishedViewModel::class.java -> FinishedViewModel(repository)
            DetailEventViewModel::class.java -> DetailEventViewModel(repository)
            FavoritViewModel::class.java -> FavoritViewModel(repository)
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        } as T

    }
}