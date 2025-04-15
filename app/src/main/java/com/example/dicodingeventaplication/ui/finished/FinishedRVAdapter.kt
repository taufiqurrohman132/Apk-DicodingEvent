package com.example.dicodingeventaplication.ui.finished

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.dicodingeventaplication.data.local.entity.FavoritEvent
import com.example.dicodingeventaplication.data.remote.model.EventItem
import com.example.dicodingeventaplication.databinding.ItemFinishedBinding

class FinishedRVAdapter(
    private val context: Context,
    private val onItemClick: (FavoritEvent) -> Unit
) : ListAdapter<FavoritEvent, FinishedRVAdapter.ItemViewHolder>(DIFF_CALLBACK) {
    inner class ItemViewHolder(private val binding: ItemFinishedBinding) : ViewHolder(binding.root) {
        fun bind(eventItem: FavoritEvent){

            binding.finishedTvJudulItem.text = eventItem.name
            Glide.with(context)
                .load(eventItem.imgLogo)
                .into(binding.finishedImgitem)

            itemView.setOnClickListener { onItemClick(eventItem) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val itemHolder = ItemFinishedBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(itemHolder)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val event = getItem(position)
        holder.bind(event)
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
}
