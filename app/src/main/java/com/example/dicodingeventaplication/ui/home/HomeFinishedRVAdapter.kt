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

    inner class FinishedViewHolder(private val binding: ItemHomeFinishedBinding) : ViewHolder(binding.root) {
        // inisialisasi data
        fun bind(eventsItem: EventEntity?){
            binding.apply {
                tvHomeFinishedError.isVisible = eventsItem == null
                itemHomeFinishedItem.isVisible = eventsItem != null
                swipLayout.isEnabledSwipe = false
            }


            if (eventsItem != null){
                binding.apply {
                    tvOwnerItemVer.text = eventsItem.ownerName
                    tvJudulItemVer.text = eventsItem.name
                    tvWaktuItemVer.text = eventsItem.formatYear
                    tvSummaryItemVer.text = eventsItem.summary
                    homeTvStatusItemVer.text = context.resources.getString(R.string.finished)
                }

                Glide.with(context)
                    .load(eventsItem.imgLogo)
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .placeholder(R.drawable.placeholder_view_vector)
                    .override(200, 200)
                    .thumbnail(0.50f)
                    .into(binding.imgItemVer)

                binding.dragItem.setOnClickListener {
                    onItemClick(eventsItem)
                }
            } else {
                binding.tvHomeFinishedError.text = errorMessage
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