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
import com.example.dicodingeventaplication.data.local.entity.FavoritEvent
import com.example.dicodingeventaplication.utils.TimeUtils
import com.example.dicodingeventaplication.data.remote.model.EventItem
import com.example.dicodingeventaplication.databinding.ItemHomeCorouselBinding
import java.util.concurrent.Executors

class HomeCorouselRVAdaptor(
    private val context: Context,
    private val onItemClick: (FavoritEvent) -> Unit
) : ListAdapter<FavoritEvent, HomeCorouselRVAdaptor.ItemViewHolder>(
    AsyncDifferConfig.Builder(object : DiffUtil.ItemCallback<FavoritEvent>() {
        override fun areItemsTheSame(oldItem: FavoritEvent, newItem: FavoritEvent): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: FavoritEvent, newItem: FavoritEvent): Boolean {
            return oldItem == newItem
        }

    })
        .setBackgroundThreadExecutor(Executors.newSingleThreadExecutor()) // jalankan di bg tred
        .build()
){
    inner class ItemViewHolder(private val item: ItemHomeCorouselBinding) : ViewHolder(item.root) {
        // inisialisasi data
        fun bind(eventsItem: FavoritEvent?){
            item.itemCorousel.isVisible = eventsItem != null
            item.itemCorouselLottieEmpty.isVisible = eventsItem == null

            if (eventsItem != null){
                item.tvOwnerItem.text= eventsItem.ownerName
                item.tvHeaderItem.text = eventsItem.name
                item.tvDes1Item.text = eventsItem.summary
                item.homeTvBulan.text = TimeUtils.getMount(eventsItem.formatMount)//getMount(eventsItem).uppercase()
                item.homeTvTgl.text = eventsItem.formateDate // tgl
                Glide.with(context)
                    .load(eventsItem.imgLogo)
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .override(400, 400)
                    .thumbnail(0.50f)
                    .into(item.imgItemHori)

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
    }
}