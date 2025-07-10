package com.example.dicodingeventaplication.utils

import android.util.Log
import android.widget.ImageButton
import androidx.core.content.ContextCompat
import com.example.dicodingeventaplication.R
import com.example.dicodingeventaplication.data.local.entity.EventEntity
import com.example.dicodingeventaplication.data.local.entity.FavoritEventEntity
import com.example.dicodingeventaplication.data.repository.DicodingEventRepository
import com.example.dicodingeventaplication.ui.upcoming.UpcomingFragment
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

object FavoritHelper {
    fun togleFavorit(
        scope: CoroutineScope,
        repository: DicodingEventRepository,
        event: EventEntity,
        isBookmarked: Boolean,
        createAt: Long
    ){
        scope.launch {
            val favorit = event.toFavoritEntity(createAt)
            if (isBookmarked){
                Log.d("toglefavorit", "togleFavorit: insert favorit")
                repository.insertFavorit(favorit)
            }else{
                Log.d("toglefavorit", "togleFavorit: delete favorit")
                repository.deleteFavorit(favorit)
            }
            repository.setFavoritBookmark(event, isBookmarked, createAt)
        }
    }

    // maapping
    private fun EventEntity.toFavoritEntity(createAt: Long): FavoritEventEntity{
        return FavoritEventEntity(
            id = this.id,
            title = this.name,
            summary = this.summary,
            imgLogo = this.imgLogo,
            isBookmarked = this.isBookmarked,
            beginTime = this.beginTime,
            endTime = this.endTime,
            owner = this.ownerName,
            status = this.isActive,
            createAt = createAt
        )
    }

    fun updateIcon(event: EventEntity?, icon: ImageButton?){
        if (event != null && icon != null){
            if (event.isBookmarked){
                icon.setImageDrawable(ContextCompat.getDrawable(icon.context, R.drawable.ic_favorit))
            } else {
                icon.setImageDrawable(ContextCompat.getDrawable(icon.context, R.drawable.ic_favorit_not))
            }
            Log.d(UpcomingFragment.TAG, "updateIcon: ${event.isBookmarked}")
        }
    }

    fun updateButtonText(isBookmarked: Boolean?, button: MaterialButton?){
        if (isBookmarked != null && button != null){
            button.text = if (isBookmarked){
                button.context.getString(R.string.favorited)
            } else {
                button.context.getString(R.string.favorit_now)
            }
            Log.d(UpcomingFragment.TAG, "updateIcon: $isBookmarked")
        }
    }

}