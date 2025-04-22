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
    @Query("SELECT * FROM favorit WHERE isActive = 1 ORDER BY beginTime ASC")
    fun getEventUpcoming(): LiveData<List<FavoritEvent>?>

    @Query("SELECT * FROM favorit WHERE isActive = 0 ORDER BY beginTime DESC")
    fun getEventFinished(): LiveData<List<FavoritEvent>?>

    @Query("SELECT * FROM favorit WHERE isActive = -1 ORDER BY beginTime DESC")
    fun getEventAll(): LiveData<List<FavoritEvent>?>

    @Query("SELECT * FROM favorit WHERE bookmarked = 1")
    fun getBookmarkedEvent(): LiveData<List<FavoritEvent>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(event: List<FavoritEvent>)

    @Update
    suspend fun updateFavorit(favorit: FavoritEvent)

    @Query("DELETE FROM favorit WHERE bookmarked = 0")
    suspend fun deleteAll()

    @Query("DELETE FROM favorit WHERE isActive = :active")
    suspend fun deleteAllByActive(active: Int)

    @Query("SELECT EXISTS(SELECT * FROM favorit WHERE id = :id AND bookmarked = 1)")
    suspend fun isEventFavorit(id: Int): Boolean

    @Query("SELECT * FROM favorit WHERE id = :id")
    suspend fun getById(id: Int?): FavoritEvent
}