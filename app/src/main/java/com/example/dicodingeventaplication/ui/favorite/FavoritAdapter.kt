package com.example.dicodingeventaplication.ui.favorite

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.dicodingeventaplication.R
import com.example.dicodingeventaplication.data.local.entity.FavoritEvent
import com.example.dicodingeventaplication.data.remote.model.EventItem
import com.example.dicodingeventaplication.databinding.ItemHomeFinishedBinding
//import com.example.dicodingeventaplication.ui.home.HomeFinishedRVAdapter.Companion.TAG

class FavoritAdapter(
    private val context: Context,
    private val onItemClick: (FavoritEvent) -> Unit,
    private val onDelete: (FavoritEvent) -> Unit
) : ListAdapter<FavoritEvent, FavoritAdapter.ItemViewHolder>(DIFF_CALLBACK) {
    inner class ItemViewHolder(private val binding: ItemHomeFinishedBinding) : ViewHolder(binding.root) {
        fun bind(favorit: FavoritEvent?){
//            binding.tvJudulItemVer.text = favorit.name
//            Glide.with(context)
//                .load(favorit.imgLogo)
//                .into(binding.imgItemVer)
//
//            itemView.setOnClickListener {
//                onItemClick(favorit)
//            }

            if (favorit != null){
                binding.tvOwnerItemVer.text = favorit.ownerName
                binding.tvJudulItemVer.text = favorit.name
                binding.tvWaktuItemVer.text = favorit.formatYear
                binding.tvSummaryItemVer.text = favorit.summary
                binding.homeTvStatusItemVer.text = context.resources.getString(R.string.finished)

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
            } else {
//                item.tvHomeFinishedError.text = errorMessage
//                Log.d(TAG, "bind: $errorMessage")

            }
        }
    }

    companion object{
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<FavoritEvent>() {
            override fun areItemsTheSame(
                oldItem: FavoritEvent,
                newItem: FavoritEvent
            ): Boolean {
                return oldItem.id == newItem.id && oldItem.isBookmarked == newItem.isBookmarked
            }

            override fun areContentsTheSame(
                oldItem: FavoritEvent,
                newItem: FavoritEvent
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
    }

    // remove
    fun removeItem(position: Int){
        val currentList = currentList.toMutableList()
        currentList.removeAt(position)
        submitList(currentList)
    }

}