package com.example.dicodingeventaplication.ui.detailEvent

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dicodingeventaplication.data.repository.DicodingEventRepository
import com.example.dicodingeventaplication.data.respons.Event
import com.example.dicodingeventaplication.utils.Resource
import com.example.dicodingeventaplication.utils.SingleEvent
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class DetailEventViewModel(private val repository: DicodingEventRepository, id: Int?) : ViewModel() {
    private val _listEventData = MutableLiveData<Resource<Event?>>()
    val listEventData: LiveData<Resource<Event?>> = _listEventData

    private val _dialogNotifError = MutableLiveData<SingleEvent<String?>>()
    val dialogNotifError: LiveData<SingleEvent<String?>> = _dialogNotifError

    private val _snackBarEmpty = MutableLiveData<SingleEvent<String?>>()
    val snackbarEmpty: LiveData<SingleEvent<String?>> = _snackBarEmpty

    private val _isRefresing = MutableLiveData(false)
    val isRefresing: LiveData<Boolean> = _isRefresing

    var isDetailSuccess = false

    init {
        findDetailEvent(id)
    }

    fun findDetailEvent(id: Int?){
        viewModelScope.launch {
            delay(500)

            repository.findDetailEvent(id){ eventData ->
                _listEventData.value = when(eventData){
                    is Resource.Error -> {
                        _dialogNotifError.value = SingleEvent(eventData.message)
                        eventData
                    }
                    is Resource.ErrorConection -> {
                        _dialogNotifError.value = SingleEvent(eventData.message)
                        eventData
                    }
                    is Resource.Empty ->{
                        _snackBarEmpty.value = SingleEvent(eventData.message)
                        eventData
                    }
                    else ->{eventData}
                }
                _listEventData.value = eventData
            }

            _isRefresing.value = false
        }
    }

    fun markUpcomingSuccess(){
        isDetailSuccess = true
    }

    fun startRefreshing(){
        _isRefresing.value = true
    }

//    fun findDetailEvent(id: Int){
//        val clientEventDetail = ApiConfig.getApiService().getEventDetail(id)
//        clientEventDetail.enqueue(object : Callback<DetailEventResponse> { // enqueue otomatis berjalan di bg treaad
//            override fun onResponse(
//                call: Call<DetailEventResponse>,
//                response: Response<DetailEventResponse>
//            ) {
//                if (response.isSuccessful){
//                    val responsBody = response.body()
//                    if (responsBody != null){
//                        _listEventData.value = responsBody
//                    }
//                    Log.d("res", "onResponse: succes $responsBody")
//                }else{
//                    Log.e("res", "onResponse: onfailure ${response.message()}")
//                }
//            }
//
//            override fun onFailure(call: Call<DetailEventResponse>, t: Throwable) {
//                Log.e("res", "onFailure: ${t.message}")
//            }
//        })
//    }


}