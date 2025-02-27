package com.example.dicodingeventaplication.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dicodingeventaplication.utils.Resource
import com.example.dicodingeventaplication.data.repository.DicodingEventRepository
import com.example.dicodingeventaplication.data.respons.EventItem
import com.example.dicodingeventaplication.ui.home.HomeFragment.Companion.TAG
import com.example.dicodingeventaplication.utils.SingleEvent
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: DicodingEventRepository) : ViewModel() {

    private val _resultEvenItemFinished = MutableLiveData<Resource<List<EventItem?>>>()
    val resultEventItemFinished: LiveData<Resource<List<EventItem?>>> = _resultEvenItemFinished

    private val _resultEvenItemUpcome = MutableLiveData<Resource<List<EventItem?>>>()
    val resultEventItemUpcome: LiveData<Resource<List<EventItem?>>> = _resultEvenItemUpcome

    private val _headerEvent = MutableLiveData<Resource<List<EventItem?>>>()
    val headerEvent: LiveData<Resource<List<EventItem?>>> = _headerEvent

    private val _dialogNotifError = MutableLiveData<SingleEvent<String?>>()
    val dialogNotifError: LiveData<SingleEvent<String?>> = _dialogNotifError

    private var _isReload = MutableLiveData(false)
    val isReload: LiveData<Boolean> = _isReload

    private var _isRefreshing = MutableLiveData(false)
    val isRefreshing: LiveData<Boolean> = _isRefreshing

    var isUpcomingSuccess = false
    var isFinishedSuccess = false
    var isHeaderSuccess = false

    private var job: Job? = null

    var scrollY: Int= 0

    init {
        findImageHeader()
    }

    fun findImageHeader(){ //callback: (() -> Unit)? = null
        job?.cancel()

        Log.d(TAG, "findImageHeader dipanggil")

        job = viewModelScope.launch {
            delay(1000)
            Log.d(TAG, "findEvent heder berjalan di thread: ${Thread.currentThread().name}")

            repository.findEvent(HomeFragment.FINISHED) { event ->
                _headerEvent.value = when(event){
                    is Resource.ErrorConection -> {
                        _dialogNotifError.value = SingleEvent(event.message)
                        event
                    }
                    is Resource.Error ->{
                        _dialogNotifError.value = SingleEvent(event.message)
                        event
                    }
                    else -> event
                }
                _isRefreshing.value = false
                _isReload.value = false
            }
            findEventUpcome()
        }
    }

    fun findEventFinished(){
        viewModelScope.launch {
            delay(500)
            Log.d(TAG, "findEvent finish berjalan di thread: ${Thread.currentThread().name}")

            repository.findEvent(HomeFragment.FINISHED) { event ->
                _resultEvenItemFinished.value = event
            }
        }
    }

    fun findEventUpcome(){
        viewModelScope.launch {
            delay(500)
            Log.d(TAG, "findEvent upcome berjalan di thread: ${Thread.currentThread().name}")

            repository.findEvent(HomeFragment.UPCOMING) { event ->
                _resultEvenItemUpcome.value= when(event) {
                    is Resource.Success -> {
                        val itemFromApi = event.data ?: emptyList()
                        if (itemFromApi.size in 1..4){
                            Resource.Success(event.data!! + listOf(null))// jika kosong, tambahkan list kosong
                        } else {
                            event
                        }
                    }
                    is Resource.Error -> {
                        event
                    }
                    is Resource.ErrorConection -> {
                        event
                    }
                    is Resource.Empty -> {
                        Resource.Success(listOf(null, null))// jika kosong, tambahkan list kosong
                    }
                    else -> event
                }
                Log.d(TAG, "viewmodel upcome: $event")
            }
            findEventFinished()
        }
    }

    fun isUpcomingSuccess(){
        isUpcomingSuccess = true
    }
    fun isFinishedSuccess(){
        isFinishedSuccess = true
    }

    fun isHeaderSuccess(){
        isHeaderSuccess = true
    }

    fun startReload(){
        _isReload.value = true
    }

    fun startRefreshing(){
        _isRefreshing.value = true
    }

}