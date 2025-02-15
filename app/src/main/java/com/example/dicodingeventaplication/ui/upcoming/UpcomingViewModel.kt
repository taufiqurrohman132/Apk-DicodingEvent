package com.example.dicodingeventaplication.ui.upcoming

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
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

class UpcomingViewModel(private val repository: DicodingEventRepository) : ViewModel() {

    private val _resultEvenItemUpcome = MutableLiveData<Resource<List<EventItem?>>>()
    val resultEventItemUpcome: LiveData<Resource<List<EventItem?>>> = _resultEvenItemUpcome

//    private val _isRefresing = MutableLiveData<Boolean>()
//    val isRefresing: LiveData<Boolean> = _isRefresing

    init {
        findEventUpcome()
    }

    fun findEventUpcome(callback: (() -> Unit)? = null){
        viewModelScope.launch {
            delay(1500)
            Log.d(UpcomingFragment.TAG, "findEvent upcome berjalan di thread: ${Thread.currentThread().name}")

            repository.findEvent(HomeFragment.UPCOMING) { event ->
                _resultEvenItemUpcome.value = event
                callback?.invoke()
            }
        }
    }
}