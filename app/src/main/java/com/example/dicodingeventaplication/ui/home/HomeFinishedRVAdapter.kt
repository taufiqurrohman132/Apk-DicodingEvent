package com.example.dicodingeventaplication.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.dicodingeventaplication.data.respons.EventItem
import com.example.dicodingeventaplication.databinding.ItemHomeVertikalBinding

class HomeFinishedRVAdapter(
    private val context: Context,
    private val onItemClick: (EventItem) -> Unit
) : ListAdapter<EventItem, HomeFinishedRVAdapter.ItemViewHolder>(DIFF_CALLBACK) {

    inner class ItemViewHolder(private val item: ItemHomeVertikalBinding) : ViewHolder(item.root) {
        // inisialisasi data
        fun bind(eventsItem: EventItem){
            val dataTime = eventsItem.beginTime
            val part = dataTime?.split(" ")
            val date = part?.get(0)

            item.tvOwnerItemVer.text = eventsItem.ownerName
            item.tvJudulItemVer.text = eventsItem.name
            item.tvSummaryItemVer.text = eventsItem.summary
            item.homeTvStatusItemVer.text = "Finished"
            item.tvWaktuItemVer.text = date
            Glide.with(context)
                .load(eventsItem.imageLogo)
                .into(item.imgItemVer)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val itemHolder = ItemHomeVertikalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(itemHolder)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val event = getItem(position)
        holder.bind(event)

        // pasang klik listener untuk mengambil data dari item
        holder.itemView.setOnClickListener { onItemClick(event) }
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