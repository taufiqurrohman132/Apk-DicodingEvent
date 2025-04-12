package com.example.dicodingeventaplication.utils

import android.content.Context

class DefaultResourceProvider(private val context: Context) : ResourceProvider {
    override fun getString(resId: Int): String = context.getString(resId)
}