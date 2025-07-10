package com.example.dicodingeventaplication.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import com.example.dicodingeventaplication.R
import com.example.dicodingeventaplication.data.local.dao.EventDao
import com.example.dicodingeventaplication.data.local.dao.FavoritEventDao
import com.example.dicodingeventaplication.data.local.entity.EventEntity
import com.example.dicodingeventaplication.data.local.entity.FavoritEventEntity
import com.example.dicodingeventaplication.data.remote.model.DetailEventResponse
import com.example.dicodingeventaplication.data.remote.model.Event
import com.example.dicodingeventaplication.utils.Resource
import com.example.dicodingeventaplication.data.remote.model.EventItem
import com.example.dicodingeventaplication.data.remote.model.EventResponse
import com.example.dicodingeventaplication.data.remote.network.ApiService
import com.example.dicodingeventaplication.utils.ResourceProvider
import com.example.dicodingeventaplication.utils.SharedPrefHelper
import okio.IOException
import kotlin.concurrent.Volatile

class DicodingEventRepository private constructor(
    private val resourceProvider: ResourceProvider,
    private val sharedPrefHelper: SharedPrefHelper,
    private val apiService: ApiService,
    private val eventDao: EventDao,
    private val favoritDao: FavoritEventDao,
) {

    // variable chache
    private var  cacheDataSearching: EventResponse? = null
    private var  cacheDataDetail: DetailEventResponse? = null

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
            eventDao: EventDao,
            favoritDao: FavoritEventDao,
        ): DicodingEventRepository =
            instance ?: synchronized(this){
                instance ?: DicodingEventRepository(
                    resourceProvider,
                    sharedPrefHelper,
                    apiService,
                    eventDao,
                    favoritDao,
                )
            }.also { instance = it }
    }

    // FAVORIT
    suspend fun setFavoritBookmark(eventEntity: EventEntity, bookmarkState: Boolean, createAt: Long){
        val updateEvent = eventEntity.copy(isBookmarked = bookmarkState, createAt = createAt)
        eventDao.updateEvent(updateEvent)
    }

    fun getAllFavorit(): LiveData<List<FavoritEventEntity>> =
        favoritDao.getAllFavorites()

    suspend fun insertFavorit(favorit: FavoritEventEntity) =
        favoritDao.insertFavorite(favorit)

    suspend fun deleteFavorit(favorit: FavoritEventEntity) =
        favoritDao.deleteFavorite(favorit)

    suspend fun getDetailFromFavorit(favorit: FavoritEventEntity) =
        eventDao.getById(favorit.id)


    suspend fun getDetailFromSearch(eventItem: EventItem) =
        eventDao.getById(eventItem.id)

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

    // search
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
    }


    val startTime = System.currentTimeMillis()
    fun findEvent(active: Int): LiveData<Resource<List<EventEntity?>>?> = liveData {
        emit(Resource.Loading())
        var errorHappened = false
        var isConnectNet = true

        try {
            val response = apiService.getEventActive(active) // suspend function
            if (response.isSuccessful) {

                val responseBody = response.body()
                val events = responseBody?.listEvents
                errorHappened = false

                if (!events.isNullOrEmpty()) {
                    val newList = events.map { event ->
                        val isFavorit = eventDao.isEventFavorit(event.id ?: 0)
                        EventEntity(
                            event.id ?: 0,
                            event.name,
                            event.imageLogo,
                            event.mediaCover,
                            event.summary,
                            event.category,
                            event.ownerName,
                            event.cityName,
                            event.beginTime,
                            event.endTime,
                            event.quota,
                            event.registrants,
                            event.link,
                            isFavorit,
                            active,
                            0
                        )
                    }

                    eventDao.deleteAllByActive(active)
                    eventDao.insert(newList)

                    val endTime = System.currentTimeMillis()
                    Log.d("Performance", "API Load time: ${endTime - startTime} ms")



                    Log.d(TAG, "findEvent: success")
                } else {
                    emit(Resource.Empty(List(2) { null }))
                    if (!eventDao.hasDataByActiveStatus(active)){
                        Log.d(TAG, "findEvent: is not data active $active")
                        return@liveData
                    }
                }
            } else {
                errorHappened = true
                val errorMsg = errorHandling(response.code())
                when (active) {
                    FINISHED -> emit(Resource.Error(errorMsg, List(5) { null }))
                    UPCOMING -> emit(Resource.Error(errorMsg))
                }
                if (!eventDao.hasDataByActiveStatus(active)){
                    Log.d(TAG, "findEvent: is not data active $active")
                    return@liveData
                }
            }
        } catch (e: IOException) {
            Log.d(TAG, "findEvent: eror connect")
            isConnectNet = false
            emit(Resource.ErrorConection(resourceProvider.getString(R.string.error_koneksi), List(5) { null }))
            if (!eventDao.hasDataByActiveStatus(active)){
                Log.d(TAG, "findEvent: is not data active $active")
                return@liveData
            }
        } catch (e: Exception) {
            errorHappened = true
            isConnectNet = true
            val message = resourceProvider.getString(R.string.error_takterduga)
            when (active) {
                FINISHED -> emit(Resource.Error(message, List(5) { null }))
                UPCOMING -> emit(Resource.Error(message))
            }
            Log.e(TAG, "findEvent: terjadi eror $e")
            if (!eventDao.hasDataByActiveStatus(active)){
                Log.d(TAG, "findEvent: is not data active $active")
                return@liveData
            }
        } finally {
        }
        if (!errorHappened) {
            val raaSource: LiveData<List<EventEntity>?> = when (active) {
                UPCOMING -> eventDao.getEventUpcoming()
                FINISHED -> eventDao.getEventFinished()
                else -> eventDao.getEventAll()
            }

            val source: LiveData<Resource<List<EventEntity?>>?> = raaSource.map { list ->
                isConnectNet = true
                errorHappened = false

                when {
                    !list.isNullOrEmpty() -> {
                        Log.d(TAG, "findEvent: local succes")
                        Resource.Success(list)
                    }

                    !isConnectNet -> {
                        Log.d(TAG, "findEvent: local eror conect")
                        Resource.ErrorConection(
                            resourceProvider.getString(R.string.error_koneksi),
                            List(5) { null })
                    }

                    else -> {
                        Log.d(TAG, "findEvent: local loading")
                        Resource.Loading()
                    }
                }
            }

            emitSource(source)
        }
    }

    fun findDetailEvent(id: Int):  LiveData<Resource<Event?>> = liveData{
        emit(Resource.Loading())

        Log.d(TAG, "findDetailEvent: id $id")
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
    }

    suspend fun setFavoritState(id: Int?): Boolean {
        val item = eventDao.getById(id)
        val newBookmark = !item.isBookmarked
        eventDao.updateFavoritState(id, newBookmark)
        return newBookmark
    }

    fun observeFavoritById(id: Int?): LiveData<EventEntity> = liveData{
        emitSource(eventDao.observeById(id))
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

}
