package com.example.dicodingeventaplication.ui.finished

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
import com.example.dicodingeventaplication.utils.FavoritHelper
import com.example.dicodingeventaplication.utils.SingleEvent
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class FinishedViewModel(private val repository: DicodingEventRepository) : ViewModel() {

    private val _resultEvenItemFinished = MediatorLiveData<Resource<List<FavoritEvent?>>?>()
    val resultEventItemFinished: LiveData<Resource<List<FavoritEvent?>>?> = _resultEvenItemFinished

    private val _dialogNotifError = MutableLiveData<SingleEvent<String?>>()
    val dialogNotifError: LiveData<SingleEvent<String?>> = _dialogNotifError

    private val _listSearchResult = MutableLiveData<Resource<List<FavoritEvent?>?>>()
    val listSearchResult: LiveData<Resource<List<FavoritEvent?>?>> get() = _listSearchResult

    private val _isReload = MutableLiveData(false)
    val isReload: LiveData<Boolean> = _isReload

    private val _isRefresing = MutableLiveData(false)
    val isRefresing: LiveData<Boolean> = _isRefresing

    var isSearching = false

    var isCollapse = false

    var isFinishedSuccess = false

    init {
        findEventFinished()
    }

    fun findEventFinished(){
        viewModelScope.launch {
            delay(500)

//            repository.findEvent(HomeFragment.FINISHED)
//            { event ->
//                _resultEvenItemFinished.value = when(event){
//                    is Resource.Error -> {
//                        _dialogNotifError.value = SingleEvent(event.message)
//                        event
//                    }
//                    is Resource.ErrorConection -> {
//                        _dialogNotifError.value = SingleEvent(event.message)
//                        event
//                    }
//                    else -> event
//                }
//            }
            val sourse = repository.findEvent(HomeFragment.FINISHED)
            _resultEvenItemFinished.removeSource(sourse)
            _resultEvenItemFinished.addSource(sourse) { event ->
                if (event is Resource.Error || event is Resource.ErrorConection){
                    _dialogNotifError.value = SingleEvent(event.message)
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
    fun onFavoritClicked(favorit: FavoritEvent, isBookmarked: Boolean) {
        FavoritHelper.togleFavorit(viewModelScope, repository, favorit, isBookmarked)
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