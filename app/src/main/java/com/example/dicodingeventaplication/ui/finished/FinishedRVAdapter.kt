package com.example.dicodingeventaplication.ui.finished

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.dicodingeventaplication.data.respons.EventItem
import com.example.dicodingeventaplication.databinding.ItemFinishedBinding
import kotlin.contracts.contract

class FinishedRVAdapter(
    private val context: Context,
    private val onItemClick: (EventItem) -> Unit
) : ListAdapter<EventItem, FinishedRVAdapter.ItemViewHolder>(DIFF_CALLBACK) {
    inner class ItemViewHolder(private val binding: ItemFinishedBinding) : ViewHolder(binding.root) {
        fun bind(eventItem: EventItem){
//            val dataTime = eventItem.beginTime
//            val part = dataTime?.split(" ")
//            val data = part?.get(0)

            binding.finishedTvJudulItem.text = eventItem.name
//            binding.finishedTvSummaryItem.text = eventItem.summary
//            binding.finishedTvOwnerItem.text = eventItem.ownerName
            Glide.with(context)
                .load(eventItem.imageLogo)
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
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<EventItem>() {
            override fun areItemsTheSame(
                oldItem: EventItem,
                newItem: EventItem
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: EventItem,
                newItem: EventItem
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}
