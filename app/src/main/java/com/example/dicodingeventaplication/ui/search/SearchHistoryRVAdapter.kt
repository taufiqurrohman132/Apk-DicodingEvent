package com.example.dicodingeventaplication.ui.search

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.dicodingeventaplication.data.remote.model.EventItem
import com.example.dicodingeventaplication.databinding.ItemSearchHistoryBinding
import java.util.concurrent.Executors

class SearchHistoryRVAdapter(
    private val context: Context,
    private val onItemClick: (EventItem) -> Unit,
    private val onDeleteClickItem: (EventItem) -> Unit

) : ListAdapter<EventItem, SearchHistoryRVAdapter.HistoryViewHolder>(
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

    inner class HistoryViewHolder(private val item: ItemSearchHistoryBinding) : ViewHolder(item.root) {
        fun bind(eventItem: EventItem, onClick: (EventItem) -> Unit ){//onDeleteClickItem: (EventItem) -> Unit
            item.historyTvJudul.text = eventItem.name
            item.btnDelete.setOnClickListener {
                onDeleteClickItem(eventItem)
            }
            itemView.setOnClickListener {
                onClick(eventItem)
            }
            Glide.with(context)
                .load(eventItem.imageLogo)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .override(100, 100)
                .into(item.historyImgLogo)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val inflater = ItemSearchHistoryBinding.inflate(LayoutInflater.from(context), parent, false)
        return HistoryViewHolder(inflater)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val event = getItem(position)
        holder.bind(event, onItemClick)
    }
}