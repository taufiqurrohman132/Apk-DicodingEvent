package com.example.dicodingeventaplication

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.util.concurrent.TimeUnit

class MyApp : Application() {

    companion object{
        lateinit var instance: MyApp
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        themeState()
//        startDailyReminderEvent()
    }

    private fun startDailyReminderEvent(){
        val periodicRequest = PeriodicWorkRequestBuilder<EventWorker>(
            15, TimeUnit.MINUTES
        ).build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "upcoming_event_check",
            ExistingPeriodicWorkPolicy.KEEP,
            periodicRequest
        )
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