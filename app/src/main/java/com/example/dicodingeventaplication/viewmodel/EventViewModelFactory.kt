package com.example.dicodingeventaplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.dicodingeventaplication.data.repository.DicodingEventRepository
import com.example.dicodingeventaplication.ui.detailEvent.DetailEventViewModel
import com.example.dicodingeventaplication.ui.finished.FinishedViewModel
import com.example.dicodingeventaplication.ui.home.HomeViewModel
import com.example.dicodingeventaplication.ui.search.SearchViewModel
import com.example.dicodingeventaplication.ui.upcoming.UpcomingViewModel

class EventViewModelFactory(private val repository: DicodingEventRepository, private val id: Int? = null) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        if (modelClass.isAssignableFrom(SearchViewModel::class.java)){
            return SearchViewModel(repository) as T
        }else if (modelClass.isAssignableFrom(HomeViewModel::class.java)){
            return HomeViewModel(repository) as T
        }else if (modelClass.isAssignableFrom(UpcomingViewModel::class.java)){
            return UpcomingViewModel(repository) as T
        } else if (modelClass.isAssignableFrom(FinishedViewModel::class.java)){
            return FinishedViewModel(repository) as T
        } else if (modelClass.isAssignableFrom(DetailEventViewModel::class.java)){
            return DetailEventViewModel(repository, id) as T
        }
        throw IllegalArgumentException("Unknwon Viewmodel Class")
    }
}