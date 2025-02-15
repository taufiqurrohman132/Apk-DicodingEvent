//package com.example.dicodingeventaplication.Utils
//
//import android.content.Context
//import android.net.ConnectivityManager
//
//object NetworkUtils {
//    fun isOnline(context: Context): Boolean{
//        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
//        val networkInfo = connectivityManager.activeNetworkInfo
//        return networkInfo != null && networkInfo.isConnected
//    }
//}