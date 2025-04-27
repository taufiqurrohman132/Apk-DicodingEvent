package com.example.dicodingeventaplication.utils

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import com.example.dicodingeventaplication.R
import com.example.dicodingeventaplication.data.local.entity.FavoritEvent
import com.example.dicodingeventaplication.ui.detailEvent.DetailEventActivity

class NotificationHelper(private val context: Context) {

    @SuppressLint("ObsoleteSdkInt")
    fun showNotification(
        calculateDateEvent: String,
        eventData: FavoritEvent,
        name: String,
        beginTime: String,
        channelId: String,
        channelName: String,
        notificationId: Int
    ){
        Log.d("notif", "showNotification: jalan")
        val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.ic_apps)
        val title = "[H-$calculateDateEvent] $name"

        val intent = Intent(context, DetailEventActivity::class.java).apply {
            putExtra(DetailEventActivity.EXTRA_EVENT, eventData)
        }

        // mgenatur back stack aga kembali ke parnt yaitu main
        val pendingIntent = TaskStackBuilder.create(context).run {
            addNextIntentWithParentStack(intent)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                getPendingIntent(notificationId, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
            }else{
                getPendingIntent(notificationId, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
            }
        }

//        val pendingIntent = PendingIntent.getActivity(
//            context,
//            0,
//            intent,
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0
//        )

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification: NotificationCompat.Builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_apps)
            .setLargeIcon(bitmap)
            .setContentTitle(title)
            .setContentText(beginTime)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setAutoCancel(true)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            Log.d("jalan", "showNotification: jalan")
            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
            notification.setChannelId(channelId)
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(notificationId, notification.build())
    }

}
