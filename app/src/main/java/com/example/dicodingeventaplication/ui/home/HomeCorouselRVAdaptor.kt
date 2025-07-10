package com.example.dicodingeventaplication.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.dicodingeventaplication.data.local.entity.EventEntity
import com.example.dicodingeventaplication.utils.TimeUtils
import com.example.dicodingeventaplication.databinding.ItemHomeCorouselBinding
import com.example.dicodingeventaplication.utils.FavoritHelper
import java.util.concurrent.Executors

class HomeCorouselRVAdaptor(
    private val context: Context,
    private val onItemClick: (EventEntity) -> Unit,
    private val onBookmarkClick: (EventEntity) -> Unit
) : ListAdapter<EventEntity, HomeCorouselRVAdaptor.ItemViewHolder>(
    AsyncDifferConfig.Builder(object : DiffUtil.ItemCallback<EventEntity>() {
        override fun areItemsTheSame(oldItem: EventEntity, newItem: EventEntity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: EventEntity, newItem: EventEntity): Boolean {
            return oldItem == newItem
        }

    })
        .setBackgroundThreadExecutor(Executors.newSingleThreadExecutor()) // jalankan di bg tred
        .build()
){
    inner class ItemViewHolder( val binding: ItemHomeCorouselBinding) : ViewHolder(binding.root) {
        // inisialisasi data
        fun bind(eventsItem: EventEntity?){
            binding.apply {
                itemCorousel.isVisible = eventsItem != null
                itemCorouselLottieEmpty.isVisible = eventsItem == null
            }

            if (eventsItem != null){
                binding.apply {
                    tvOwnerItem.text= eventsItem.ownerName
                    tvHeaderItem.text = eventsItem.name
                    tvDes1Item.text = eventsItem.summary
                    homeTvBulan.text = TimeUtils.getMount(eventsItem.formatMount)//getMount(eventsItem).uppercase()
                    homeTvTgl.text = eventsItem.formateDate // tgl
                }
                Glide.with(context)
                    .load(eventsItem.imgLogo)
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .override(400, 400)
                    .thumbnail(0.50f)
                    .into(binding.imgItemHori)

                itemView.setOnClickListener {
                    onItemClick(eventsItem)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val itemHolder = ItemHomeCorouselBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(itemHolder)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val event = getItem(position)

        holder.bind(event)

        val ivBookmark = holder.binding.ivFavorit
        FavoritHelper.updateIcon(event, ivBookmark)

        ivBookmark.setOnClickListener {
            onBookmarkClick(event)
        }
    }
}