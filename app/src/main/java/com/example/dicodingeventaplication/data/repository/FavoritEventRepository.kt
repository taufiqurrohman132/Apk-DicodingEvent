package com.example.dicodingeventaplication.data.repository

//import androidx.lifecycle.LiveData
//import androidx.lifecycle.MediatorLiveData
//import com.example.dicodingeventaplication.data.local.dao.FavoritEventDao
//import com.example.dicodingeventaplication.data.local.entity.FavoritEvent
//import com.example.dicodingeventaplication.data.remote.network.ApiService
//import com.example.dicodingeventaplication.utils.AppExecutors
//import com.example.dicodingeventaplication.utils.Resource

//class FavoritEventRepository(
//    private val apiService: ApiService,
//    private val favoritDao: FavoritEventDao,
//    private val appExecutors: AppExecutors
//) {
//    private val result = MediatorLiveData<Resource<List<FavoritEvent>>>()
//
//    companion object{
//        @Volatile
//        private var instance: FavoritEventRepository? = null
//
//        fun getInstance(
//            apiService: ApiService,
//            favoritDao: FavoritEventDao,
//            appExecutors: AppExecutors
//        ): FavoritEventRepository =
//            instance ?: synchronized(this){
//                instance ?: FavoritEventRepository(apiService, favoritDao, appExecutors)
//            }.also { instance = it }
//    }
//
//    suspend fun setFavoritBookmark(favorit: FavoritEvent, bookmarkState: Boolean){
//        favorit.isBookmarked = bookmarkState
//        favoritDao.updateFavorit(favorit)
//    }
//
//    fun getFavoritBookmark(): LiveData<List<FavoritEvent>>{
//        return favoritDao.getBookmarkedFavorit()
//    }
//
//}