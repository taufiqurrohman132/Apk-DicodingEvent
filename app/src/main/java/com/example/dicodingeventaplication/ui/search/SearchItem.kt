package com.example.dicodingeventaplication.ui.search

import com.example.dicodingeventaplication.data.respons.EventItem

sealed class SearchItem {
//    data class Header(val title: String): SearchItem()
//    data class History(val id: Int, val name: String) : SearchItem()
//    data class Result(val id: Int, val name: String) : SearchItem()

    data class HistoryItem(val eventItem: EventItem) : SearchItem(){
        val id get() = eventItem.id
    }
    data class ResultItem(val eventItem: EventItem) : SearchItem(){
        val id get() = eventItem.id
    }
    object Header : SearchItem(){
        const val id = -1
    }

//    object HeaderResult: SearchItem() {
//        const val status =
//    }
//    class HeaderResult(val status: String) : SearchItem() {
//    }

}