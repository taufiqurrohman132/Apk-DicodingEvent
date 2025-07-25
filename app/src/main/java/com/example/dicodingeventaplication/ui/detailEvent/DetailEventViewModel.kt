package com.example.dicodingeventaplication.ui.detailEvent

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dicodingeventaplication.data.local.entity.EventEntity
import com.example.dicodingeventaplication.data.repository.DicodingEventRepository
import com.example.dicodingeventaplication.data.remote.model.Event
import com.example.dicodingeventaplication.utils.FavoritHelper
import com.example.dicodingeventaplication.utils.Resource
import com.example.dicodingeventaplication.utils.SingleEvent
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class DetailEventViewModel(private val repository: DicodingEventRepository) : ViewModel() {
    private val _eventData = MediatorLiveData<Resource<Event?>>()
    val eventData: LiveData<Resource<Event?>> = _eventData

    private val _dialogNotifError = MutableLiveData<SingleEvent<String?>>()
    val dialogNotifError: LiveData<SingleEvent<String?>> = _dialogNotifError

    private val _snackBarEmpty = MutableLiveData<SingleEvent<String?>>()
    val snackbarEmpty: LiveData<SingleEvent<String?>> = _snackBarEmpty

    private val _isReload = MutableLiveData(false)
    val isReload: LiveData<Boolean> = _isReload

    private val _isRefresing = MutableLiveData(false)
    val isRefresing: LiveData<Boolean> = _isRefresing

    var isDetailSuccess = false

    fun findDetailEvent(id: Int){
        viewModelScope.launch {
            delay(500)

            Log.d("detailvm", "findDetailEvent: jalan")
            val sourse = repository.findDetailEvent(id)
            _eventData.removeSource(sourse)
            _eventData.addSource(sourse){ data ->
                when (data) {
                    is Resource.Error, is Resource.ErrorConection -> {
                        _dialogNotifError.value = SingleEvent(data.message)
                        _eventData.value = data
                        Log.d("detailvm", "findDetailEvent: eror $data")
                    }

                    is Resource.Empty -> {
                        _snackBarEmpty.value = SingleEvent(data.message)
                    }

                    else -> {
                        Log.d("detailvm", "findDetailEvent: data $data")
                        _eventData.value = data
                    }
                }
            }

            _isRefresing.value = false
            _isReload.value = false
        }
    }

    fun markUpcomingSuccess(){
        isDetailSuccess = true
    }

    fun startReload(){
        _isReload.value = true
    }

    fun startRefreshing(){
        _isRefresing.value = true
    }

    // favorit
    fun onFavoritClicked(favoritEvent: EventEntity, createAt: Long){
        viewModelScope.launch {
            val newBookmarked = repository.setFavoritState(favoritEvent.id)
            FavoritHelper.togleFavorit(viewModelScope, repository, favoritEvent, newBookmarked, createAt)
        }
    }

    fun getFavoritById(id: Int): LiveData<EventEntity> =
        repository.observeFavoritById(id)
}