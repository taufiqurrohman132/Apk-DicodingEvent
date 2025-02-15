package com.example.dicodingeventaplication.ui.search

import com.example.dicodingeventaplication.data.respons.EventItem

sealed class SearchItem {
    data class HistoryItem(val eventItem: EventItem) : SearchItem(){
        val id get() = eventItem.id
    }
    data class ResultItem(val eventItem: EventItem) : SearchItem(){
        val id get() = eventItem.id
    }
//    data object Header : SearchItem(){
//        const val id = -1
//    }

}