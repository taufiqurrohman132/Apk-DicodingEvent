package com.example.dicodingeventaplication.utils

interface ResourceProvider {
    fun getString(resId: Int): String
}