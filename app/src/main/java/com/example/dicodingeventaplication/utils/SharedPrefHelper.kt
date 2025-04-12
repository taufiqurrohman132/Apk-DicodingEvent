package com.example.dicodingeventaplication.utils

import android.content.Context
import android.util.Log
import com.example.dicodingeventaplication.data.remote.model.EventItem
import com.example.dicodingeventaplication.data.repository.DicodingEventRepository
import com.example.dicodingeventaplication.data.repository.DicodingEventRepository.Companion.HISTORY_LIST
import com.example.dicodingeventaplication.data.repository.DicodingEventRepository.Companion.TAG
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SharedPrefHelper(context: Context) {
    private val sharedPref = context.applicationContext.getSharedPreferences(DicodingEventRepository.SEARCH_HISTORY, Context.MODE_PRIVATE)
    private val gson = Gson()

    // ambil history dari shered
    fun getSearchHistory(): List<EventItem> {
        val json = sharedPref.getString(HISTORY_LIST, "[]") ?: "[]"// default empty list
        Log.d(TAG, "getSearchHistory: $json")
        val type = object : TypeToken<List<EventItem>>() {}.type
        return gson.fromJson(json, type)
    }

    // simpan id ke shered / dimpan ke history
    fun saveSearchHistory(eventItem: EventItem) {
        val historyList = getSearchHistory().toMutableList()
        historyList.removeAll { it.id == eventItem.id } // hindsari duplikasi
        historyList.add(0, eventItem) // tambahkan ke awal list
        if (historyList.size > 15) {// batas max item
            historyList.dropLast(1) // hapus elemen terahir
        }

        val json = gson.toJson(historyList)
        sharedPref.edit().putString(HISTORY_LIST, json).apply()
        Log.d(TAG, "saveSearchHistory: $json")
    }

    // bersihkan history
    fun clearHistory() {
        sharedPref.edit().remove(HISTORY_LIST).apply()
    }

    fun removeItemHistory(eventItem: EventItem){
        val historyList = getSearchHistory().toMutableList()
        historyList.removeAll { it.id == eventItem.id }

        val json = gson.toJson(historyList)
        sharedPref.edit().putString(HISTORY_LIST, json).apply()
        Log.d(TAG, "remoseSearchHistory: $json")
    }
}