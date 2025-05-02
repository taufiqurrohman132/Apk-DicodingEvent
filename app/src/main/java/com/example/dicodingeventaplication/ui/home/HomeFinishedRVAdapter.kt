package com.example.dicodingeventaplication.ui.home

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.dicodingeventaplication.R
import com.example.dicodingeventaplication.data.local.entity.EventEntity
import com.example.dicodingeventaplication.databinding.ItemHomeFinishedBinding
import java.util.concurrent.Executors

class HomeFinishedRVAdapter(
    private val context: Context,
    private val onItemClick: (EventEntity) -> Unit
) : ListAdapter<EventEntity, HomeFinishedRVAdapter.FinishedViewHolder>(
    AsyncDifferConfig.Builder(object : DiffUtil.ItemCallback<EventEntity>() {
        override fun areItemsTheSame(oldItem: EventEntity, newItem: EventEntity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: EventEntity, newItem: EventEntity): Boolean {
            return oldItem == newItem
        }

    })
        .setBackgroundThreadExecutor(Executors.newSingleThreadExecutor())
        .build()
) {
    private var errorMessage: String? = null

    inner class FinishedViewHolder(private val item: ItemHomeFinishedBinding) : ViewHolder(item.root) {
        // inisialisasi data
        fun bind(eventsItem: EventEntity?){
            item.tvHomeFinishedError.isVisible = eventsItem == null
            item.itemHomeFinishedItem.isVisible = eventsItem != null

            item.swipLayout.isEnabledSwipe = false

            if (eventsItem != null){
                item.tvOwnerItemVer.text = eventsItem.ownerName
                item.tvJudulItemVer.text = eventsItem.name
                item.tvWaktuItemVer.text = eventsItem.formatYear
                item.tvSummaryItemVer.text = eventsItem.summary
                item.homeTvStatusItemVer.text = context.resources.getString(R.string.finished)

                Glide.with(context)
                    .load(eventsItem.imgLogo)
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .placeholder(R.drawable.placeholder_view_vector)
                    .override(200, 200)
                    .thumbnail(0.50f)
                    .into(item.imgItemVer)

                item.dragItem.setOnClickListener {
                    onItemClick(eventsItem)
                }
            } else {
                item.tvHomeFinishedError.text = errorMessage
                Log.d(TAG, "bind: $errorMessage")
            }
        }
    }

    fun setError(message: String?){
        errorMessage = message
        Log.d(TAG, "mesage: $errorMessage")
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FinishedViewHolder {
        val inflater = ItemHomeFinishedBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FinishedViewHolder(inflater)
    }

    override fun onBindViewHolder(holder: FinishedViewHolder, position: Int) {
        val event = getItem(position)
        holder.bind(event)
        Log.d(TAG, "bindv: $errorMessage")
        Log.d(TAG, "bindv itemcount: ${super.getItemCount()}")
    }

    companion object{
        private const val TAG = "adapterfinishHome"
    }
}