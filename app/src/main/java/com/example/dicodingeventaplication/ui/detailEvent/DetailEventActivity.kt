package com.example.dicodingeventaplication.ui.detailEvent

import android.os.Bundle
import android.text.Html
import android.text.Spanned
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.dicodingeventaplication.data.respons.DetailEventResponse
import com.example.dicodingeventaplication.databinding.ActivityDetailEventBinding

class DetailEventActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailEventBinding
    private val viewModel: DetailEventViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailEventBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val itemFinishedId = intent.getIntExtra(EXTRA_ID, 0)
        viewModel.listEventData.observe(this){ eventData ->
            setEventData(eventData)
        }
        viewModel.findDetailEvent(itemFinishedId)

        Log.d(TAG, "item id = $itemFinishedId")


    }

    private fun setEventData(eventsItem: DetailEventResponse?){
        val dataTime = eventsItem?.event?.beginTime
        val part = dataTime?.split(" ")
        val date = part?.get(0) // date
        val time = part?.get(1)
        var remainingQuota = 0
        Log.d("res", "setEventData: ${eventsItem == null} ")
        if (eventsItem?.event?.registrants != null)
            remainingQuota = eventsItem.event.quota?.minus(eventsItem.event.registrants) ?: 0

        if (eventsItem?.event != null){
            binding.detailTvOwnerValue.text= eventsItem.event.ownerName
            binding.detailTvJudul.text = eventsItem.event.name
            binding.detailTvTanggal.text = date
            binding.detailTvJam.text = time
            binding.detailTvSisakuotaValue.text = remainingQuota.toString()

            binding.detailTvCategoryValue.text = eventsItem.event.category
            binding.detailTvLocationValue.text = eventsItem.event.cityName
            binding.detailTvKuotaValue.text = eventsItem.event.quota.toString()
            binding.detailTvPendaftarValue.text = eventsItem.event.registrants.toString()

            binding.detailTvSummaryValue.text = eventsItem.event.summary
//            binding.detailTvDescriptionValue.text = eventsItem.event.description?.toSpanned()

            binding.detailTvDescriptionValue.settings.domStorageEnabled = true
            binding.detailTvDescriptionValue.settings.loadsImagesAutomatically = true

            // load html
            if (eventsItem.event.description != null){
                binding.detailTvDescriptionValue.loadDataWithBaseURL(
                    null, // base url
                    eventsItem.event.description,
                    "text/html",
                    "UTF-8",
                    null
                )
            }
            Glide.with(this)
                .load(eventsItem.event.mediaCover)
                .into(binding.detailImgCover)
            Glide.with(this)
                .load(eventsItem.event.imageLogo)
                .into(binding.detailImgHeader)
        }

    }

    private fun String.toSpanned(): Spanned{
        return Html.fromHtml(this, Html.FROM_HTML_MODE_LEGACY)
    }

    companion object{
        const val EXTRA_ID = "extra id"
        private const val TAG = "detailactivity"
    }
}