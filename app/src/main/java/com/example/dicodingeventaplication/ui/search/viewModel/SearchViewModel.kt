package com.example.dicodingeventaplication.ui.search.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.dicodingeventaplication.R
import com.example.dicodingeventaplication.Resource
import com.example.dicodingeventaplication.data.respons.EventItem
import com.example.dicodingeventaplication.ui.search.SearchItem
import com.example.dicodingeventaplication.ui.search.SearchRepository

class SearchViewModel(private val repository: SearchRepository) : ViewModel() {
//    private var cacheResult: List<EventItem>? = null
//    private var lastQuery: String? = null

    private val _searchResultEvenItem = MutableLiveData<Resource<List<EventItem>>>()
    val searchResultEventItem: LiveData<Resource<List<EventItem>>> = _searchResultEvenItem

    private val _listHistory = MutableLiveData<List<EventItem>>()
    val listhHistory: LiveData<List<EventItem>> get() = _listHistory

    private val _selectButton = MutableLiveData<Int?>() // menyimpan id tombol yang di pilih
    val selectButton: LiveData<Int?> get() = _selectButton

    private val _activeQuery = MutableLiveData<Int>().apply { value = -1 } // default
    val activeQuery: LiveData<Int> get() =  _activeQuery

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        loadSearchHistory()
    }

    // ambil historu dari repository
    fun loadSearchHistory(){
        _listHistory.value = repository.getSearchHistory()
        Log.d(TAG, "loadSearchHistory: live data, size ${repository.getSearchHistory().size}")

//        val historyId = repository.getSearchHistory()
//        fetchItemFromApi(historyId)
    }

    // simulasikan pengambilan data dari api berdasarkan id
//    private fun fetchItemFromApi(id: List<Int>){
//        val historyList = id.map { ids ->
//            SearchItem.History(ids, "item $ids") // misal ambil nama dari id
//        }
//        _listHistory.value = listOf(SearchItem.History(id"Riwayta Pencarian")) + historyList
//    }

    // tembahkan pencarian baru ke history
    fun saveToHistory(eventItem: EventItem){
        repository.saveSearchHistory(eventItem)
        loadSearchHistory() // update ui
    }

    // hapus satu item dari history
    fun removeFromHistory(eventItem: EventItem){//item: SearchItem.History
        val historyList = repository.getSearchHistory().toMutableList()
        historyList.remove(eventItem)
//        historyList.toList()
//        repository.saveSearchHistory(historyList)
        loadSearchHistory()
    }

    // hapus semua history
    fun clearHistory(){
        repository.clearHistory()
        _listHistory.value = emptyList()
//        loadSearchHistory()
    }

    fun searchEvent(query: String, active: Int){
        if (query.isBlank()){
            _searchResultEvenItem.value = Resource.Success(emptyList()) // kosongkan hasil pencarian
            // panggile eror data tidak ditemukan
            return
        }

        // jika query samadengan sebelumnya dan sudah ada di chace, maka pakai
//        if (query == lastQuery && cacheResult != null){
//            _searchResultEvenItem.value = Resource.Success(cacheResult!!)
//            return
//        }

//        lastQuery = query
//        _searchResultEvenItem.value = Resource.Loading()

        repository.searchEvent(query, active) { result ->
//            if (result is Resource.Success ){
//                cacheResult = result.data
//            }
//            _searchResultEvenItem.postValue(result)
            _searchResultEvenItem.value = result
        }


//        val clientEventSearch = ApiConfig.getApiService().searchEvent(FINISHED, query)
//        clientEventSearch.enqueue(object : Callback<EventResponse> {
//            override fun onResponse(call: Call<EventResponse>, response: Response<EventResponse>) {
//                if (response.isSuccessful){
//                    val responsBody = response.body()
//                    if(responsBody != null){
//                        _searchEvenItem.value = responsBody.listEvents
//                        Log.e(TAG, "onResponse: onsucces ${response.message()}")
//                    }else{
//                        _searchEvenItem.value = emptyList()
//                    }
//                }
//                else{
//                    Log.e(TAG, "onResponse: onfailure ${response.message()}")
//                    _searchEvenItem.value = emptyList()
//                }
//            }
//
//            override fun onFailure(call: Call<EventResponse>, t: Throwable) {
//                Log.e(TAG, "onResponse: onfailure ${t.message}")
//            }
//        })

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