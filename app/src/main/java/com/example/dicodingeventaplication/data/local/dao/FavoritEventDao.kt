package com.example.dicodingeventaplication.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.dicodingeventaplication.data.local.entity.FavoritEventEntity

@Dao
interface FavoritEventDao {
    @Query("SELECT * FROM favoritEvent ORDER BY createAt DESC")
    fun getAllFavorites(): LiveData<List<FavoritEventEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorit: FavoritEventEntity)

    @Delete
    suspend fun deleteFavorite(favorit: FavoritEventEntity)

}