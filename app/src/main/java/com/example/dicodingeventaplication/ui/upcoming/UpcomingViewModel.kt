package com.example.dicodingeventaplication.ui.upcoming

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.dicodingeventaplication.Resource
import com.example.dicodingeventaplication.data.repository.DicodingEventRepository
import com.example.dicodingeventaplication.data.respons.EventItem
import com.example.dicodingeventaplication.ui.home.HomeFragment

class UpcomingViewModel(private val repository: DicodingEventRepository) : ViewModel() {

    private val _resultEvenItemUpcome = MutableLiveData<Resource<List<EventItem>>>()
    val resultEventItemUpcome: LiveData<Resource<List<EventItem>>> = _resultEvenItemUpcome

//    private var hasMoreData = true
    private var isLoading = false

    init {
        findEventUpcome()
    }

    private fun findEventUpcome(){
        repository.findEvent(HomeFragment.FINISHED) { event ->

//            if(event is Resource.Success){
//                val eventItem = event.data ?: emptyList()
//
//                // tambahkan 3 shimmer
//                val shimmerItem = List(3) {UpcomingItem.Loading}
//
//            }else{
//                _resultEvenItemUpcome.value = event
//            }
                _resultEvenItemUpcome.value = event


//            // jika data kosong atau batas akhir
//            if (event is Resource.Success && (event.data?.size ?: 0) < resultEventItemUpcome.value?.data?.size){
//
//            }
        }
    }

//    fun findEventUpcomeWithLoading(eventResource: Resource<List<EventItem>>): List<UpcomingItem> {
//        return when(eventResource) {
//            is Resource.Success ->{
//                val eventItem = eventResource.data?.map { UpcomingItem.ResultItem(it) } ?: emptyList()
//                eventItem// + UpcomingItem.Loading
//            }
//            is Resource.Loading ->{
//                val eventItem = eventResource.data?.map { UpcomingItem.ResultItem(it) } ?: emptyList()
//                eventItem + UpcomingItem.Loading
////                listOf(UpcomingItem.Loading)
//            }
//            is Resource.Error -> {
//                emptyList()
//            }
//            is Resource.Empty -> {
//                emptyList()
//            }
//        }
//    }

//    fun removeLoading(){
//        val currentList = _resultEvenItemUpcome.value?.data?.toMutableList() ?: mutableListOf()
//        if (currentList.isNotEmpty() && currentList?.last() is UpcomingItem.Loading){
//            currentList.removeAt(currentList.size -1)
//            _resultEvenItemUpcome.value = currentList
//        }
//    }

}