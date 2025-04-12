package com.example.dicodingeventaplication.ui.upcoming

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.dicodingeventaplication.data.local.entity.FavoritEvent
import com.example.dicodingeventaplication.utils.TimeUtils
import com.example.dicodingeventaplication.data.remote.model.EventItem
import com.example.dicodingeventaplication.databinding.ItemUpcomingBinding
import com.example.dicodingeventaplication.utils.FavoritHelper

class UpcomingRVAdapter(
    private val context: Context,
    private val onItemClick: (EventItem) -> Unit,
    private val onBookmarkClick: (FavoritEvent) -> Unit
) : ListAdapter<EventItem, UpcomingRVAdapter.ItemViewHolder>(DIFF_CALLBACK) {
    inner class ItemViewHolder(val item: ItemUpcomingBinding) : ViewHolder(item.root) {
        // inisialize data
        fun bind(eventItem: EventItem){
            item.upcomeTvTgl.text = eventItem.formateDate
            item.upcomeTvBulan.text = TimeUtils.getMount(eventItem.formatMount)
            item.upcomingTvJudulItem.text = eventItem.name
            item.upcomingTvSummaryItem.text = eventItem.summary
            item.upcomingTvOwnerItem.text = eventItem.ownerName
            item.upcomingTvSisakuotaItem.text = (eventItem.quota?.minus(
                eventItem.registrants ?: 0
            )).toString()

            Glide.with(context)
                .load(eventItem.imageLogo)
                .into(
                    item.upcomingImgitem
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

        val ivBookmark = holder.item.upcomingBtnFavorit
        FavoritHelper.updateIcon(event, ivBookmark)
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
