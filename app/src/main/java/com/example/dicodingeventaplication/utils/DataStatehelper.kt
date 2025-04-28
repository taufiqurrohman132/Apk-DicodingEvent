package com.example.dicodingeventaplication.utils

import android.util.Log
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.example.dicodingeventaplication.MyApp
import com.example.dicodingeventaplication.dataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

object DataStatehelper {

    private val HAS_LOCAL_DATA_FINISHED_KEY = booleanPreferencesKey("has_local_data_finish")
    private val HAS_LOCAL_DATA_UPCOME_KEY = booleanPreferencesKey("has_local_data_upcome")

    // myapp instance = pengganti context
    suspend fun setHasLocalDataFinished(isLocal: Boolean){
        MyApp.instance.dataStore.edit { preferences ->
            preferences[HAS_LOCAL_DATA_FINISHED_KEY] = isLocal
        }
        Log.d("islocal", "setHasLocalData: islocal $isLocal")
    }
    suspend fun setHasLocalDataUpcome(isLocal: Boolean){
        MyApp.instance.dataStore.edit { preferences ->
            preferences[HAS_LOCAL_DATA_UPCOME_KEY] = isLocal
        }
        Log.d("islocal", "setHasLocalData: islocal $isLocal")
    }


    fun getHasLocaFinishedlState(): Flow<Boolean> {
        return MyApp.instance.dataStore.data.map { preferences ->
            val value = preferences[HAS_LOCAL_DATA_FINISHED_KEY] ?: false
            Log.d("islocal", "getHasLocalState: is $value")
            value
        }
    }

    fun getHasLocaUpcomelState(): Flow<Boolean> {
        return MyApp.instance.dataStore.data.map { preferences ->
            val value = preferences[HAS_LOCAL_DATA_UPCOME_KEY] ?: false
            Log.d("islocal", "getHasLocalState: is $value")
            value
        }
    }


}