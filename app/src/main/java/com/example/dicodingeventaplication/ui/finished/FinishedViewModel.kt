package com.example.dicodingeventaplication.ui.finished

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.dicodingeventaplication.Resource
import com.example.dicodingeventaplication.data.repository.DicodingEventRepository
import com.example.dicodingeventaplication.data.respons.EventItem
import com.example.dicodingeventaplication.ui.home.HomeFragment

class FinishedViewModel(private val repository: DicodingEventRepository) : ViewModel() {

    private val _resultEvenItemUpcome = MutableLiveData<Resource<List<EventItem>>>()
    val resultEventItemUpcome: LiveData<Resource<List<EventItem>>> = _resultEvenItemUpcome

    init {
        findEventUpcome()
    }

    private fun findEventUpcome(){
        repository.findEvent(HomeFragment.FINISHED) { event ->
            _resultEvenItemUpcome.value = event
        }
    }
}