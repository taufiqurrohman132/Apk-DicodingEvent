package com.example.dicodingeventaplication.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dicodingeventaplication.data.local.entity.EventEntity
import com.example.dicodingeventaplication.utils.Resource
import com.example.dicodingeventaplication.data.repository.DicodingEventRepository
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

    private val _resultEvenItemFinished = MediatorLiveData<Resource<List<EventEntity?>>?>()
    val resultEventItemFinished: LiveData<Resource<List<EventEntity?>>?> = _resultEvenItemFinished

    private val _resultEvenItemUpcome = MediatorLiveData<Resource<List<EventEntity?>>?>()
    val resultEventItemUpcome: LiveData<Resource<List<EventEntity?>>?> = _resultEvenItemUpcome

    private val _headerEvent = MediatorLiveData<Resource<List<EventEntity?>>?>()
    val headerEvent: LiveData<Resource<List<EventEntity?>>?> = _headerEvent

    private val _hasLocalDataFinish = MutableStateFlow(false)
    val hasLocalDataFinish: StateFlow<Boolean> = _hasLocalDataFinish.asStateFlow()

    private val _hasLocalDataUpcome = MutableStateFlow(false)
    val hasLocalDataUpcome: StateFlow<Boolean> = _hasLocalDataUpcome.asStateFlow()

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

        // Pantau status data lokal dari DataStore
        DataStatehelper.getHasLocaFinishedlState()
            .onEach {
                _hasLocalDataFinish.value = it
            }.launchIn(viewModelScope)

        DataStatehelper.getHasLocaUpcomelState()
            .onEach {
                _hasLocalDataUpcome.value = it
            }.launchIn(viewModelScope)


    }

    fun findImageHeader(){ //callback: (() -> Unit)? = null
        job?.cancel()

        Log.d(TAG, "findImageHeader dipanggil")

        job = viewModelScope.launch {
//            delay(1000)
            Log.d(TAG, "findEvent heder berjalan di thread: ${Thread.currentThread().name}")

            val source = repository.findEvent(HomeFragment.FINISHED)
            _headerEvent.removeSource(source)
            _headerEvent.addSource(source){ event ->
                if (event is Resource.Error || event is Resource.ErrorConection){
                    _dialogNotifError.value = SingleEvent(event.message)
                } else if (event is Resource.Success)
                    viewModelScope.launch {
                        DataStatehelper.setHasLocalDataFinished(true)
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
//            delay(500)
            Log.d(TAG, "findEvent finish berjalan di thread: ${Thread.currentThread().name}")

            val source = repository.findEvent(HomeFragment.FINISHED)
            _resultEvenItemFinished.removeSource(source)
            _resultEvenItemFinished.addSource(source){ event ->
                if (event is Resource.Success)
                    viewModelScope.launch {
                        DataStatehelper.setHasLocalDataFinished(true)
                    }
                _resultEvenItemFinished.value = event
            }

        }

    fun findEventUpcome(){
        viewModelScope.launch {
//            delay(500)
            Log.d(TAG, "findEvent upcome berjalan di thread: ${Thread.currentThread().name}")

            val source = repository.findEvent(HomeFragment.UPCOMING)
            _resultEvenItemUpcome.removeSource(source)
            _resultEvenItemUpcome.addSource(source){ event ->
                _resultEvenItemUpcome.value = when(event){
                    is Resource.Success -> {
                        val itemFromApi = event.data ?: emptyList()
                        if (itemFromApi.size in 1..4){
                            viewModelScope.launch {
                                DataStatehelper.setHasLocalDataUpcome(true)
                            }
                            Resource.Success(event.data!! + listOf(null))// jika kosong, tambahkan list kosong
                        } else {
                            Log.w(TAG, "findEventUpcome: is 5 event")
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
            findEventFinished()
        }
    }

    // Favorit
    fun onFavoritClicked(favorit: EventEntity, isBookmarked: Boolean, createAt: Long) {
        FavoritHelper.togleFavorit(viewModelScope, repository, favorit, isBookmarked, createAt)
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