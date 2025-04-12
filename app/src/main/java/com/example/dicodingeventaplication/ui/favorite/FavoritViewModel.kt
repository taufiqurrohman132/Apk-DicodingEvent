package com.example.dicodingeventaplication.ui.favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dicodingeventaplication.data.local.entity.FavoritEvent
import com.example.dicodingeventaplication.data.repository.DicodingEventRepository
//import com.example.dicodingeventaplication.data.repository.FavoritEventRepository
import kotlinx.coroutines.launch

class FavoritViewModel(private val favoritRepository: DicodingEventRepository) : ViewModel(){

    fun getFavoritBookmark() = favoritRepository.getFavoritBookmark()

    fun deleteFavorit(favorit: FavoritEvent){
        viewModelScope.launch {
            favoritRepository.setFavoritBookmark(favorit, false)
        }
    }

}
