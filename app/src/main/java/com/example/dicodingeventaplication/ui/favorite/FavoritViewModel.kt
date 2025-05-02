package com.example.dicodingeventaplication.ui.favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dicodingeventaplication.data.local.entity.EventEntity
import com.example.dicodingeventaplication.data.local.entity.FavoritEventEntity
import com.example.dicodingeventaplication.data.repository.DicodingEventRepository
//import com.example.dicodingeventaplication.data.repository.FavoritEventRepository
import kotlinx.coroutines.launch

class FavoritViewModel(private val favoritRepository: DicodingEventRepository) : ViewModel(){

//    fun getFavoritBookmark() = favoritRepository.getFavoritBookmark()

//    fun deleteFavorit(favorit: EventEntity){
//        viewModelScope.launch {
//            favoritRepository.setFavoritBookmark(favorit, false, 0)
//        }
//    }



    fun getAllFavorit() = favoritRepository.getAllFavorit()

    fun deleteFavorit(favorit: FavoritEventEntity){
        viewModelScope.launch {
            favoritRepository.setFavoritState(favorit.id)
            favoritRepository.deleteFavorit(favorit)
        }
    }

    fun getDetailFromFavorit(favorit: FavoritEventEntity, onResult: (EventEntity) -> Unit){
        viewModelScope.launch {
            val value = favoritRepository.getDetailFromFavorit(favorit)
            onResult(value)
        }
    }

}
