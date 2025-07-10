package com.example.dicodingeventaplication.ui.favorite

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.dicodingeventaplication.R
import com.example.dicodingeventaplication.data.local.entity.FavoritEventEntity
import com.example.dicodingeventaplication.databinding.ItemHomeFinishedBinding
import com.example.dicodingeventaplication.utils.TimeUtils
import com.zerobranch.layout.SwipeLayout
import com.zerobranch.layout.SwipeLayout.SwipeActionsListener

class FavoritAdapter(
    private val context: Context,
    private val onItemClick: (FavoritEventEntity) -> Unit,
    private val onDelete: (FavoritEventEntity) -> Unit,
    private val onSwipeChanged: () -> Unit
) : ListAdapter<FavoritEventEntity, FavoritAdapter.ItemViewHolder>(DIFF_CALLBACK) {
    val swipeLayouts = mutableListOf<SwipeLayout>()
    var isAllOpenItem = false

    inner class ItemViewHolder( val binding: ItemHomeFinishedBinding) : ViewHolder(binding.root) {
        fun bind(favorit: FavoritEventEntity?){
            if (favorit != null){
                binding.apply {
                    tvOwnerItemVer.text = favorit.owner
                    tvJudulItemVer.text = favorit.title
                    tvWaktuItemVer.text = favorit.formatYear
                    tvSummaryItemVer.text = favorit.summary
                    homeTvStatusItemVer.text = context.resources.getString(R.string.finished)
                }

                Glide.with(context)
                    .load(favorit.imgLogo)
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .override(200, 200)
                    .thumbnail(0.50f)
                    .into(binding.imgItemVer)

                binding.dragItem.setOnClickListener {
                    onItemClick(favorit)
                }

                binding.layoutDelete.setOnClickListener {
                    binding.swipLayout.close() // tutup swip
                    onDelete(favorit)
                }

                // event status
                val eventIsFinished = TimeUtils.isEventFinished(favorit.endTime.toString())
                val (statusText, statusColor) = if (eventIsFinished) {
                    R.string.finished to Color.RED
                } else {
                    R.string.upcoming to Color.GREEN
                }

                binding.homeTvStatusItemVer.apply {
                    text = resources.getString(statusText)
                    setTextColor(statusColor)
                }


            }
        }
    }

    companion object{
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<FavoritEventEntity>() {
            override fun areItemsTheSame(
                oldItem: FavoritEventEntity,
                newItem: FavoritEventEntity
            ): Boolean {
                return oldItem.id == newItem.id && oldItem.isBookmarked == newItem.isBookmarked
            }

            override fun areContentsTheSame(
                oldItem: FavoritEventEntity,
                newItem: FavoritEventEntity
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val itemHolder = ItemHomeFinishedBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(itemHolder)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val event = getItem(position)
        holder.bind(event)

        holder.binding.swipLayout.setOnActionsListener(object : SwipeActionsListener{
            override fun onOpen(direction: Int, isContinuous: Boolean) {
                if (direction == SwipeLayout.LEFT)
                    onSwipeChanged()
            }

            override fun onClose() {
                onSwipeChanged()
            }
        })

        if (!swipeLayouts.contains(holder.binding.swipLayout)){// contains = menhindari duplikat
            swipeLayouts.add(holder.binding.swipLayout)
        }

        Log.d("favorit list", "onBindViewHolder: list $swipeLayouts")
        Log.d("favorit list", "onBindViewHolder: item is open ${holder.binding.swipLayout.isEnabledSwipe}")
    }

    override fun onViewRecycled(holder: ItemViewHolder) {
        super.onViewRecycled(holder)
        if (holder.binding.swipLayout.isRightOpen) {
            holder.binding.swipLayout.close(false) // false = tidak ada animasi
        }
    }
}