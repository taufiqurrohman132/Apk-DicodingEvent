package com.example.dicodingeventaplication.ui.upcoming

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.dicodingeventaplication.data.local.entity.EventEntity
import com.example.dicodingeventaplication.utils.TimeUtils
import com.example.dicodingeventaplication.databinding.ItemUpcomingBinding
import com.example.dicodingeventaplication.utils.FavoritHelper

class UpcomingRVAdapter(
    private val context: Context,
    private val onItemClick: (EventEntity) -> Unit,
    private val onBookmarkClick: (EventEntity) -> Unit
) : ListAdapter<EventEntity, UpcomingRVAdapter.ItemViewHolder>(DIFF_CALLBACK) {
    inner class ItemViewHolder(val binding: ItemUpcomingBinding) : ViewHolder(binding.root) {
        // inisialize data
        fun bind(eventItem: EventEntity ){
            binding.apply {
                upcomeTvTgl.text = eventItem.formateDate
                upcomeTvBulan.text = TimeUtils.getMount(eventItem.formatMount)
                upcomingTvJudulItem.text = eventItem.name
                upcomingTvSummaryItem.text = eventItem.summary
                upcomingTvOwnerItem.text = eventItem.ownerName
                upcomingTvSisakuotaItem.text = (eventItem.quota?.minus(
                    eventItem.registranst ?: 0
                )).toString()
            }

            Glide.with(context)
                .load(eventItem.imgLogo)
                .into(
                    binding.upcomingImgitem
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

        val ivBookmark = holder.binding.upcomingBtnFavorit
        FavoritHelper.updateIcon(event, ivBookmark)

        ivBookmark.setOnClickListener {
            onBookmarkClick(event)
        }
    }

    companion object{
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<EventEntity>() {
            override fun areItemsTheSame(
                oldItem: EventEntity,
                newItem: EventEntity
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: EventEntity,
                newItem: EventEntity
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}
