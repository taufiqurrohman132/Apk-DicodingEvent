package com.example.dicodingeventaplication.ui.finished

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

class FinishedViewModel(private val repository: DicodingEventRepository) : ViewModel() {

    private val _resultEvenItemFinished = MutableLiveData<Resource<List<EventItem?>>>()
    val resultEventItemFinished: LiveData<Resource<List<EventItem?>>> = _resultEvenItemFinished

    private val _dialogNotifError = MutableLiveData<SingleEvent<String?>>()
    val dialogNotifError: LiveData<SingleEvent<String?>> = _dialogNotifError

    private val _isRefresing = MutableLiveData(false)
    val isRefresing: LiveData<Boolean> = _isRefresing

    var isFinishedSuccess = false

    init {
        findEventFinished()
    }

    fun findEventFinished(){
        viewModelScope.launch {
            delay(500)

            repository.findEvent(HomeFragment.FINISHED) { event ->
                _resultEvenItemFinished.value = when(event){
                    is Resource.Error -> {
                        _dialogNotifError.value = SingleEvent(event.message)
                        event
                    }
                    is Resource.ErrorConection -> {
                        _dialogNotifError.value = SingleEvent(event.message)
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

    fun markFinishedSuccess(){
        isFinishedSuccess = true
    }
}