package com.example.dicodingeventaplication.data.local.database

import android.content.Context
import androidx.collection.intSetOf
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.dicodingeventaplication.data.local.dao.FavoritEventDao
import com.example.dicodingeventaplication.data.local.entity.FavoritEvent

@Database(entities = [FavoritEvent::class], version = 1)
abstract class FavoritEventRoomDatabase : RoomDatabase() {
    abstract fun favoritEventDao(): FavoritEventDao

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