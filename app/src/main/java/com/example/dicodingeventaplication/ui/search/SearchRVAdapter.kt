package com.example.dicodingeventaplication.ui.search

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.dicodingeventaplication.R
import com.example.dicodingeventaplication.data.respons.EventItem

class SearchRVAdapter(
    private val context: Context,
    private val onItemClick: (EventItem) -> Unit,
//    private val onDeleteClickItem: (SearchItem.History) -> Unit,
    private val onClearHistory: () -> Unit
) : ListAdapter<SearchItem, ViewHolder>(DIFF_CALLBACK) {
    inner class ResultViewHolder(private val item: View) : ViewHolder(item){
        fun bind(eventItem: EventItem, onClick: (EventItem) -> Unit){
            // inisialize ui
            item.findViewById<TextView>(R.id.search_tv_judul).text = eventItem.name
            Glide.with(context)
                .load(eventItem.imageLogo)
                .into(item.findViewById(R.id.reulst_img_logo))
            itemView.setOnClickListener { onClick(eventItem) }
        }
    }

    inner class HeaderViewHolder(private val item: View) : ViewHolder(item){
        fun bind(onClearHistory: () -> Unit){
            item.findViewById<TextView>(R.id.tv_clear_history).setOnClickListener {
                onClearHistory()
            }
        }
    }

    inner class HistoryViewHolder(private val item: View) : ViewHolder(item) {
        fun bind(eventItem: EventItem, onClick: (EventItem) -> Unit ){//onDeleteClickItem: (EventItem) -> Unit
            item.findViewById<TextView>(R.id.history_tv_judul).text = eventItem.name
//            item.findViewById<ImageView>(R.id.btn_delete).setOnClickListener {
//                onDeleteClickItem(eventItem)
//            }
            itemView.setOnClickListener {
                onClick(eventItem)
            }
            Glide.with(context)
                .load(eventItem.imageLogo)
                .into(item.findViewById(R.id.history_img_logo))
        }
    }




    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is SearchItem.HistoryItem -> TYPE_HISTORY// 0
            is SearchItem.ResultItem -> TYPE_RESULT// 1
            is SearchItem.Header -> TYPE_HEADER// 2
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        val itemHolder = ItemSearchBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val inflater = LayoutInflater.from(parent.context)
        return when(viewType){
            TYPE_HEADER -> HeaderViewHolder(inflater.inflate(R.layout.item_search_header, parent, false))
            TYPE_HISTORY -> HistoryViewHolder(inflater.inflate(R.layout.item_search_history, parent, false))
            else -> ResultViewHolder(inflater.inflate(R.layout.item_search_result, parent, false))
        }

//        return ResultViewHolder(itemHolder)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
////        holder.bind(event)
//        when(holder){
//            is HistoryViewHolder -> {
//                val historyItem = event as SearchItem.History
//                holder.bind(historyItem, onDeleteClickItem)
//            }
//            is HeaderViewHolder -> {
//                holder.bind(onClearHistory)
//            }
//            is ResultViewHolder -> {
//                val resultItem = event as SearchItem.Result
//                holder.bind(resultItem)
//            }
//        }
        when(item){
            is SearchItem.HistoryItem -> (holder as HistoryViewHolder).bind(item.eventItem, onItemClick)
            is SearchItem.ResultItem -> (holder as ResultViewHolder).bind(item.eventItem, onItemClick)
            is SearchItem.Header -> (holder as HeaderViewHolder).bind(onClearHistory) // di triger dan main
        }
        // klik
//        holder.itemView.setOnClickListener { onItemClick(event) }
    }

    companion object{
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<SearchItem>() {
            override fun areItemsTheSame(
                oldItem: SearchItem,
                newItem: SearchItem
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: SearchItem,
                newItem: SearchItem
            ): Boolean {
                return oldItem == newItem
            }

        }

        private const val TYPE_HISTORY = 0
        private const val TYPE_RESULT = 1
        private const val TYPE_HEADER = 2
    }

}