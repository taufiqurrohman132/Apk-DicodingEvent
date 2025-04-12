package com.example.dicodingeventaplication.ui.search

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.view.ContextThemeWrapper
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.R
import com.example.dicodingeventaplication.data.remote.model.EventItem
import com.example.dicodingeventaplication.databinding.ItemSearchResultBinding
import java.util.concurrent.Executors

class SearchResultRVAdapter(
    private val context: Context,
    private val onItemClick: (EventItem) -> Unit,
    private val textColor: Int = Color.BLACK,
    private val theme: Int = R.style.ThemeOverlay_MaterialComponents_Light
) : ListAdapter<EventItem, SearchResultRVAdapter.ItemViewHolder>(
    AsyncDifferConfig.Builder(object : DiffUtil.ItemCallback<EventItem>() {
        override fun areItemsTheSame(oldItem: EventItem, newItem: EventItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: EventItem, newItem: EventItem): Boolean {
            return oldItem == newItem
        }
    })
        .setBackgroundThreadExecutor(Executors.newSingleThreadExecutor())
        .build()
) {
    inner class ItemViewHolder(private val item: ItemSearchResultBinding) : ViewHolder(item.root) {
        fun bind(eventItem: EventItem, onClick: (EventItem) -> Unit){
            // inisialize ui
            item.searchTvJudul.text = eventItem.name
            item.searchTvJudul.setTextColor(textColor)
            Glide.with(context)
                .load(eventItem.imageLogo)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .override(100, 100)
                .thumbnail(0.25f)
                .into(item.reulstImgLogo)
            itemView.setOnClickListener { onClick(eventItem) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        // theme
        val themeContext = ContextThemeWrapper(context, theme)
        val inflater = ItemSearchResultBinding.inflate(LayoutInflater.from(themeContext), parent, false)
        return ItemViewHolder(inflater)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val event = getItem(position)
        holder.bind(event, onItemClick)
    }
}