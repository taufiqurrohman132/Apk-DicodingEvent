package com.example.dicodingeventaplication.ui.search

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import com.example.dicodingeventaplication.R
import com.example.dicodingeventaplication.Resource
import com.example.dicodingeventaplication.data.respons.EventItem
import com.example.dicodingeventaplication.data.respons.EventResponse
import com.example.dicodingeventaplication.data.retrofit.ApiService
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okio.IOException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchRepository(private val apiService: ApiService, private val context: Context){
    private val sharedPref = context.getSharedPreferences(SEARCH_HISTORY, Context.MODE_PRIVATE)
    private val gson = Gson()

    // ambil history dari shered
    fun getSearchHistory(): List<EventItem> {
        val json = sharedPref.getString(HISTORY_LIST, "[]") ?: "[]"// default empty list
        Log.d(TAG, "getSearchHistory: $json")
        val type = object : TypeToken<List<EventItem>>() {}.type
        return gson.fromJson(json, type)
    }

    // simpan id ke shered / dimpan ke history
    fun saveSearchHistory(eventItem: EventItem){
        val historyList = getSearchHistory().toMutableList()
        historyList.removeAll { it.id == eventItem.id  } // hindsari duplikasi
        historyList.add(0, eventItem) // tambahkan ke awal list
        if (historyList.size > 15) {// batas max item
            historyList.dropLast(1) // hapus elemen terahir
        }

        val json = gson.toJson(historyList)
        sharedPref.edit().putString(HISTORY_LIST, json).apply()
        Log.d(TAG, "saveSearchHistory: $json")
    }

    fun clearHistory(){
        sharedPref.edit().remove(HISTORY_LIST).apply()
    }
//
//    // tambahkan id ke history
//    fun addToHistory(id: Int){
//        val historyList = getSearchHistory().toMutableList()
//        historyList.remove(id) // hindsari duplikasi
//        historyList.add(0, id) // tambahkan ke awal list
//        if (historyList.size > 15) {// batas max item
//            historyList.dropLast(1) // hapus elemen terahir
//        }
//        saveSearchHistory(historyList)
//    }

    // mencari event
    fun searchEvent(query: String, active: Int, callback: (Resource<List<EventItem>>) -> Unit) {
        callback(Resource.Loading()) // tampilkan loding dulu

//        val clientEventSearch = ApiConfig.getApiService().searchEvent(FINISHED, query)
        apiService.searchEvent(active, query).enqueue(object : Callback<EventResponse> {
            override fun onResponse(call: Call<EventResponse>, response: Response<EventResponse>) {
                if (response.isSuccessful) {
                    val responsBody = response.body()
                    if (responsBody != null) {
//                        _listEvenItem.value = responsBody.listEvents
                        callback(Resource.Success(responsBody.listEvents.take(8)))
                        Log.e(TAG, "onResponse: onsucces ${response.message()}")
                    } else {
                        callback(Resource.Empty(emptyList())) // data kosong
//                        _listEvenItem.value = emptyList()
                        Log.e(TAG, "onResponse: onsucces ${response.message()}")
                        Log.e(TAG, "onResponse: onsucces data null")
                    }
                } else {
                    Log.e(TAG, "onResponse: onfailure ${response.message()}")
//                    _listEvenItem.value = emptyList()
                    val erroMessage = when(response.code()){
                        400 -> context.resources.getString(R.string.error_400)
                        403 -> context.resources.getString(R.string.error_403)
                        404 -> context.resources.getString(R.string.error_404)
                        408 -> context.resources.getString(R.string.error_408)
                        500 -> context.resources.getString(R.string.error_500)
                        503 -> context.resources.getString(R.string.error_503)
                        else -> "Terjadi kesalahan (${response.code()})"
                    }
                    callback(Resource.Error(erroMessage))
                }
            }

            override fun onFailure(call: Call<EventResponse>, t: Throwable) {
                Log.e(TAG, "onResponse: onfailure ${t.message}")
                if (t is IOException){
                    callback(Resource.Error(context.resources.getString(R.string.error_koneksi)))
                }else{
                    callback(Resource.Error(context.resources.getString(R.string.error_takterduga)))
                }
            }
        })
    }


    companion object{
        const val TAG = "srepo"
        private const val SEARCH_HISTORY = "search_history"
        private const val HISTORY_LIST = "history_list"
        private const val FINISHED = 0
        private const val UPCOMING = 1
        private const val ALL = -1
    }
}