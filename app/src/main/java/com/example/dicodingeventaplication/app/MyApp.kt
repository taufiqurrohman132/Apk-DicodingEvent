package com.example.dicodingeventaplication.app

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.dicodingeventaplication.data.local.datastore.SettingPreferences
import com.example.dicodingeventaplication.data.local.datastore.dataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class MyApp : Application() {

    companion object{
        lateinit var instance: MyApp
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        themeState()
    }

    private fun themeState(){
        val preferences = SettingPreferences.getInstance(dataStore)
        val isDark = runBlocking {
            preferences.getThemeSetting().first()
        }

        AppCompatDelegate.setDefaultNightMode(
            if (isDark) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }
}