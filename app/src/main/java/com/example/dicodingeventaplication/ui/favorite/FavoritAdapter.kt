package com.example.dicodingeventaplication.ui.favorite

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.dicodingeventaplication.data.local.entity.FavoritEvent
import com.example.dicodingeventaplication.data.remote.model.EventItem
import com.example.dicodingeventaplication.databinding.ItemHomeFinishedBinding

class FavoritAdapter(
    private val context: Context,
    private val onItemClick: (EventItem) -> Unit
) : ListAdapter<FavoritEvent, FavoritAdapter.ItemViewHolder>(DIFF_CALLBACK) {
    inner class ItemViewHolder(private val binding: ItemHomeFinishedBinding) : ViewHolder(binding.root) {
        fun bind(favorit: FavoritEvent){
            binding.tvJudulItemVer.text = favorit.name
            Glide.with(context)
                .load(favorit.mediaCover)
                .into(binding.imgItemVer)
        }
    }

    companion object{
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<FavoritEvent>() {
            override fun areItemsTheSame(
                oldItem: FavoritEvent,
                newItem: FavoritEvent
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: FavoritEvent,
                newItem: FavoritEvent
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val itemHolder = ItemHomeFinishedBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(itemHolder)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val event = getItem(position)
        holder.bind(event)
    }
}