package com.example.dicodingeventaplication.data.local.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "favorit")
data class FavoritEvent(
    @PrimaryKey(autoGenerate = false)
    var id: String = "",
    var name: String = "",
    var mediaCover: String? = null,

    @field:ColumnInfo(name = "bookmarked")
    var isBookmarked: Boolean
)
