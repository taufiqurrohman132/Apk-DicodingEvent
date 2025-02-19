package com.example.dicodingeventaplication.data.repository

import android.content.Context
import android.util.Log
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

class DicodingEventRepository(
    private val apiService: ApiService,
    private val context: Context
) {
    private val sharedPref = context.getSharedPreferences(SEARCH_HISTORY, Context.MODE_PRIVATE)
    private val gson = Gson()

    // variable chache
    private var  cacheDataUpcoming: EventResponse? = null
    private var  cacheDataFinished: EventResponse? = null
    private var  cacheDataSearching: EventResponse? = null

    private var querySearch = ""
    private var isActive = -1

    // ambil history dari shered
    fun getSearchHistory(): List<EventItem> {
        val json = sharedPref.getString(HISTORY_LIST, "[]") ?: "[]"// default empty list
        Log.d(TAG, "getSearchHistory: $json")
        val type = object : TypeToken<List<EventItem>>() {}.type
        return gson.fromJson(json, type)
    }

    // simpan id ke shered / dimpan ke history
    fun saveSearchHistory(eventItem: EventItem) {
        val historyList = getSearchHistory().toMutableList()
        historyList.removeAll { it.id == eventItem.id } // hindsari duplikasi
        historyList.add(0, eventItem) // tambahkan ke awal list
        if (historyList.size > 15) {// batas max item
            historyList.dropLast(1) // hapus elemen terahir
        }

        val json = gson.toJson(historyList)
        sharedPref.edit().putString(HISTORY_LIST, json).apply()
        Log.d(TAG, "saveSearchHistory: $json")
    }

    // bersihkan history
    fun clearHistory() {
        sharedPref.edit().remove(HISTORY_LIST).apply()
    }

    fun removeItemHistory(eventItem: EventItem){
        val historyList = getSearchHistory().toMutableList()
        historyList.removeAll { it.id == eventItem.id }

        val json = gson.toJson(historyList)
        sharedPref.edit().putString(HISTORY_LIST, json).apply()
        Log.d(TAG, "remoseSearchHistory: $json")
    }

    // mencari event
    fun searchEvent(query: String, active: Int, callback: (Resource<List<EventItem>>) -> Unit) {
        callback(Resource.Loading()) // tampilkan loding dulu

        apiService.searchEvent(active, query).enqueue(object : Callback<EventResponse> {
            override fun onResponse(call: Call<EventResponse>, response: Response<EventResponse>) {
                if (response.isSuccessful) {
                    val responsBody = response.body()
                    querySearch = query

                    if (responsBody?.listEvents?.isNotEmpty() == true) {
                        callback(Resource.Success(responsBody.listEvents.take(8)))
                        cacheDataSearching = responsBody
                        isActive = active
                        Log.e(TAG, "onResponse: onsucces ${response.message()}")
                    } else {
                        callback(Resource.Empty(emptyList())) // data kosong
                        Log.e(TAG, "onResponse: onsucces ${response.message()}")
                        Log.e(TAG, "onResponse: onsucces data null")
                    }
                } else {
                    Log.e(TAG, "onResponse: onfailure ${response.message()}")
                    val erroMessage = errorHandling(response.code())
                    callback(Resource.Error(erroMessage))
                }
            }

            override fun onFailure(call: Call<EventResponse>, t: Throwable) {
                Log.e(TAG, "onResponse: onfailure ${t.message}")
                if (t is IOException) {
//                    callback(Resource.ErrorConection(context.resources.getString(R.string.error_koneksi)))
                    if (cacheDataSearching != null && querySearch == query && isActive == active)
                        callback(Resource.Success(cacheDataSearching?.listEvents?.take(8) ?: emptyList()))
                    else
                        callback(Resource.ErrorConection(context.resources.getString(R.string.error_koneksi)))
                } else {
                    callback(Resource.Error(context.resources.getString(R.string.error_takterduga)))
                }
            }
        })
    }

    fun findEvent(active: Int, callback: (Resource<List<EventItem?>>) -> Unit) {
        callback(Resource.Loading())

        apiService.getEventActive(active).enqueue(object : Callback<EventResponse> { // enqueue otomatis berjalan di bg treaad
            override fun onResponse(
                call: Call<EventResponse>,
                response: Response<EventResponse>
            ) {
                if (response.isSuccessful) {
                    val responsBody = response.body()
                    if (responsBody?.listEvents?.isNotEmpty() == true) {
                        when(active){
                            FINISHED -> cacheDataFinished = responsBody// masukkan chace
                            UPCOMING -> cacheDataUpcoming = responsBody
                        }
                        val limitEvent = responsBody.listEvents//.take(5)
                        callback(Resource.Success(limitEvent))
                    } else {
                        Log.e(TAG, "onResponse: data null}")
                        callback(Resource.Empty(List<EventItem?>(2) {null})) // data kosong
                    }
                } else {
                    Log.e(TAG, "onResponse: onfailure ${response.message()}")
                    val erroMessage = errorHandling(response.code())
                    when(active){
                        FINISHED -> callback(Resource.Error(erroMessage, List<EventItem?>(5) {null}))
                        UPCOMING -> callback(Resource.Error(erroMessage))
                    }
                }
            }

            override fun onFailure(call: Call<EventResponse>, t: Throwable) {
                Log.e(TAG, "onResponse: onfailure ${t.message}")
                if (t is IOException) {
                    callback(Resource.ErrorConection(context.resources.getString(R.string.error_koneksi),  List<EventItem?>(5) {null}))
                    if (cacheDataUpcoming != null || cacheDataFinished != null) {
                        when(active){
                            UPCOMING -> {
                                if (cacheDataUpcoming != null)
                                    callback(Resource.Success(cacheDataUpcoming?.listEvents ?: emptyList()))
                                else
                                    callback(Resource.Empty(List<EventItem?>(2) {null}))
                            }
                            FINISHED -> {
                                callback(Resource.Success(cacheDataFinished?.listEvents ?: emptyList()))
                            }
                        }
                        Log.d(TAG, "onFailure cace data: tes")
                    }

                } else {
                    when(active){
                        FINISHED -> callback(Resource.Error(context.resources.getString(R.string.error_takterduga), List<EventItem?>(5) {null}))
                        UPCOMING -> callback(Resource.Error(context.resources.getString(R.string.error_takterduga)))
                    }
                }
            }
        })
    }

    private fun errorHandling(code: Int): String {
        return when (code) {
            400 -> context.resources.getString(R.string.error_400)
            403 -> context.resources.getString(R.string.error_403)
            404 -> context.resources.getString(R.string.error_404)
            408 -> context.resources.getString(R.string.error_408)
            500 -> context.resources.getString(R.string.error_500)
            503 -> context.resources.getString(R.string.error_503)
            else -> "Terjadi kesalahan ($code)"
        }
    }

    companion object {
        const val TAG = "srepo"
        private const val SEARCH_HISTORY = "search_history"
        private const val HISTORY_LIST = "history_list"
        private const val FINISHED = 0
        private const val UPCOMING = 1
    }
}
