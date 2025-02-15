package com.example.dicodingeventaplication.utils

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
}