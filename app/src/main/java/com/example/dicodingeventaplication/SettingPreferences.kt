package com.example.dicodingeventaplication

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.preferencesOf
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.dicodingeventaplication.data.remote.model.EventItem
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.concurrent.Volatile

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingPreferences private constructor(private val dataStore: DataStore<Preferences>){
    private val THEME_KEY = booleanPreferencesKey("theme_setting")
    private val NOTIFICATION_KEY = booleanPreferencesKey("notification_settings")
    private val SEARCH_RESULT_KEY = stringPreferencesKey("search_result")

    // theme
    fun getThemeSetting(): Flow<Boolean>{
        return dataStore.data.map { preferences ->
            preferences[THEME_KEY] ?: false
        }
    }

    fun getNotificationSetting(): Flow<Boolean>{
        return dataStore.data.map { preferences ->
            preferences[NOTIFICATION_KEY] ?: false
        }
    }

    // notification
    suspend fun saveThemeSetting(isDarkModeActive: Boolean){
        dataStore.edit { preferences ->
            preferences[THEME_KEY] = isDarkModeActive
        }
    }

    suspend fun saveNotificationSetting(isNotifActive: Boolean){
        dataStore.edit { preferences ->
            preferences[NOTIFICATION_KEY] = isNotifActive
        }
    }

    // searc result
    suspend fun saveSearchResult(result: List<EventItem>){
        val gson = Gson()
        val json = gson.toJson(result)

        dataStore.edit { preferences ->
            preferences[SEARCH_RESULT_KEY] = json
        }
    }

    companion object{
        @Volatile
        private var INSTANCE: SettingPreferences? = null

        fun getInstance(dataStore: DataStore<Preferences>): SettingPreferences{
            return INSTANCE ?: synchronized(this){
                val instances = SettingPreferences(dataStore)
                INSTANCE = instances
                instances
            }
        }
    }
}
