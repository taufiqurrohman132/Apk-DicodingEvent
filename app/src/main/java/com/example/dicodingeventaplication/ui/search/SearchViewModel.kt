package com.example.dicodingeventaplication.ui.search

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.viewModelScope
import com.example.dicodingeventaplication.R
import com.example.dicodingeventaplication.Resource
import com.example.dicodingeventaplication.data.respons.EventItem
import com.example.dicodingeventaplication.data.repository.DicodingEventRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class SearchViewModel(private val repository: DicodingEventRepository) : ViewModel() {
//    private var cacheResult: List<EventItem>? = null
//    private var lastQuery: String? = null

    private val _searchResultEvenItem = MutableStateFlow<Resource<List<EventItem>>>(Resource.Success(
        emptyList()
    ))
    val searchResultEventItem: StateFlow<Resource<List<EventItem>>> = _searchResultEvenItem

    private val _listHistory = MutableLiveData<List<EventItem>>()
    val listhHistory: LiveData<List<EventItem>> get() = _listHistory

    private val _selectButton = MutableLiveData<Int?>() // menyimpan id tombol yang di pilih
    val selectButton: LiveData<Int?> get() = _selectButton

    private val _activeQuery = MutableLiveData<Int>().apply { value = -1 } // default
    val activeQuery: LiveData<Int> get() =  _activeQuery

    private var job: Job? = null

    init {
        loadSearchHistory()
    }

    // ambil historu dari repository
    fun loadSearchHistory(){
        _listHistory.value = repository.getSearchHistory()
        Log.d(TAG, "loadSearchHistory: live data, size ${repository.getSearchHistory().size}")
    }

    // tembahkan pencarian baru ke history
    fun saveToHistory(eventItem: EventItem){
        repository.saveSearchHistory(eventItem)
        loadSearchHistory() // update ui
    }

    // hapus satu item dari history
    fun removeFromHistory(eventItem: EventItem){//item: SearchItem.History
        val historyList = repository.getSearchHistory().toMutableList()
        historyList.remove(eventItem)
        loadSearchHistory()
    }

    // hapus semua history
    fun clearHistory(){
        repository.clearHistory()
        _listHistory.value = emptyList()
    }

    fun searchEvent(query: String, active: Int){
        job?.cancel() // batalkan proses sebwelum nyua jika ada

        if (query.isBlank()){
            _searchResultEvenItem.value = Resource.Success(emptyList()) // kosongkan hasil pencarian
            return
        }

        _searchResultEvenItem.value = Resource.Loading()

        job = viewModelScope.launch {
            delay(500)

            if (!isActive || query.isBlank()) return@launch // tidak lanjut jika job dibatalkan
            repository.searchEvent(query, active) { result ->
                _searchResultEvenItem.value = result
            }
        }
    }

    fun selectButton(buttonId: Int){
        _selectButton.value = buttonId
        _activeQuery.value = when(buttonId){
            R.id.btn_state_finish -> FINISHED
            R.id.btn_state_upcone -> UPCOMING
            else -> ALL
        }
    }

    companion object{
        const val TAG = "searchvm"
        private const val SEARCH_HISTORY = "search_history"
        private const val HISTORY_LIST = "history_list"
        private const val FINISHED = 0
        private const val UPCOMING = 1
        private const val ALL = -1
    }
}