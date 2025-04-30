package com.example.dicodingeventaplication.ui.upcoming

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dicodingeventaplication.data.local.entity.FavoritEvent
import com.example.dicodingeventaplication.utils.Resource
import com.example.dicodingeventaplication.data.repository.DicodingEventRepository
import com.example.dicodingeventaplication.ui.home.HomeFragment
import com.example.dicodingeventaplication.utils.DataStatehelper
import com.example.dicodingeventaplication.utils.FavoritHelper
import com.example.dicodingeventaplication.utils.SingleEvent
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class UpcomingViewModel(
    private val repository: DicodingEventRepository,
) : ViewModel() {
    private val _resultEvenItemUpcome = MediatorLiveData<Resource<List<FavoritEvent?>>?>()
    val resultEventItemUpcome: LiveData<Resource<List<FavoritEvent?>>?> = _resultEvenItemUpcome

    private val _hasLocalDataUpcome = MutableStateFlow(false)
    val hasLocalDataUpcome: StateFlow<Boolean> = _hasLocalDataUpcome.asStateFlow()

    private val _dialogNotifError = MutableLiveData<SingleEvent<String?>>()
    val dialogNotifError: LiveData<SingleEvent<String?>> = _dialogNotifError

    private val _isReload = MutableLiveData(false)
    val isReload: LiveData<Boolean> = _isReload

    private val _isRefreshing = MutableLiveData(false)
    val isRefreshing: LiveData<Boolean> = _isRefreshing

    var isUpcomingSuccess = false
    var isUpcomingEmpty = false

    init {
        findEventUpcome()

        DataStatehelper.getHasLocaUpcomelState()
            .onEach {
                _hasLocalDataUpcome.value = it
            }.launchIn(viewModelScope)
    }

    fun findEventUpcome(){
        viewModelScope.launch {
            delay(500)
            Log.d(UpcomingFragment.TAG, "findEvent upcome berjalan di thread: ${Thread.currentThread().name}")

            val sourse = repository.findEvent(HomeFragment.UPCOMING)
            _resultEvenItemUpcome.removeSource(sourse)
            _resultEvenItemUpcome.addSource(sourse) { event ->
                if (event is Resource.Error || event is Resource.ErrorConection){
                    _dialogNotifError.value = SingleEvent(event.message)
                } else if (event is Resource.Success){
                    viewModelScope.launch {
                        DataStatehelper.setHasLocalDataUpcome(true)
                    }
                }
                _resultEvenItemUpcome.value = event
                Log.d(UpcomingFragment.TAG, "findEventUpcome: event is $event")
            }

            Log.d(UpcomingFragment.TAG, "findEventUpcome: pesan eror ${_dialogNotifError.value}")

            _isRefreshing.value = false
            _isReload.value = false
        }
    }

    fun startReload(){
        _isReload.value = true
    }

    fun startRefreshing(){
        _isRefreshing.value = true
    }

    fun markUpcomingSuccess(){
        isUpcomingSuccess = true
    }

    fun markUpcomingEmpty(){
        isUpcomingEmpty = true
    }

    // Favorit
    fun onFavoritClicked(favorit: FavoritEvent, isBookmarked: Boolean, createAt: Long) {
        FavoritHelper.togleFavorit(viewModelScope, repository, favorit, isBookmarked, createAt)
    }
}