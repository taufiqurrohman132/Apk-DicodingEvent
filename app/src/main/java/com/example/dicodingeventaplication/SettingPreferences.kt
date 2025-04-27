package com.example.dicodingeventaplication

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.concurrent.Volatile

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingPreferences private constructor(private val dataStore: DataStore<Preferences>){
    private val themeKey = booleanPreferencesKey("theme_setting")
    private val notificationKey = booleanPreferencesKey("notification_settings")
//    private val searchResultKey = stringPreferencesKey("search_result")

    // theme
    fun getThemeSetting(): Flow<Boolean>{
        return dataStore.data.map { preferences ->
            preferences[themeKey] ?: false
        }
    }

    fun getNotificationSetting(): Flow<Boolean>{
        return dataStore.data.map { preferences ->
            preferences[notificationKey] ?: false
        }
    }

    // notification
    suspend fun saveThemeSetting(isDarkModeActive: Boolean){
        dataStore.edit { preferences ->
            preferences[themeKey] = isDarkModeActive
        }
    }

    suspend fun saveNotificationSetting(isNotifActive: Boolean){
        dataStore.edit { preferences ->
            preferences[notificationKey] = isNotifActive
        }
    }

    // searc result
//    suspend fun saveSearchResult(result: List<EventItem>){
//        val gson = Gson()
//        val json = gson.toJson(result)
//
//        dataStore.edit { preferences ->
//            preferences[searchResultKey] = json
//        }
//    }

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
