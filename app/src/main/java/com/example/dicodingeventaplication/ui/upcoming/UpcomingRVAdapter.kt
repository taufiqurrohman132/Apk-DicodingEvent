package com.example.dicodingeventaplication.ui.upcoming

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.dicodingeventaplication.R
import com.example.dicodingeventaplication.data.respons.EventItem
import com.example.dicodingeventaplication.databinding.ItemUpcomingBinding
import com.example.dicodingeventaplication.ui.search.SearchRVAdapter
import okhttp3.internal.isSensitiveHeader

class UpcomingRVAdapter(
    private val context: Context,
    private val onItemClick: (EventItem) -> Unit
) :
    ListAdapter<UpcomingItem, ViewHolder>(DIFF_CALLBACK) {
    inner class ResultViewHolder(item: View) : ViewHolder(item) {
        // inisialize data
        fun bind(eventItem: EventItem){
            val dataTime = eventItem.beginTime
            val part = dataTime?.split(" ")
            val date = part?.get(0)
            val dataPart = date?.split("-")
            val tgl = dataPart?.get(2)

            itemView.findViewById<TextView>(R.id.upcome_tv_tgl).text = tgl
            itemView.findViewById<TextView>(R.id.upcome_tv_bulan).text = "Jan"
            itemView.findViewById<TextView>(R.id.upcoming_tv_judul_item).text = eventItem.name
            itemView.findViewById<TextView>(R.id.upcoming_tv_summary_item).text = eventItem.summary
            itemView.findViewById<TextView>(R.id.upcoming_tv_owner_item).text = eventItem.ownerName
            itemView.findViewById<TextView>(R.id.upcoming_tv_sisakuota_item).text = (eventItem.quota?.minus(
                eventItem.registrants ?: 0
            )).toString()

            Glide.with(context)
                .load(eventItem.imageLogo)
                .into(
                    itemView.findViewById(R.id.upcoming_imgitem)
                )

            itemView.setOnClickListener {
                onItemClick(eventItem)
            }
        }
    }

    class LoadingViewHolder(item: View) : ViewHolder(item)

    override fun getItemViewType(position: Int): Int {
        return when(getItem(position)) {
            is UpcomingItem.ResultItem -> RESULT_ITEM
            is UpcomingItem.Loading -> LOADING
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when(viewType){
            LOADING -> LoadingViewHolder(inflater.inflate(R.layout.item_upcoming_loading, parent, false))
            else -> ResultViewHolder(inflater.inflate(R.layout.item_upcoming, parent, false))
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (val item = getItem(position)){
            is UpcomingItem.ResultItem -> (holder as ResultViewHolder).bind(item.eventItem)
            is UpcomingItem.Loading -> {}
        }
    }

    companion object{
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<UpcomingItem>() {
            override fun areItemsTheSame(
                oldItem: UpcomingItem,
                newItem: UpcomingItem
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: UpcomingItem,
                newItem: UpcomingItem
            ): Boolean {
                return oldItem == newItem
            }
        }

        private val RESULT_ITEM = 0
        private val LOADING = 1
    }
}
