package com.example.dicodingeventaplication.ui.upcoming

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.dicodingeventaplication.data.respons.EventItem
import com.example.dicodingeventaplication.databinding.ItemUpcomingBinding

class UpcomingRVAdapter :
    ListAdapter<String, UpcomingRVAdapter.ItemViewHolder>(DIFF_CALLBACK) {
    inner class ItemViewHolder(item: ItemUpcomingBinding) : ViewHolder(item.root) {
        // inisialize data
        fun bind(eventItem: EventItem){

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val itemHolder = ItemUpcomingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(itemHolder)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {

    }
    companion object{
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<String>() {
            override fun areItemsTheSame(
                oldItem: String,
                newItem: String
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: String,
                newItem: String
            ): Boolean {
                return oldItem == newItem
            }

        }
    }
}
