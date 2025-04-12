package com.example.dicodingeventaplication.utils

import android.widget.ImageButton
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import com.example.dicodingeventaplication.R
//import com.example.dicodingeventaplication.data.FavoritUseCase
import com.example.dicodingeventaplication.data.local.entity.FavoritEvent
import com.example.dicodingeventaplication.data.remote.model.EventItem
import com.example.dicodingeventaplication.data.repository.DicodingEventRepository
//import com.example.dicodingeventaplication.data.repository.FavoritEventRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

object FavoritHelper {
    fun togleFavorit(
        scope: CoroutineScope,
        repository: DicodingEventRepository,
        favorit: FavoritEvent,
        isBookmarked: Boolean
    ){
        scope.launch {
            repository.setFavoritBookmark(favorit, isBookmarked)
        }
    }

    fun updateIcon(event: FavoritEvent, icon: ImageButton){
        if (event.isBookmarked){
            icon.setImageDrawable(ContextCompat.getDrawable(icon.context, R.drawable.ic_favorit))
        } else {
            icon.setImageDrawable(ContextCompat.getDrawable(icon.context, R.drawable.ic_favorit_not))
        }
    }

//    fun getFavoritLiveData(): LiveData<List<FavoritEvent>> =
//        .getFavorit()
}