package com.example.dicodingeventaplication.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.dicodingeventaplication.data.local.entity.EventEntity

@Dao
interface EventDao {
    @Query("SELECT * FROM event WHERE isActive = 1 ORDER BY beginTime ASC")
    fun getEventUpcoming(): LiveData<List<EventEntity>?>

    @Query("SELECT * FROM event WHERE isActive = 0 ORDER BY beginTime DESC")
    fun getEventFinished(): LiveData<List<EventEntity>?>

    @Query("SELECT * FROM event WHERE isActive = -1 ORDER BY beginTime DESC")
    fun getEventAll(): LiveData<List<EventEntity>?>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(event: List<EventEntity>)

    @Update
    suspend fun updateEvent(event: EventEntity)

    @Query("DELETE FROM event WHERE isActive = :active")
    suspend fun deleteAllByActive(active: Int)

    @Query("SELECT EXISTS(SELECT * FROM event WHERE id = :id AND bookmarked = 1)")
    suspend fun isEventFavorit(id: Int): Boolean

    @Query("SELECT * FROM event WHERE id = :id")
    suspend fun getById(id: Int?): EventEntity // ketika mengamati terus itu tidak butuh suspand

    @Query("SELECT * FROM event WHERE id = :id")
    fun observeById(id: Int?): LiveData<EventEntity>

    // ubah state favorit
    @Query("UPDATE event SET bookmarked = :isFavorit WHERE id = :id")
    suspend fun updateFavoritState(id: Int?, isFavorit: Boolean)

    // cek ada data
    @Query("SELECT EXISTS(SELECT 1 FROM event WHERE isActive = :isActive LIMIT 1)")
    suspend fun hasDataByActiveStatus(isActive: Int): Boolean

}