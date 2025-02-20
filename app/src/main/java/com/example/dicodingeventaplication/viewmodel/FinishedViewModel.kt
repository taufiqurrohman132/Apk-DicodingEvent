package com.example.dicodingeventaplication.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dicodingeventaplication.utils.Resource
import com.example.dicodingeventaplication.data.repository.DicodingEventRepository
import com.example.dicodingeventaplication.data.respons.EventItem
import com.example.dicodingeventaplication.ui.home.HomeFragment
import com.example.dicodingeventaplication.utils.Event
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class FinishedViewModel(private val repository: DicodingEventRepository) : ViewModel() {

    private val _resultEvenItemFinished = MutableLiveData<Resource<List<EventItem?>>>()
    val resultEventItemFinished: LiveData<Resource<List<EventItem?>>> = _resultEvenItemFinished

    private val _dialogNotifError = MutableLiveData<Event<String?>>()
    val dialogNotifError: LiveData<Event<String?>> = _dialogNotifError

    private val _isRefresing = MutableLiveData(false)
    val isRefresing: LiveData<Boolean> = _isRefresing

    var isFinishedSuccess = false

    var appBarOffset: Int = 0

    init {
        findEventFinished()
    }

    fun findEventFinished(){
        viewModelScope.launch {
            delay(1000)

            repository.findEvent(HomeFragment.FINISHED) { event ->
                _resultEvenItemFinished.value = when(event){
                    is Resource.Error -> {
                        _dialogNotifError.value = Event(event.message)
                        event
                    }
                    is Resource.ErrorConection -> {
                        _dialogNotifError.value = Event(event.message)
                        event
                    }
                    else -> event
                }
            }

            _isRefresing.value = false
        }
    }

    fun startRefreshing(){
        _isRefresing.value = true
    }


    fun isFinishedSuccess(){
        isFinishedSuccess = true
    }
}