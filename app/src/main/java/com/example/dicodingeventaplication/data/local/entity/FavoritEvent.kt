package com.example.dicodingeventaplication.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorit")
data class FavoritEvent(
    @PrimaryKey(autoGenerate = false)
    var id: Int? = null,
    var name: String? = null,
    var imgLogo: String? = null,
    var imgCover: String? = null,
    var summary: String? = null,
    var category: String? = null,
    var ownerName: String? = null,
    var cityName: String? = null,
    var beginTime: String? = null,
    var quota: Int? = null,
    var registranst: Int? = null,
    var url: String? = null,


    @field:ColumnInfo(name = "bookmarked")
    var isBookmarked: Boolean,

    @field:ColumnInfo(name = "isActive")
    var isActive: Int
){
    val formatYear: String?
        get() = beginTime?.split(" ")?.getOrNull(0)

    val formateDate: String?
        get() = formatYear?.split("-")?.getOrNull(2)

    val formatMount: String?
        get() = formatYear?.split("-")?.getOrNull(1)

}
