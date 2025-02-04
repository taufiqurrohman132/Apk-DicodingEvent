package com.example.dicodingeventaplication.ui.upcoming

import com.example.dicodingeventaplication.data.respons.EventItem

sealed class UpcomingItem {
    data class ResultItem(val eventItem: EventItem) : UpcomingItem()

    data object Loading : UpcomingItem()
}