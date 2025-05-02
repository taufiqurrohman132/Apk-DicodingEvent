package com.example.dicodingeventaplication.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.dicodingeventaplication.data.local.dao.EventDao
import com.example.dicodingeventaplication.data.local.dao.FavoritEventDao2
import com.example.dicodingeventaplication.data.local.entity.EventEntity
import com.example.dicodingeventaplication.data.local.entity.FavoritEventEntity

@Database(entities = [EventEntity::class, FavoritEventEntity::class], version = 1, exportSchema = false)
abstract class FavoritEventRoomDatabase : RoomDatabase() {
    abstract fun eventDao(): EventDao
    abstract fun favoritDao(): FavoritEventDao2

    companion object{
        @Volatile
        private var instance: FavoritEventRoomDatabase? = null

        fun getInstance(context: Context): FavoritEventRoomDatabase =
            instance ?: Room.databaseBuilder(
                context.applicationContext,
                FavoritEventRoomDatabase::class.java,
                "favorit.database"
            ).build()
    }
}