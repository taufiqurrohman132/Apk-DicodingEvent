package com.example.dicodingeventaplication.ui.upcoming

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.dicodingeventaplication.R
import com.example.dicodingeventaplication.data.respons.EventItem
import com.example.dicodingeventaplication.databinding.ItemUpcomingBinding

class UpcomingRVAdapter(
    private val context: Context,
    private val onItemClick: (EventItem) -> Unit
) : ListAdapter<EventItem, UpcomingRVAdapter.ItemViewHolder>(DIFF_CALLBACK) {
    inner class ItemViewHolder(item: ItemUpcomingBinding) : ViewHolder(item.root) {
        // inisialize data
        fun bind(eventItem: EventItem){
            val dataTime = eventItem.beginTime
            val part = dataTime?.split(" ")
            val date = part?.get(0)
            val dataPart = date?.split("-")
            val tgl = dataPart?.get(2)

            itemView.findViewById<TextView>(R.id.upcome_tv_tgl).text = tgl
            itemView.findViewById<TextView>(R.id.upcome_tv_bulan).text = "Jan"
            itemView.findViewById<TextView>(R.id.upcoming_tv_judul_item).text = eventItem.name
            itemView.findViewById<TextView>(R.id.upcoming_tv_summary_item).text = eventItem.summary
            itemView.findViewById<TextView>(R.id.upcoming_tv_owner_item).text = eventItem.ownerName
            itemView.findViewById<TextView>(R.id.upcoming_tv_sisakuota_item).text = (eventItem.quota?.minus(
                eventItem.registrants ?: 0
            )).toString()

            Glide.with(context)
                .load(eventItem.imageLogo)
                .into(
                    itemView.findViewById(R.id.upcoming_imgitem)
                )

            itemView.setOnClickListener {
                onItemClick(eventItem)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val itemHolder = ItemUpcomingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return  ItemViewHolder(itemHolder)
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
