package com.example.dicodingeventaplication

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.work.CoroutineWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.dicodingeventaplication.data.di.Injection
import com.example.dicodingeventaplication.data.repository.DicodingEventRepository
import com.example.dicodingeventaplication.utils.NotificationHelper
import kotlinx.coroutines.flow.first
import java.time.LocalDate

class EventWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {
    private var resultStatus: Result? = null

    companion object{
        private val TAG = "work"
        const val EXTRA_TIME = "time"
        const val NOTIFICATION_ID = 1
        const val CHANNEL_ID = "channel_01"
        const val CHANNEL_NAME = "event channel"
    }

    override suspend fun doWork(): Result {
        return try {
            val today = LocalDate.now().toString()
            val notifKey = stringPreferencesKey("last_notif_date")

            val pref = applicationContext.dataStore.data.first()
            val lastNotif = pref[notifKey]
            Log.d(TAG, "doWork: pref $pref")

            val repository = Injection.provideRepository(applicationContext)
            val nearestEvent = repository.fetchNearestEvent()
            Log.d(TAG, "doWork: event $nearestEvent")

            if (today != lastNotif){
                nearestEvent?.let { event ->
                    Log.d(TAG, "doWork: tampilkan notifikasi")
                    val notificationHelper = NotificationHelper(applicationContext)
                    notificationHelper.showNotification(
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
            Log.e(TAG, "doWork: failure", )
            Result.retry()
        }
    }

    private fun getNearestActiveEvent(repository: DicodingEventRepository){

    }


}