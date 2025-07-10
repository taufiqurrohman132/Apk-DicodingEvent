package com.example.dicodingeventaplication.data.local.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "favoritEvent")
data class FavoritEventEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "id")
    var id: Int = 0,

    @ColumnInfo(name = "title")
    var title: String? = null,

    @ColumnInfo(name = "imgLogo")
    var imgLogo: String? = null,

    @ColumnInfo(name = "summary")
    var summary: String? = null,

    @ColumnInfo(name = "beginTime")
    var beginTime: String? = null,

    @ColumnInfo(name = "endTime")
    var endTime: String? = null,

    @ColumnInfo(name = "owner")
    var owner: String? = null,

    @field:ColumnInfo(name = "bookmarked")
    var isBookmarked: Boolean,

    @ColumnInfo(name = "status")
    var status: Int? = null,

    @field:ColumnInfo(name = "createAt")
    var createAt: Long? = null
) : Parcelable{
    val formatYear: String?
        get() = beginTime?.split(" ")?.getOrNull(0)

}
