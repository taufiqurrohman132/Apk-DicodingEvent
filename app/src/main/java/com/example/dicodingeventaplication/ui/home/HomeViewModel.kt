package com.example.dicodingeventaplication.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.dicodingeventaplication.data.respons.EventResponse
import com.example.dicodingeventaplication.data.respons.EventItem
import com.example.dicodingeventaplication.data.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeViewModel : ViewModel() {
//
//    private val _text = MutableLiveData<String>().apply {
//        value = "This is home Fragment"
//    }
//    val text: LiveData<String> = _text

    private val _listEventData = MutableLiveData<List<EventItem>>()
    val listEventData: LiveData<List<EventItem>> = _listEventData

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        findEvent()
    }

    // mecari data dari api
    private fun findEvent(){
        // finished
        val clientEventFinished = ApiConfig.getApiService().getEventActive(FINISHED)
        clientEventFinished.enqueue(object : Callback<EventResponse> { // enqueue otomatis berjalan di bg treaad
            override fun onResponse(call: Call<EventResponse>, response: Response<EventResponse>) {
                if (response.isSuccessful){
                    val responsBody = response.body()
                    if (responsBody != null){
                        val limitEvent = responsBody.listEvents.take(5)
                        _listEventData.value = limitEvent
                    }
                }else{
                    Log.e("res", "onResponse: onfailure ${response.message()}")
                }
            }

            override fun onFailure(call: Call<EventResponse>, t: Throwable) {
                Log.e("res", "onFailure: ${t.message}")
            }
        })

        // upcoming
        val clientEventUpcoming = ApiConfig.getApiService().getEventActive(FINISHED)
        clientEventUpcoming.enqueue(object : Callback<EventResponse> {
            override fun onResponse(call: Call<EventResponse>, response: Response<EventResponse>) {
                if (response.isSuccessful){
                    val responsBody = response.body()
                    if (responsBody != null){
                        val limitEvent = responsBody.listEvents.take(5)
                        _listEventData.value = limitEvent
                    }
                }else{
                    Log.e("res", "onResponse: onfailure ${response.message()}")
                }
            }

            override fun onFailure(call: Call<EventResponse>, t: Throwable) {
                Log.e("res", "onFailure: ${t.message}")
            }

        })
    }

    companion object{
        private const val FINISHED = 0
        private const val UPCOMING = 1
    }
}