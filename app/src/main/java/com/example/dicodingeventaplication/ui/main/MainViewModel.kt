package com.example.dicodingeventaplication.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.dicodingeventaplication.data.local.datastore.SettingPreferences
import kotlinx.coroutines.launch

class MainViewModel(private val pref: SettingPreferences) : ViewModel() {
    var scrollY: Int= 0

    fun getThemeSettings(): LiveData<Boolean>{
        return pref.getThemeSetting().asLiveData()
    }

    fun saveThemeSetting(isDarkModeActive: Boolean){
        viewModelScope.launch {
            pref.saveThemeSetting(isDarkModeActive)
        }
    }

    fun getNotificationSettings(): LiveData<Boolean>{
        return pref.getNotificationSetting().asLiveData()
    }

    fun saveNotificationSetting(isNotifActive: Boolean){
        viewModelScope.launch {
            pref.saveNotificationSetting(isNotifActive)
        }
    }
}