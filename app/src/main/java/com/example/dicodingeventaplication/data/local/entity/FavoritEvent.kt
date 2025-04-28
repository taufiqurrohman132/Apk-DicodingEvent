package com.example.dicodingeventaplication.data.local.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
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
    var isActive: Int,

    @field:ColumnInfo(name = "createAt")
    var createAt: Long? = null


) : Parcelable {
    val formatYear: String?
        get() = beginTime?.split(" ")?.getOrNull(0)

    val formateDate: String?
        get() = formatYear?.split("-")?.getOrNull(2)

    val formatMount: String?
        get() = formatYear?.split("-")?.getOrNull(1)

}
