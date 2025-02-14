package com.example.dicodingeventaplication.ui.finished

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dicodingeventaplication.Resource
import com.example.dicodingeventaplication.data.repository.DicodingEventRepository
import com.example.dicodingeventaplication.data.respons.EventItem
import com.example.dicodingeventaplication.ui.home.HomeFragment
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class FinishedViewModel(private val repository: DicodingEventRepository) : ViewModel() {

    private val _resultEvenItemUpcome = MutableLiveData<Resource<List<EventItem?>>>()
    val resultEventItemUpcome: LiveData<Resource<List<EventItem?>>> = _resultEvenItemUpcome

    init {
        findEventUpcome()
    }

    fun findEventUpcome(callback: (() -> Unit)? = null){
        viewModelScope.launch {
            delay(500)

            repository.findEvent(HomeFragment.FINISHED) { event ->
                _resultEvenItemUpcome.value = event
                callback?.invoke()
            }
        }
    }
}