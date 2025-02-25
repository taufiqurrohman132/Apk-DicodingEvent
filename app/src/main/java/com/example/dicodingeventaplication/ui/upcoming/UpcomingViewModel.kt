package com.example.dicodingeventaplication.ui.upcoming

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dicodingeventaplication.utils.Resource
import com.example.dicodingeventaplication.data.repository.DicodingEventRepository
import com.example.dicodingeventaplication.data.respons.EventItem
import com.example.dicodingeventaplication.ui.home.HomeFragment
import com.example.dicodingeventaplication.utils.SingleEvent
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class UpcomingViewModel(private val repository: DicodingEventRepository) : ViewModel() {

    private val _resultEvenItemUpcome = MutableLiveData<Resource<List<EventItem?>>>()
    val resultEventItemUpcome: LiveData<Resource<List<EventItem?>>> = _resultEvenItemUpcome

    private val _dialogNotifError = MutableLiveData<SingleEvent<String?>>()
    val dialogNotifError: LiveData<SingleEvent<String?>> = _dialogNotifError

    private val _isRefresing = MutableLiveData(false)
    val isRefresing: LiveData<Boolean> = _isRefresing

    var isUpcomingSuccess = false
    var isUpcomingEmpty = false

    init {
        findEventUpcome()
    }

    fun findEventUpcome(){
        viewModelScope.launch {
            delay(500)
            Log.d(UpcomingFragment.TAG, "findEvent upcome berjalan di thread: ${Thread.currentThread().name}")

            repository.findEvent(HomeFragment.UPCOMING) { event ->
                _resultEvenItemUpcome.value = when(event){
                    is Resource.Error -> {
                        _dialogNotifError.value = SingleEvent(event.message)
                        event
                    }
                    is Resource.ErrorConection -> {
                        _dialogNotifError.value = SingleEvent(event.message)
                        event
                    }
                    else-> event
                }
            }
            _isRefresing.value = false
        }
    }

    fun startRefreshing(){
        _isRefresing.value = true
    }

    fun markUpcomingSuccess(){
        isUpcomingSuccess = true
    }

    fun markUpcomingEmpty(){
        isUpcomingEmpty = true
    }


}