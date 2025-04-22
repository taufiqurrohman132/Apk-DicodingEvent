package com.example.dicodingeventaplication.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dicodingeventaplication.data.local.entity.FavoritEvent
import com.example.dicodingeventaplication.utils.Resource
import com.example.dicodingeventaplication.data.repository.DicodingEventRepository
import com.example.dicodingeventaplication.data.remote.model.EventItem
import com.example.dicodingeventaplication.ui.home.HomeFragment.Companion.TAG
import com.example.dicodingeventaplication.utils.DataStatehelper
import com.example.dicodingeventaplication.utils.FavoritHelper
import com.example.dicodingeventaplication.utils.SingleEvent
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: DicodingEventRepository) : ViewModel() {

    private val _resultEvenItemFinished = MediatorLiveData<Resource<List<FavoritEvent?>>?>()
    val resultEventItemFinished: LiveData<Resource<List<FavoritEvent?>>?> = _resultEvenItemFinished

    private val _resultEvenItemUpcome = MediatorLiveData<Resource<List<FavoritEvent?>>?>()
    val resultEventItemUpcome: LiveData<Resource<List<FavoritEvent?>>?> = _resultEvenItemUpcome

    private val _headerEvent = MediatorLiveData<Resource<List<FavoritEvent?>>?>()
    val headerEvent: LiveData<Resource<List<FavoritEvent?>>?> = _headerEvent

    private val _hasLocalData = MutableStateFlow(false)
    val hasLocalData: StateFlow<Boolean> = _hasLocalData.asStateFlow()

    private val _dialogNotifError = MutableLiveData<SingleEvent<String?>>()
    val dialogNotifError: LiveData<SingleEvent<String?>> = _dialogNotifError

    private var _isReload = MutableLiveData(false)
    val isReload: LiveData<Boolean> = _isReload

    private var _isRefreshing = MutableLiveData(false)
    val isRefreshing: LiveData<Boolean> = _isRefreshing

    var isUpcomingSuccess = false
    var isFinishedSuccess = false
    var isHeaderSuccess = false

    private var isHasLocalData = false

    private var job: Job? = null

    var scrollY: Int= 0

    init {
        findImageHeader()

        // Pantau status data lokal dari DataStore
        DataStatehelper.getHasLocalState()
            .onEach {
                _hasLocalData.value = it
            }.launchIn(viewModelScope)
    }

    fun findImageHeader(){ //callback: (() -> Unit)? = null
        job?.cancel()

        Log.d(TAG, "findImageHeader dipanggil")

        job = viewModelScope.launch {
            delay(1000)
            Log.d(TAG, "findEvent heder berjalan di thread: ${Thread.currentThread().name}")

//            repository.findEvent(HomeFragment.FINISHED) { event ->
//                _headerEvent.value = when(event){
//                    is Resource.ErrorConection -> {
//                        _dialogNotifError.value = SingleEvent(event.message)
//                        event
//                    }
//                    is Resource.Error ->{
//                        _dialogNotifError.value = SingleEvent(event.message)
//                        event
//                    }
//                    else -> event
//                }
//                _isRefreshing.value = false
//                _isReload.value = false
//            }
            val source = repository.findEvent(HomeFragment.FINISHED)
            _headerEvent.removeSource(source)
            _headerEvent.addSource(source){ event ->
                if (event is Resource.Error || event is Resource.ErrorConection){
                    _dialogNotifError.value = SingleEvent(event.message)
                }
                _headerEvent.value = event

                _isRefreshing.value = false
                _isReload.value = false
            }

            findEventUpcome()
        }
    }

    fun findEventFinished() =
        viewModelScope.launch {
            delay(500)
            Log.d(TAG, "findEvent finish berjalan di thread: ${Thread.currentThread().name}")

            val source = repository.findEvent(HomeFragment.FINISHED)
            _resultEvenItemFinished.removeSource(source)
            _resultEvenItemFinished.addSource(source){ event ->
                if (event is Resource.Success)
                    isHasLocalData = true
                _resultEvenItemFinished.value = event
            }
        //          { event ->
//                _resultEvenItemFinished.value = event
//            }
            if (isHasLocalData){
                DataStatehelper.setHasLocalData( true)
                isHasLocalData = false
            }

        }

    fun findEventUpcome(){
        viewModelScope.launch {
            delay(500)
            Log.d(TAG, "findEvent upcome berjalan di thread: ${Thread.currentThread().name}")

//            repository.findEvent(HomeFragment.UPCOMING) { event ->
//                _resultEvenItemUpcome.value= when(event) {
//                    is Resource.Success -> {
//                        val itemFromApi = event.data ?: emptyList()
//                        if (itemFromApi.size in 1..4){
//                            Resource.Success(event.data!! + listOf(null))// jika kosong, tambahkan list kosong
//                        } else {
//                            event
//                        }
//                    }
//                    is Resource.Error -> {
//                        event
//                    }
//                    is Resource.ErrorConection -> {
//                        event
//                    }
//                    is Resource.Empty -> {
//                        Resource.Success(listOf(null, null))// jika kosong, tambahkan list kosong
//                    }
//                    else -> event
//                }
//                Log.d(TAG, "viewmodel upcome: $event")
//            }
            val source = repository.findEvent(HomeFragment.UPCOMING)
            _resultEvenItemUpcome.removeSource(source)
            _resultEvenItemUpcome.addSource(source){ event ->
                _resultEvenItemUpcome.value = when(event){
                    is Resource.Success -> {
                        val itemFromApi = event.data ?: emptyList()
                        if (itemFromApi.size in 1..4){
                            isHasLocalData = true
                            Resource.Success(event.data!! + listOf(null))// jika kosong, tambahkan list kosong
                        } else {
                            Log.w(TAG, "findEventUpcome: is 5 event", )
                            event
                        }
                    }
                    is Resource.Empty ->{
                        Resource.Success(listOf(null, null))// jika kosong, tambahkan list kosong
                    }
                    else -> event
                }
                Log.d(TAG, "viewmodel upcome: $event")
            }
            if (isHasLocalData){
                DataStatehelper.setHasLocalData( true)
                isHasLocalData = false
            }
            findEventFinished()
        }
    }

    // Favorit
    fun onFavoritClicked(favorit: FavoritEvent, isBookmarked: Boolean) {
        FavoritHelper.togleFavorit(viewModelScope, repository, favorit, isBookmarked)
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