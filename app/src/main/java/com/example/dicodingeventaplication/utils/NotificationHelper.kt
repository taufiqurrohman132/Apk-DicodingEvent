package com.example.dicodingeventaplication.utils

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.dicodingeventaplication.EventWorker
import com.example.dicodingeventaplication.R

class NotificationHelper(private val context: Context) {

    @SuppressLint("ObsoleteSdkInt")
    fun showNotification(
        name: String,
        beginTime: String,
        channelId: String,
        channelName: String,
        notificationId: Int
    ){
        Log.d("notif", "showNotification: jalan")
        val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.ic_apps)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification: NotificationCompat.Builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_apps)
            .setLargeIcon(bitmap)
            .setContentTitle(name)
            .setContentText(beginTime)
            .setSubText("halo dicoding")
            .setContentInfo(name)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            Log.d("jalan", "showNotification: jalan")
            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
            notification.setChannelId(channelId)
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(notificationId, notification.build())


    }

}
