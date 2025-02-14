package com.example.dicodingeventaplication.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dicodingeventaplication.Resource
import com.example.dicodingeventaplication.data.repository.DicodingEventRepository
import com.example.dicodingeventaplication.data.respons.EventItem
import com.example.dicodingeventaplication.ui.home.HomeFragment.Companion.TAG
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: DicodingEventRepository) : ViewModel() {

    private val _resultEvenItemFinished = MutableLiveData<Resource<List<EventItem?>>>()
    val resultEventItemFinished: LiveData<Resource<List<EventItem?>>> = _resultEvenItemFinished

    private val _resultEvenItemUpcome = MutableLiveData<Resource<List<EventItem?>>>()
    val resultEventItemUpcome: LiveData<Resource<List<EventItem?>>> = _resultEvenItemUpcome

    private val _headerEvent = MutableLiveData<Resource<List<EventItem?>>>()
    val headerEvent: LiveData<Resource<List<EventItem?>>> = _headerEvent

    private val _imageHeaderUrl = MutableLiveData<String?>()
    val imageHeaderUrl: LiveData<String?> = _imageHeaderUrl

    init {
//        findEventUpcome()
//        findEventFinished()
        findImageHeader()
    }

    fun findImageHeader(callback: (() -> Unit)? = null){
        Log.d(HomeFragment.TAG, "findImageHeader dipanggil")
        viewModelScope.launch {
            delay(500)
            Log.d(HomeFragment.TAG, "findEvent finish berjalan di thread: ${Thread.currentThread().name}")

            repository.findEvent(HomeFragment.FINISHED) { event ->
                _headerEvent.value = event
                _imageHeaderUrl.value = event.data?.getOrNull(6)?.mediaCover
            }
            findEventUpcome{
                callback?.invoke()
            }
        }
    }

    fun findEventFinished(){
        viewModelScope.launch {
            delay(1500)
            Log.d(HomeFragment.TAG, "findEvent finish berjalan di thread: ${Thread.currentThread().name}")

            repository.findEvent(HomeFragment.FINISHED) { event ->
                _resultEvenItemFinished.value = event
            }
        }
    }

    fun findEventUpcome(callback: (() -> Unit)? = null){
        viewModelScope.launch {
            delay(1000)
            Log.d(HomeFragment.TAG, "findEvent upcome berjalan di thread: ${Thread.currentThread().name}")

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
                callback?.invoke()
            }
            findEventFinished()
        }
    }
}