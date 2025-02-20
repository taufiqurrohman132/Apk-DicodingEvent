package com.example.dicodingeventaplication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.dicodingeventaplication.data.repository.DicodingEventRepository
import com.example.dicodingeventaplication.viewmodel.FinishedViewModel
import com.example.dicodingeventaplication.viewmodel.HomeViewModel
import com.example.dicodingeventaplication.viewmodel.SearchViewModel
import com.example.dicodingeventaplication.viewmodel.UpcomingViewModel

class EventViewModelFactory(private val repository: DicodingEventRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        if (modelClass.isAssignableFrom(SearchViewModel::class.java)){
            return SearchViewModel(repository) as T
        }else if (modelClass.isAssignableFrom(HomeViewModel::class.java)){
            return HomeViewModel(repository) as T
        }else if (modelClass.isAssignableFrom(UpcomingViewModel::class.java)){
            return UpcomingViewModel(repository) as T
        }else if (modelClass.isAssignableFrom(FinishedViewModel::class.java)){
            return FinishedViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknwon Viewmodel Class")
    }
}