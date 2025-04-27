package com.example.dicodingeventaplication.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import com.example.dicodingeventaplication.R
import com.example.dicodingeventaplication.data.local.dao.FavoritEventDao
import com.example.dicodingeventaplication.data.local.entity.FavoritEvent
import com.example.dicodingeventaplication.data.remote.model.DetailEventResponse
import com.example.dicodingeventaplication.data.remote.model.Event
import com.example.dicodingeventaplication.utils.Resource
import com.example.dicodingeventaplication.data.remote.model.EventItem
import com.example.dicodingeventaplication.data.remote.model.EventResponse
import com.example.dicodingeventaplication.data.remote.network.ApiService
//import com.example.dicodingeventaplication.utils.AppExecutors
//import com.example.dicodingeventaplication.utils.AppExecutors
import com.example.dicodingeventaplication.utils.ResourceProvider
import com.example.dicodingeventaplication.utils.SharedPrefHelper
import okio.IOException
import kotlin.concurrent.Volatile

class
DicodingEventRepository private constructor(
    private val resourceProvider: ResourceProvider,
    private val sharedPrefHelper: SharedPrefHelper,
    private val apiService: ApiService,
    private val favoritDao: FavoritEventDao,
//    private val appExecutors: AppExecutors
) {
//    private val sharedPref = context.getSharedPreferences(SEARCH_HISTORY, Context.MODE_PRIVATE)
//    private val gson = Gson()

    // variable chache
    private var  cacheDataSearching: EventResponse? = null
    private var  cacheDataDetail: DetailEventResponse? = null

//    private val apiService = ApiConfig.getApiService()

    private var querySearch = ""
    private var isActive = -1

    companion object{
        const val TAG = "srepo"
        const val SEARCH_HISTORY = "search_history"
        const val HISTORY_LIST = "history_list"
        private const val FINISHED = 0
        private const val UPCOMING = 1

        @Volatile
        private var instance: DicodingEventRepository? = null
        fun getInstance(
            resourceProvider: ResourceProvider,
            sharedPrefHelper: SharedPrefHelper,
            apiService: ApiService,
            favoritDao: FavoritEventDao,
//            appExecutors: AppExecutors
        ): DicodingEventRepository =
            instance ?: synchronized(this){
                instance ?: DicodingEventRepository(
                    resourceProvider,
                    sharedPrefHelper,
                    apiService,
                    favoritDao,
//                    appExecutors
                )
            }.also { instance = it }
    }

    // FAVORIT
    suspend fun setFavoritBookmark(favorit: FavoritEvent, bookmarkState: Boolean){
//        favorit.isBookmarked = bookmarkState
        val updateEvent = favorit.copy(isBookmarked = bookmarkState)
        favoritDao.updateFavorit(updateEvent)
    }

    fun getFavoritBookmark(): LiveData<List<FavoritEvent>> {
        return favoritDao.getBookmarkedEvent()
    }

    suspend fun getDetailFromSearch(eventItem: EventItem) =
        favoritDao.getById(eventItem.id)


    // ambil history dari shered
    fun getSearchHistory(): List<EventItem> =
        sharedPrefHelper.getSearchHistory()

    // simpan id ke shered / dimpan ke history
    fun saveSearchHistory(eventItem: EventItem) =
        sharedPrefHelper.saveSearchHistory(eventItem)

    // bersihkan history
    fun clearHistory() =
        sharedPrefHelper.clearHistory()

    fun removeItemHistory(eventItem: EventItem)=
        sharedPrefHelper.removeItemHistory(eventItem)

//    // ambil history dari shered
//    fun getSearchHistory(): List<EventItem> {
//        val json = sharedPref.getString(HISTORY_LIST, "[]") ?: "[]"// default empty list
//        Log.d(TAG, "getSearchHistory: $json")
//        val type = object : TypeToken<List<EventItem>>() {}.type
//        return gson.fromJson(json, type)
//    }
//
//    // simpan id ke shered / dimpan ke history
//    fun saveSearchHistory(eventItem: EventItem) {
//        val historyList = getSearchHistory().toMutableList()
//        historyList.removeAll { it.id == eventItem.id } // hindsari duplikasi
//        historyList.add(0, eventItem) // tambahkan ke awal list
//        if (historyList.size > 15) {// batas max item
//            historyList.dropLast(1) // hapus elemen terahir
//        }
//
//        val json = gson.toJson(historyList)
//        sharedPref.edit().putString(HISTORY_LIST, json).apply()
//        Log.d(TAG, "saveSearchHistory: $json")
//    }
//
//    // bersihkan history
//    fun clearHistory() {
//        sharedPref.edit().remove(HISTORY_LIST).apply()
//    }
//
//    fun removeItemHistory(eventItem: EventItem){
//        val historyList = getSearchHistory().toMutableList()
//        historyList.removeAll { it.id == eventItem.id }
//
//        val json = gson.toJson(historyList)
//        sharedPref.edit().putString(HISTORY_LIST, json).apply()
//        Log.d(TAG, "remoseSearchHistory: $json")
//    }

//     mencari event
//    fun searchEvent(query: String, active: Int, callback: (Resource<List<EventItem>>) -> Unit) {
//        callback(Resource.Loading()) // tampilkan loding dulu
//
//        apiService.searchEvent(active, query).enqueue(object : Callback<EventResponse> {
//            override fun onResponse(call: Call<EventResponse>, response: Response<EventResponse>) {
//                if (response.isSuccessful) {
//                    val responsBody = response.body()
//                    querySearch = query
//
//                    if (responsBody?.listEvents?.isNotEmpty() == true) {
//                        callback(Resource.Success(responsBody.listEvents.take(8)))
//                        cacheDataSearching = responsBody
//                        isActive = active
//                        Log.e(TAG, "onResponse: onsucces ${response.message()}")
//                    } else {
//                        callback(Resource.Empty(emptyList())) // data kosong
//                        cacheDataSearching = null // kembalikan ke null ketika empty
//
//                        Log.e(TAG, "onResponse: onsucces ${response.message()}")
//                        Log.e(TAG, "onResponse: onsucces data null")
//                    }
//                } else {
//                    Log.e(TAG, "onResponse: onfailure ${response.message()}")
//                    val erroMessage = errorHandling(response.code())
//                    callback(Resource.Error(erroMessage))
//                }
//            }
//
//            override fun onFailure(call: Call<EventResponse>, t: Throwable) {
//                Log.e(TAG, "onResponse: onfailure ${t.message}")
//                if (t is IOException) {
//                    if (querySearch == query && isActive == active) {
//                        if (cacheDataSearching != null)
//                            callback(Resource.Success(cacheDataSearching?.listEvents?.take(8) ?: emptyList()))
//                        else{
//                            callback(Resource.Empty(emptyList()))
//                        }
//                    }
//                    else
//                        callback(Resource.ErrorConection(resourceProvider.getString(R.string.error_koneksi)))
//                } else {
//                    callback(Resource.Error(resourceProvider.getString(R.string.error_takterduga)))
//                }
//            }
//        })
//    }


    suspend fun searchEvent(query: String, active: Int, callback: (Resource<List<EventItem>>) -> Unit) {
        callback(Resource.Loading()) // tampilkan loding dulu
        try {
            val response = apiService.searchEvent(active, query)
            if (response.isSuccessful){
                val responseBody = response.body()
                val event = responseBody?.listEvents
                querySearch = query

                if (!event.isNullOrEmpty())  {
                    isActive = active
                    callback(Resource.Success(responseBody.listEvents.take(8)))
                    cacheDataSearching = responseBody
                    Log.e(TAG, "onResponse: onsucces ${response.message()}")
                }else{
                    callback(Resource.Empty(emptyList())) // data kosong
                    cacheDataSearching = null // kembalikan ke null ketika empty

                    Log.e(TAG, "onResponse: onsucces ${response.message()}")
                    Log.e(TAG, "onResponse: onsucces data null")
                }

            }else {
                Log.e(TAG, "onResponse: onfailure ${response.message()}")
                val erroMessage = errorHandling(response.code())
                callback(Resource.Error(erroMessage))
            }
        } catch (e: IOException){
            if (querySearch == query && isActive == active) {
                if (cacheDataSearching != null)
                    callback(Resource.Success(cacheDataSearching?.listEvents?.take(8) ?: emptyList()))
                else{
                    callback(Resource.Empty(emptyList()))
                }
            }
            else
                callback(Resource.ErrorConection(resourceProvider.getString(R.string.error_koneksi)))
        } catch (e: Exception){
            callback(Resource.Error(resourceProvider.getString(R.string.error_takterduga)))
            Log.d(TAG, "searchEvent: respoms $e")
        }
//        apiService.searchEvent(active, query).enqueue(object : Callback<EventResponse> {
//            override fun onResponse(call: Call<EventResponse>, response: Response<EventResponse>) {
//                if (response.isSuccessful) {
//                    val responsBody = response.body()
//                    querySearch = query
//
//                    if (responsBody?.listEvents?.isNotEmpty() == true) {
//                        callback(Resource.Success(responsBody.listEvents.take(8)))
//                        cacheDataSearching = responsBody
//                        isActive = active
//                        Log.e(TAG, "onResponse: onsucces ${response.message()}")
//                    } else {
//                        callback(Resource.Empty(emptyList())) // data kosong
//                        cacheDataSearching = null // kembalikan ke null ketika empty
//
//                        Log.e(TAG, "onResponse: onsucces ${response.message()}")
//                        Log.e(TAG, "onResponse: onsucces data null")
//                    }
//                } else {
//                    Log.e(TAG, "onResponse: onfailure ${response.message()}")
//                    val erroMessage = errorHandling(response.code())
//                    callback(Resource.Error(erroMessage))
//                }
//            }
//
//            override fun onFailure(call: Call<EventResponse>, t: Throwable) {
//                Log.e(TAG, "onResponse: onfailure ${t.message}")
//                if (t is IOException) {
//                    if (querySearch == query && isActive == active) {
//                        if (cacheDataSearching != null)
//                            callback(Resource.Success(cacheDataSearching?.listEvents?.take(8) ?: emptyList()))
//                        else{
//                            callback(Resource.Empty(emptyList()))
//                        }
//                    }
//                    else
//                        callback(Resource.ErrorConection(resourceProvider.getString(R.string.error_koneksi)))
//                } else {
//                    callback(Resource.Error(resourceProvider.getString(R.string.error_takterduga)))
//                }
//            }
//        })
    }

//    fun findEvent(active: Int, callback: (Resource<List<EventItem?>>) -> Unit){
//        callback(Resource.Loading())
//
//        apiService.getEventActive(active).enqueue(object : Callback<EventResponse> { // enqueue otomatis berjalan di bg treaad
//            override fun onResponse(
//                call: Call<EventResponse>,
//                response: Response<EventResponse>
//            ) {
//                if (response.isSuccessful) {
//                    val responsBody = response.body()
//                    val respons = response.body()?.listEvents
//                    if (responsBody?.listEvents?.isNotEmpty() == true) {
//                        when(active){
//                            FINISHED -> cacheDataFinished = responsBody// masukkan chace
//                            UPCOMING -> cacheDataUpcoming = responsBody
//                        }
//
//                        val limitEvent = responsBody.listEvents//.take(5)
//                        callback(Resource.Success(limitEvent))
//                    } else {
//                        Log.e(TAG, "onResponse: data null}")
//                        callback(Resource.Empty(List<EventItem?>(2) {null})) // data kosong
//                    }
//                } else {
//                    Log.e(TAG, "onResponse: onfailure ${response.message()}")
//                    val erroMessage = errorHandling(response.code())
//                    when(active){
//                        FINISHED -> callback(Resource.Error(erroMessage, List<EventItem?>(5) {null}))
//                        UPCOMING -> callback(Resource.Error(erroMessage))
//                    }
//                }
//            }
//
//            override fun onFailure(call: Call<EventResponse>, t: Throwable) {
//                Log.e(TAG, "onResponse: onfailure ${t.message}")
//                if (t is IOException) {
//                    callback(Resource.ErrorConection(resourceProvider.getString(R.string.error_koneksi),  List<EventItem?>(5) {null}))
//                    if (cacheDataUpcoming != null || cacheDataFinished != null) {
//                        when(active){
//                            UPCOMING -> {
//                                if (cacheDataUpcoming != null)
//                                    callback(Resource.Success(cacheDataUpcoming?.listEvents ?: emptyList()))
//                                else
//                                    callback(Resource.Empty(List<EventItem?>(2) {null}))
//                            }
//                            FINISHED -> {
//                                callback(Resource.Success(cacheDataFinished?.listEvents ?: emptyList()))
//                            }
//                        }
//                        Log.d(TAG, "onFailure cace data: tes")
//                    }
//                } else {
//                    when(active){
//                        FINISHED -> callback(Resource.Error(resourceProvider.getString(R.string.error_takterduga), List<EventItem?>(5) {null}))
//                        UPCOMING -> callback(Resource.Error(resourceProvider.getString(R.string.error_takterduga)))
//                    }
//                }
//            }
//        })
//    }

//    fun findEvent(active: Int, callback: (Resource<List<EventItem?>>) -> Unit): LiveData<Resource<List<EventItem?>>> = liveData {
//        emit(Resource.Loading())
//
//        try {
//            val response = apiService.getEventActive(active) // suspend function
//            if (response.isSuccessful) {
//                val responseBody = response.body()
//                val events = responseBody?.listEvents
//
//                if (!events.isNullOrEmpty()) {
//                    val newList = events.map { event ->
//                        val isFavorit = favoritDao.isEventFavorit(event.name.toString())
//                        FavoritEvent(
//                            event.id,
//                            event.name.toString(),
//                            event.imageLogo,
//                            event.summary,
//                            event.category,
//                            event.ownerName,
//                            event.cityName,
//                            event.beginTime,
//                            event.quota,
//                            event.registrants,
//                            event.link,
//                            isFavorit
//                        )
//                    }
//
//                    favoritDao.insert(newList)
//
//                    when (active) {
//                        FINISHED -> cacheDataFinished = responseBody
//                        UPCOMING -> cacheDataUpcoming = responseBody
//                    }
//                    emit(Resource.Success(events)) // kalau mau emit juga ke LiveData
//                    Log.d(TAG, "findEvent: success")
//                } else {
//                    emit(Resource.Empty(List(2) { null }))
//                }
//            } else {
//                val errorMsg = errorHandling(response.code())
//                when (active) {
//                    FINISHED -> emit(Resource.Error(errorMsg, List(5) { null }))
//                    UPCOMING -> emit(Resource.Error(errorMsg))
//                }
//            }
//        } catch (e: IOException) {
//            emit(Resource.ErrorConection(resourceProvider.getString(R.string.error_koneksi), List(5) { null }))
//            // fallback ke cache
//            if (cacheDataUpcoming != null || cacheDataFinished != null) {
//                when (active) {
//                    UPCOMING -> {
//                        if (cacheDataUpcoming != null)
//                            callback(Resource.Success(cacheDataUpcoming?.listEvents ?: emptyList()))
//                        else
//                            callback(Resource.Empty(List<EventItem?>(2) {null}))
//                    }
//                    FINISHED -> {
//                        emit(Resource.Success(cacheDataFinished?.listEvents ?: emptyList()))
//                    }
//                }
//            }
//        } catch (e: Exception) {
//            val message = resourceProvider.getString(R.string.error_takterduga)
//            when (active) {
//                FINISHED -> emit(Resource.Error(message, List(5) { null }))
//                UPCOMING -> emit(Resource.Error(message))
//            }
//            Log.e(TAG, "findEvent: terjadi eror $e")
//        }
//    }

//    callback: (Resource<List<EventItem?>>) -> Unit

    fun findEvent(active: Int): LiveData<Resource<List<FavoritEvent?>>?> = liveData {
        emit(Resource.Loading())
        var errorHappened = false
        var isConnectNet = true
        var isEmptyList = false

        try {
            val response = apiService.getEventActive(active) // suspend function
            if (response.isSuccessful) {
                val responseBody = response.body()
                val events = responseBody?.listEvents

                if (!events.isNullOrEmpty()) {
                    val newList = events.map { event ->
                        val isFavorit = favoritDao.isEventFavorit(event.id ?: 0)
                        FavoritEvent(
                            event.id,
                            event.name,
                            event.imageLogo,
                            event.mediaCover,
                            event.summary,
                            event.category,
                            event.ownerName,
                            event.cityName,
                            event.beginTime,
                            event.quota,
                            event.registrants,
                            event.link,
                            isFavorit,
                            active
                        )
                    }

//                    favoritDao.deleteAll()
                    favoritDao.deleteAllByActive(active)
                    favoritDao.insert(newList)

                    Log.d(TAG, "findEvent: success")
                } else {
                    isEmptyList = true
//                    emit(Resource.Empty(List(2) { null }))
                }
            } else {
                val errorMsg = errorHandling(response.code())
                when (active) {
                    FINISHED -> emit(Resource.Error(errorMsg, List(5) { null }))
                    UPCOMING -> emit(Resource.Error(errorMsg))
                }
            }
        } catch (e: IOException) {
            Log.d(TAG, "findEvent: eror connect")
            isConnectNet = false
            emit(Resource.ErrorConection(resourceProvider.getString(R.string.error_koneksi), List(5) { null }))
        } catch (e: Exception) {
            errorHappened = true
            val message = resourceProvider.getString(R.string.error_takterduga)
            when (active) {
                FINISHED -> emit(Resource.Error(message, List(5) { null }))
                UPCOMING -> emit(Resource.Error(message))
            }
            Log.e(TAG, "findEvent: terjadi eror $e")
        } finally {
            if (!errorHappened ){
                val raaSource: LiveData<List<FavoritEvent>?> = when (active) {
                    UPCOMING -> favoritDao.getEventUpcoming()
                    FINISHED -> favoritDao.getEventFinished()
                    else -> favoritDao.getEventAll()
                }

                val source: LiveData<Resource<List<FavoritEvent?>>?> = raaSource.map { list ->
                    Log.d(TAG, "findEvent: list ${raaSource.value}")

                    when{
                        !list.isNullOrEmpty() -> {
                            val result: Resource<List<FavoritEvent?>> = Resource.Success(list)
                            result
                        }
                        !isConnectNet -> {
                            Log.d(TAG, "findEvent: not connect data local")
                            isConnectNet = true
                            Resource.ErrorConection(resourceProvider.getString(R.string.error_koneksi), List(5) { null })
                        }
                        isEmptyList -> Resource.Empty(List(2) { null })
                        else -> {
                            isConnectNet = true
                            Resource.Loading()
                        }
                    }
                }

                emitSource(source)
            }
        }
    }

//    fun findDetailEvent(id: Int?, callback: (Resource<Event?>) -> Unit) {
//        callback(Resource.Loading())
//
//        if (id != null){
//            apiService.getEventDetail(id).enqueue(object : Callback<DetailEventResponse> { // enqueue otomatis berjalan di bg treaad
//                override fun onResponse(
//                    call: Call<DetailEventResponse>,
//                    response: Response<DetailEventResponse>
//                ) {
//                    if (response.isSuccessful){
//                        val responsBody = response.body()
//                        if (responsBody?.event != null){
//                            cacheDataDetail = responsBody
//                            callback(Resource.Success(responsBody.event))
//                        }else{
//                            Log.e(TAG, "onResponse: data null}")
//                            callback(Resource.Empty(
//                                null,
//                                resourceProvider.getString(R.string.data_not_available_please_try_again_later)
//                            ))
//                        }
//                        Log.d("res", "onResponse: succes $responsBody")
//                    }else{
//                        Log.e("res", "onResponse: onfailure ${response.message()}")
//                        val errorMessage = errorHandling(response.code())
//                        callback(Resource.Error(errorMessage))
//                    }
//                }
//
//                override fun onFailure(call: Call<DetailEventResponse>, t: Throwable) {
//                    Log.e(TAG, "detail onResponse: onfailure ${t.message}")
//                    if (t is IOException) {
//                        callback(Resource.ErrorConection(resourceProvider.getString(R.string.error_koneksi)))
//                        if (cacheDataDetail != null ){
//                            Log.d(TAG, "onFailure: cache != null ")
//                            callback(Resource.Success(cacheDataDetail?.event))
//                        }
////                    else
////                        callback(Resource.ErrorConection(context.resources.getString(R.string.error_koneksi)))
//                    } else {
//                        callback(Resource.Error(resourceProvider.getString(R.string.error_takterduga)))
//                    }
//                }
//            })
//        } else{
//            callback(Resource.Empty(null))
//        }
//    }

//    callback: (Resource<Event?>) -> Unit

    fun findDetailEvent(id: Int):  LiveData<Resource<Event?>> = liveData{
        emit(Resource.Loading())

        Log.d(TAG, "findDetailEvent: id $id")
//        if (id != null){
            try {
                val response = apiService.getEventDetail(id)// enqueue otomatis berjalan di bg treaad

                if (response.isSuccessful) {
                    val responsBody = response.body()
                    if (responsBody?.event != null) {
                        cacheDataDetail = responsBody
                        emit(Resource.Success(responsBody.event))
                    } else {
                        Log.e(TAG, "onResponse: data null}")
                        emit(
                            Resource.Empty(
                                null,
                                resourceProvider.getString(R.string.data_not_available_please_try_again_later)
                            )
                        )
                    }
                    Log.d("res", "onResponse: succes $responsBody")
                } else {
                    Log.e("res", "onResponse: onfailure ${response.message()}")
                    val errorMessage = errorHandling(response.code())
                    emit(Resource.Error(errorMessage))

                }
            }catch (e: IOException){
                Log.d(TAG, "findDetailEvent: eror konek")
                emit(Resource.ErrorConection(resourceProvider.getString(R.string.error_koneksi)))
                if (cacheDataDetail?.event?.id == id && cacheDataDetail != null ){
                    Log.d(TAG, "onFailure: cache != null ")
                    emit(Resource.Success(cacheDataDetail?.event))
                }
            }catch (e: Exception){
                emit(Resource.Error(resourceProvider.getString(R.string.error_takterduga)))
            }

//        }
    }

    suspend fun setFavoritState(id: Int?) {
        val item = favoritDao.getById(id)
        val newBookmark = !item.isBookmarked
        favoritDao.updateFavoritState(id, newBookmark)
    }

    fun observeFavoritById(id: Int?): LiveData<FavoritEvent> = liveData{
        emitSource(favoritDao.observeById(id))
    }

    private fun errorHandling(code: Int): String {
        return when (code) {
            400 -> resourceProvider.getString(R.string.error_400)
            403 -> resourceProvider.getString(R.string.error_403)
            404 -> resourceProvider.getString(R.string.error_404)
            408 -> resourceProvider.getString(R.string.error_408)
            500 -> resourceProvider.getString(R.string.error_500)
            503 -> resourceProvider.getString(R.string.error_503)
            else -> resourceProvider.getString(R.string.error_else)
        }
    }

    suspend fun fetchNearestEvent(): EventItem?{
        val response = apiService.getEventActive(UPCOMING)

        if (response.isSuccessful){
            val lisEvent = response.body()?.listEvents
            val event = lisEvent?.get(lisEvent.size - 1)
            return event
        }else{
            throw Exception("Gagal ambil event: ${response.code()}")
        }
    }

//    companion object {
//        const val TAG = "srepo"
//        private const val SEARCH_HISTORY = "search_history"
//        private const val HISTORY_LIST = "history_list"
//        private const val FINISHED = 0
//        private const val UPCOMING = 1
//    }
}
