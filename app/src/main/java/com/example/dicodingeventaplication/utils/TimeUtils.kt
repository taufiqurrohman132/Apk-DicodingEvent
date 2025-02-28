package com.example.dicodingeventaplication.utils

import android.util.Log
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

object TimeUtils {

    fun getMount(date: String?): String{
        val month = arrayOf(
            "JAN",  "FEB",  "MAR",  "APR",  "MEI",  "JUN",
            "JUL",  "AGS",  "SEP",  "OKT",  "NOV",  "DES"
        )

        return try {
            val index = date?.toIntOrNull()?.minus(1) ?: return ""
            if (index in month.indices) month[index] else ""
        }catch (e: Exception){
            return ""
        }
    }

    fun isEventFinished(eventDate: String): Boolean {
        // format data dari api
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        Log.d("format", "isEventFinished: formater $formatter")

        // konversi string ke local date time
        val eventDateTime = LocalDateTime.parse(eventDate, formatter)

        // ambil zona waktu dari hp
        val deviceZone = ZoneId.systemDefault()

        // konversi waktu event ke zona waktu perangkat
        val eventDateTimeDevice = eventDateTime.atZone(deviceZone)

        // ambil waktu seakrangdi zona waktu yang sama
        val now = ZonedDateTime.now(deviceZone)

        Log.d("EventTime", "Parsed event time: $eventDateTimeDevice")
        Log.d("CurrentTime", "Current time: $now")

        // event selsai jika eent data time sebelum waktu sekarang
        return eventDateTimeDevice.isBefore(now)
    }
}