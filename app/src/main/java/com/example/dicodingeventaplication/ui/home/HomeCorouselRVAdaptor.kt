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
import com.example.dicodingeventaplication.data.respons.EventItem
import com.example.dicodingeventaplication.databinding.ItemHomeCorouselBinding
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.Executors

class HomeCorouselRVAdaptor(
    private val context: Context,
    private val onItemClick: (EventItem) -> Unit
) : ListAdapter<EventItem, HomeCorouselRVAdaptor.ItemViewHolder>(
    AsyncDifferConfig.Builder(object : DiffUtil.ItemCallback<EventItem>() {
        override fun areItemsTheSame(oldItem: EventItem, newItem: EventItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: EventItem, newItem: EventItem): Boolean {
            return oldItem == newItem
        }

    })
        .setBackgroundThreadExecutor(Executors.newSingleThreadExecutor()) // jalankan di bg tred
        .build()
){
    inner class ItemViewHolder(private val item: ItemHomeCorouselBinding) : ViewHolder(item.root) {
        // inisialisasi data
        fun bind(eventsItem: EventItem?){
            item.itemCorousel.isVisible = eventsItem != null
            item.itemCorouselLottieEmpty.isVisible = eventsItem == null

            if (eventsItem != null){
                item.tvOwnerItem.text= eventsItem.ownerName
                item.tvHeaderItem.text = eventsItem.name
                item.tvDes1Item.text = eventsItem.summary
                item.homeTvBulan.text = getMount(eventsItem.formatMount)//getMount(eventsItem).uppercase()
                item.homeTvTgl.text = eventsItem.formateDate // tgl
                Glide.with(context)
                    .load(eventsItem.imageLogo)
                    .diskCacheStrategy(DiskCacheStrategy.DATA)
                    .override(400, 400)
                    .thumbnail(0.25f)
                    .into(item.imgItemHori)

                itemView.setOnClickListener {
                    onItemClick(eventsItem)
                }
            } else { // item tanpa data
//                item.itemCorousel.visibility = View.INVISIBLE
//                item.itemCorouselLottieEmpty.visibility = View.VISIBLE
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

    private fun getMount(date: String?): String{
        val month = arrayOf(
            "JAN",  "FEB",  "MAR",  "APR",  "MEI",  "JUN",
            "JUL",  "AGS",  "SEP",  "OKT",  "NOV",  "DES"
        )

        return try {
            val index = date?.toIntOrNull()?.minus(1) ?: return ""
            if (index in month.indices) month[index] else ""
        }catch (e: Exception){
            return ""
        }
    }

    companion object{
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<EventItem>() {
            override fun areItemsTheSame(
                oldItem: EventItem,
                newItem: EventItem
            ): Boolean {
                return oldItem.id == newItem.id
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