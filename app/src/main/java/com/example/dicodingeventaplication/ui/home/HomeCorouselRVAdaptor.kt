package com.example.dicodingeventaplication.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.dicodingeventaplication.data.respons.EventItem
import com.example.dicodingeventaplication.databinding.ItemHomeCorouselBinding

class HomeCorouselRVAdaptor(private val context: Context) :
ListAdapter<EventItem, HomeCorouselRVAdaptor.ItemViewHolder>(DIFF_CALLBACK){
    inner class ItemViewHolder(private val item: ItemHomeCorouselBinding) : ViewHolder(item.root) {
        // inisialisasi data
        fun bind(eventsItem: EventItem){
            val dataTime = eventsItem.beginTime
            val part = dataTime?.split(" ")
            val date = part?.get(0) // date
            val partDate = date?.split("-")
            val tgl = partDate?.get(2)

            item.tvOwnerItem.text= eventsItem.ownerName
            item.tvHeaderItem.text = eventsItem.name
            item.tvDes1Item.text = eventsItem.summary
            item.homeTvBulan.text = "JAN"//getMount(eventsItem).uppercase()
            item.homeTvTgl.text = tgl
            Glide.with(context)
                .load(eventsItem.imageLogo)
                .into(item.imgItemHori)
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

    private fun getMount(eventsItem: EventItem): String{
        val partTime = eventsItem.beginTime?.split(" ")
        val date = partTime?.get(0) // date
        val partDate = date?.split("-")
        val bulan = partDate?.get(1)

        return when(bulan){
            "01" -> "jan"
            "02" -> "feb"
            "03" -> "mar"
            "04" -> "apr"
            "05" -> "mei"
            "06" -> "jun"
            "07" -> "jul"
            "08" -> "ags"
            "09" -> "sep"
            "10" -> "okt"
            "11" -> "nov"
            "12" -> "des"
            else -> ""
        }
    }

    companion object{
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<EventItem>() {
            override fun areItemsTheSame(
                oldItem: EventItem,
                newItem: EventItem
            ): Boolean {
                return oldItem == newItem
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