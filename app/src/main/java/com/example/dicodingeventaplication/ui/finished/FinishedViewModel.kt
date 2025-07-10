package com.example.dicodingeventaplication.ui.finished

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dicodingeventaplication.data.local.entity.EventEntity
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

class FinishedViewModel(private val repository: DicodingEventRepository) : ViewModel() {

    private val _resultEvenItemFinished = MediatorLiveData<Resource<List<EventEntity?>>?>()
    val resultEventItemFinished: LiveData<Resource<List<EventEntity?>>?> = _resultEvenItemFinished

    private val _hasLocalData = MutableStateFlow(false)
    val hasLocalData: StateFlow<Boolean> = _hasLocalData.asStateFlow()

    private val _dialogNotifError = MutableLiveData<SingleEvent<String?>>()
    val dialogNotifError: LiveData<SingleEvent<String?>> = _dialogNotifError

    private val _listSearchResult = MutableLiveData<Resource<List<EventEntity?>?>>()
    val listSearchResult: LiveData<Resource<List<EventEntity?>?>> get() = _listSearchResult

    private val _isReload = MutableLiveData(false)
    val isReload: LiveData<Boolean> = _isReload

    private val _isRefresing = MutableLiveData(false)
    val isRefresing: LiveData<Boolean> = _isRefresing

    var isSearching = false

    var isCollapse = false

    var isFinishedSuccess = false

    init {
        findEventFinished()

        // Pantau status data lokal dari DataStore
        DataStatehelper.getHasLocaFinishedlState()
            .onEach {
                _hasLocalData.value = it
            }.launchIn(viewModelScope)
    }

    fun findEventFinished(){
        viewModelScope.launch {
            delay(500)

            val sourse = repository.findEvent(HomeFragment.FINISHED)
            _resultEvenItemFinished.removeSource(sourse)
            _resultEvenItemFinished.addSource(sourse) { event ->
                if (event is Resource.Error || event is Resource.ErrorConection){
                    _dialogNotifError.value = SingleEvent(event.message)
                }else if (event is Resource.Success){
                    viewModelScope.launch {
                        DataStatehelper.setHasLocalDataFinished(true)
                    }
                }

                _resultEvenItemFinished.value = event
                Log.d(FinishedFragment.TAG, "findEventUpcome: event is $event")
            }

            _isRefresing.value = false
            _isReload.value = false
        }
    }

    fun searchEvent(query: String){
        val searchText = query.lowercase()
        val filteredData = resultEventItemFinished.value?.data?.filter {
            it?.name?.lowercase()?.contains(searchText) == true
        }

        _listSearchResult.value =
            if (searchText.isBlank()){
                Resource.Success(data = emptyList())
            }else{
                if (!filteredData.isNullOrEmpty()){
                    Resource.Success(
                        filteredData
                    )
                } else{
                    Resource.Empty(data = emptyList())
                }
            }

    }

    // Favorit
    fun onFavoritClicked(favorit: EventEntity, isBookmarked: Boolean, createAt: Long) {
        FavoritHelper.togleFavorit(viewModelScope, repository, favorit, isBookmarked, createAt)
    }

    fun startReload(){
        _isReload.value = true
    }

    fun startRefreshing(){
        _isRefresing.value = true
    }

    fun markFinishedSuccess(){
        isFinishedSuccess = true
    }

}