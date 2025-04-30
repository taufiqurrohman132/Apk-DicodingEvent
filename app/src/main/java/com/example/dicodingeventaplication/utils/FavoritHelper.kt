package com.example.dicodingeventaplication.utils

import android.util.Log
import android.widget.ImageButton
import androidx.core.content.ContextCompat
import com.example.dicodingeventaplication.R
import com.example.dicodingeventaplication.data.local.entity.FavoritEvent
import com.example.dicodingeventaplication.data.repository.DicodingEventRepository
import com.example.dicodingeventaplication.ui.upcoming.UpcomingFragment
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

object FavoritHelper {
    fun togleFavorit(
        scope: CoroutineScope,
        repository: DicodingEventRepository,
        favorit: FavoritEvent,
        isBookmarked: Boolean,
        createAt: Long
    ){
        scope.launch {
            repository.setFavoritBookmark(favorit, isBookmarked, createAt)
        }
    }

    fun updateIcon(event: FavoritEvent?, icon: ImageButton?){
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