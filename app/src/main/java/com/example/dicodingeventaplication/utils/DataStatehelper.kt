package com.example.dicodingeventaplication.utils

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.example.dicodingeventaplication.MyApp
import com.example.dicodingeventaplication.SettingPreferences
import com.example.dicodingeventaplication.dataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

object DataStatehelper {

    private val HAS_LOCAL_DATA_KEY = booleanPreferencesKey("has_local_data")

    // myapp instance = pengganti context
    suspend fun setHasLocalData(isLocal: Boolean){
        MyApp.instance.dataStore.edit { preferences ->
            preferences[HAS_LOCAL_DATA_KEY] = isLocal
        }
    }


    fun getHasLocalState(): Flow<Boolean> {
        return MyApp.instance.dataStore.data.map { preferences ->
            preferences[HAS_LOCAL_DATA_KEY] ?: false
        }
    }


}