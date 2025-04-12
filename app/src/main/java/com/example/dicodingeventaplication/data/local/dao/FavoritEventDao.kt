package com.example.dicodingeventaplication.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.dicodingeventaplication.data.local.entity.FavoritEvent

@Dao
interface FavoritEventDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(favoritEvent: FavoritEvent)

    @Query("SELECT * FROM favorit WHERE bookmarked = 1")
    fun getBookmarkedFavorit(): LiveData<List<FavoritEvent>>

    @Update
    suspend fun updateFavorit(favorit: FavoritEvent)


}