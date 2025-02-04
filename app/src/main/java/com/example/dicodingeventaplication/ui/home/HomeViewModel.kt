package com.example.dicodingeventaplication.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.dicodingeventaplication.Resource
import com.example.dicodingeventaplication.data.repository.DicodingEventRepository
import com.example.dicodingeventaplication.data.respons.EventItem

class HomeViewModel(private val repository: DicodingEventRepository) : ViewModel() {
//
//    private val _text = MutableLiveData<String>().apply {
//        value = "This is home Fragment"
//    }
//    val text: LiveData<String> = _text

    private val _resultEvenItemFinished = MutableLiveData<Resource<List<EventItem>>>()
    val resultEventItemFinished: LiveData<Resource<List<EventItem>>> = _resultEvenItemFinished

    private val _resultEvenItemUpcome = MutableLiveData<Resource<List<EventItem>>>()
    val resultEventItemUpcome: LiveData<Resource<List<EventItem>>> = _resultEvenItemUpcome

//    private val _listEventData = MutableLiveData<List<EventItem>>()
//    val listEventData: LiveData<List<EventItem>> = _listEventData

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        findEventFinished()
        findEventUpcome()
    }

    fun findEventFinished(){
        repository.findEvent(HomeFragment.FINISHED) { event ->
            _resultEvenItemFinished.value = event
        }
    }

    fun findEventUpcome(){
        repository.findEvent(HomeFragment.UPCOMING) { event ->
            _resultEvenItemUpcome.value = event
        }
    }

    // mecari data dari api
//    private fun findEvent(){
//        // finished
//        val clientEventFinished = ApiConfig.getApiService().getEventActive(FINISHED)
//        clientEventFinished.enqueue(object : Callback<EventResponse> { // enqueue otomatis berjalan di bg treaad
//            override fun onResponse(call: Call<EventResponse>, response: Response<EventResponse>) {
//                if (response.isSuccessful){
//                    val responsBody = response.body()
//                    if (responsBody != null){
//                        val limitEvent = responsBody.listEvents.take(5)
//                        _listEventData.value = limitEvent
//                    }
//                }else{
//                    Log.e("res", "onResponse: onfailure ${response.message()}")
//                }
//            }
//
//            override fun onFailure(call: Call<EventResponse>, t: Throwable) {
//                Log.e("res", "onFailure: ${t.message}")
//            }
//        })
//
//        // upcoming
//        val clientEventUpcoming = ApiConfig.getApiService().getEventActive(FINISHED)
//        clientEventUpcoming.enqueue(object : Callback<EventResponse> {
//            override fun onResponse(call: Call<EventResponse>, response: Response<EventResponse>) {
//                if (response.isSuccessful){
//                    val responsBody = response.body()
//                    if (responsBody != null){
//                        val limitEvent = responsBody.listEvents.take(5)
//                        _listEventData.value = limitEvent
//                    }
//                }else{
//                    Log.e("res", "onResponse: onfailure ${response.message()}")
//                }
//            }
//
//            override fun onFailure(call: Call<EventResponse>, t: Throwable) {
//                Log.e("res", "onFailure: ${t.message}")
//            }
//
//        })
//    }

    companion object{
        private const val FINISHED = 0
        private const val UPCOMING = 1
    }
}