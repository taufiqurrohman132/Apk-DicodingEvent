package com.example.dicodingeventaplication

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.dicodingeventaplication.data.di.Injection
import com.example.dicodingeventaplication.utils.NotificationHelper
import com.example.dicodingeventaplication.utils.TimeUtils
import kotlinx.coroutines.flow.first
import java.time.LocalDate

class EventWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {
//    private var resultStatus: Result? = null

    companion object{
        private const val TAG = "work"
        const val NOTIFICATION_ID = 1
        const val CHANNEL_ID = "channel_02"
        const val CHANNEL_NAME = "event channel new"
    }

    override suspend fun doWork(): Result {
        return try {
            // tidak mengulangi jika dihari yang sama
            val todayDate = LocalDate.now()
            val today = todayDate.toString()
            val notifKey = stringPreferencesKey("last_notif_date")

            val pref = applicationContext.dataStore.data.first()
            val lastNotif = pref[notifKey]
            Log.d(TAG, "doWork: pref $pref")

            val repository = Injection.provideRepository(applicationContext)
            val nearestEvent = repository.fetchNearestEvent()
            Log.d(TAG, "doWork: event $nearestEvent")

            Log.d(TAG, "doWork: today $today")
            if (today != lastNotif){
                nearestEvent?.let { event ->
                    Log.d(TAG, "doWork: tampilkan notifikasi")
                    val notificationHelper = NotificationHelper(applicationContext)
                    val eventData = repository.getDetailFromSearch(event)

                    val dateEvent = TimeUtils.calculateDaysToEvent(event.formatYear.toString())
                    notificationHelper.showNotification(
                        dateEvent.toString(),
                        eventData,
                        event.name ?: "Dicoding Event",
                        event.beginTime ?: "Menunggu jadwal",
                        CHANNEL_ID,
                        CHANNEL_NAME,
                        NOTIFICATION_ID
                    )

                    // simpan tanggal hari ini
                    applicationContext.dataStore.edit { prefEdit ->
                        prefEdit[notifKey] = today
                    }
                }

            }else{
                Log.d(TAG, "doWork: notifikasi hari ini sudah di kirim")
            }

            Result.success()
        }catch (e: Exception){
            Log.e(TAG, "doWork: failure")
            Result.retry()
        }
    }



}