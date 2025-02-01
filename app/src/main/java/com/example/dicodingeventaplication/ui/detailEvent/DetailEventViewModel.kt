package com.example.dicodingeventaplication.ui.detailEvent

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.dicodingeventaplication.data.respons.DetailEventResponse
import com.example.dicodingeventaplication.data.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailEventViewModel : ViewModel() {
    private val _listEventData = MutableLiveData<DetailEventResponse?>()
    val listEventData: LiveData<DetailEventResponse?> = _listEventData

    fun findDetailEvent(id: Int){
        val clientEventDetail = ApiConfig.getApiService().getEventDetail(id)
        clientEventDetail.enqueue(object : Callback<DetailEventResponse> { // enqueue otomatis berjalan di bg treaad
            override fun onResponse(
                call: Call<DetailEventResponse>,
                response: Response<DetailEventResponse>
            ) {
                if (response.isSuccessful){
                    val responsBody = response.body()
                    if (responsBody != null){
                        _listEventData.value = responsBody
                    }
                    Log.d("res", "onResponse: succes $responsBody")
                }else{
                    Log.e("res", "onResponse: onfailure ${response.message()}")
                }
            }

            override fun onFailure(call: Call<DetailEventResponse>, t: Throwable) {
                Log.e("res", "onFailure: ${t.message}")
            }
        })
    }


}