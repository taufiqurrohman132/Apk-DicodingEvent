package com.example.dicodingeventaplication.ui.detailEvent

import android.animation.ObjectAnimator
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.res.ResourcesCompat
import androidx.core.text.HtmlCompat
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.dicodingeventaplication.EventViewModelFactory
import com.example.dicodingeventaplication.R
import com.example.dicodingeventaplication.data.repository.DicodingEventRepository
import com.example.dicodingeventaplication.data.respons.Event
import com.example.dicodingeventaplication.databinding.ActivityDetailEventBinding
import com.example.dicodingeventaplication.utils.DialogUtils
import com.example.dicodingeventaplication.utils.Resource
import com.example.dicodingeventaplication.utils.TimeUtils
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.snackbar.Snackbar

class DetailEventActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailEventBinding

    private val itemId: Int by lazy {
        intent.getIntExtra(EXTRA_ID, 0)
    }

    private val detailRepository: DicodingEventRepository by lazy {
        DicodingEventRepository(this)
    }
    private val viewModel: DetailEventViewModel by lazy {
        ViewModelProvider(this, EventViewModelFactory(detailRepository, itemId))[DetailEventViewModel::class.java]
    }

    private var isExpanned = true

    private var appBarOffset = 0

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(
            SCROLL_POSITION,
            binding.detailNestedScroll.scrollY
        )
        outState.putInt(
            APP_BAR_OFFSET,
            appBarOffset
        )
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val scrollPosition = savedInstanceState.getInt(SCROLL_POSITION, 0)
        binding.detailNestedScroll.scrollTo(0, scrollPosition)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailEventBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //buat status bar custom
        WindowCompat.setDecorFitsSystemWindows(window, false)
//        window.decorView.systemUiVisibility = (
//                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
//                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
//                        View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR //icon jadi gelap ketika bg terang
//                )

        var url: String? = null
        val eventActive = intent.getIntExtra(EXTRA_EVENT_ACTIVE, 0)

        if ( savedInstanceState != null) {
            appBarOffset = savedInstanceState.getInt(APP_BAR_OFFSET, 0)
        }

        // pulihkan offset
        binding.detailCoodinator.post {
            val params = binding.mainAppbar.layoutParams as CoordinatorLayout.LayoutParams
            val behavior = params.behavior as? AppBarLayout.Behavior
            behavior?.setTopAndBottomOffset(appBarOffset)
        }

        // listener app bar
        binding.mainAppbar.addOnOffsetChangedListener{ _, verticalOffset ->
            binding.mainAppbar.totalScrollRange
            isExpanned = verticalOffset == 0 // expaned jika offset 0
            appBarOffset = verticalOffset // inisialis untuk simpan posisi app bar
        }

        // mencegah swip jika colap
        binding.detailSwipRefresh.setOnChildScrollUpCallback { _, _ ->
            !isExpanned // cegah swip jika colap
        }

        viewModel.listEventData.observe(this){ eventData ->
            if (!viewModel.isRefresing.value!!){
                when(eventData){
                    is Resource.Success ->{
                        setEventData(eventData.data, eventActive)
                        binding.detailSimmmerDes.stopShimmer()
                        binding.detailSimmmerHeader.stopShimmer()
                        binding.detailSimmmerDes.visibility = View.INVISIBLE
                        binding.detailSimmmerHeader.visibility = View.INVISIBLE
                        binding.detailLottieErrorKoneksi.visibility = View.INVISIBLE
                        binding.detailLottieError.visibility = View.INVISIBLE

                        binding.detaiTvEror.visibility = View.INVISIBLE

                        binding.detailLayout.visibility = View.VISIBLE
                        binding.detailImgHeader.foreground = ResourcesCompat.getDrawable(this.resources, R.drawable.gradient_img, null)
                        url = eventData.data?.link

                        viewModel.markUpcomingSuccess()
                    }
                    is Resource.Error ->{
                        binding.detailSimmmerDes.stopShimmer()
                        binding.detailSimmmerHeader.stopShimmer()
                        binding.detailSimmmerDes.visibility = View.INVISIBLE
                        binding.detailSimmmerHeader.visibility = View.INVISIBLE
                        binding.detailLottieErrorKoneksi.visibility = View.INVISIBLE

                        binding.detaiTvEror.visibility = View.VISIBLE
                        binding.detaiTvEror.text = eventData.message

                        binding.detailLottieError.visibility = View.VISIBLE
                        binding.detailLayout.visibility = View.GONE
                        binding.detailImgHeader.foreground = null

                    }
                    is Resource.ErrorConection ->{
                        binding.detailSimmmerDes.stopShimmer()
                        binding.detailSimmmerHeader.stopShimmer()
                        binding.detailSimmmerDes.visibility = View.INVISIBLE
                        binding.detailSimmmerHeader.visibility = View.INVISIBLE
                        binding.detailLottieError.visibility = View.INVISIBLE

                        if (!viewModel.isDetailSuccess){
                            binding.detaiTvEror.visibility = View.VISIBLE
                            binding.detailLottieErrorKoneksi.visibility = View.VISIBLE
                            binding.detaiTvEror.text = eventData.message
                        }
                    }
                    else ->{

                    }
                }
            }
            Log.d(TAG, "onCreate: event data observe $eventData")
        }
//        viewModel.findDetailEvent(itemId)
        viewModel.isRefresing.observe(this){ isRefresh ->
            binding.detailSwipRefresh.isRefreshing = isRefresh

            if (!viewModel.isDetailSuccess && isRefresh){
                binding.detailSimmmerHeader.startShimmer()
                binding.detailSimmmerDes.startShimmer()
                binding.detailSimmmerHeader.visibility = View.VISIBLE
                binding.detailSimmmerDes.visibility = View.VISIBLE
                binding.detailLayout.visibility = View.INVISIBLE
                binding.detaiTvEror.visibility = View.INVISIBLE
                binding.detailLottieError.visibility = View.INVISIBLE
                binding.detailLottieErrorKoneksi.visibility = View.INVISIBLE
            }
        }

        viewModel.dialogNotifError.observe(this){
            it.getContentIfNotHandled()?.let { dialogMessage ->
                DialogUtils.showPopUpErrorDialog(this, dialogMessage)
            }
        }

        viewModel.snackbarEmpty.observe(this){
            if (!viewModel.isDetailSuccess){

                it.getContentIfNotHandled()?.let { mesage ->
                    val snackbar = Snackbar.make(window.decorView.rootView, mesage, Snackbar.LENGTH_LONG)
                    val snackbarView = snackbar.view

                    snackbarView.setBackgroundColor(resources.getColor(R.color.biru_tua))

                    val text = snackbarView.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
                    text.setTextColor(Color.WHITE)
                    text.textSize = 16f
                    text.typeface = Typeface.DEFAULT_BOLD

                    snackbar.setTextColor(resources.getColor(R.color.ungu_neon))
                    snackbar.setAction("Try Again"){
                        viewModel.startRefreshing()
                        viewModel.findDetailEvent(itemId)
                    }
                    snackbar.show()
                }
            }
        }

        Log.d(TAG, "item id = $itemId")

        binding.detailSwipRefresh.setColorSchemeColors(resources.getColor(R.color.biru_tua))
        binding.detailSwipRefresh.setProgressBackgroundColorSchemeColor(resources.getColor(R.color.white))
        binding.detailSwipRefresh.setProgressViewOffset(true, 0, 200)

        binding.detailSwipRefresh.setOnRefreshListener {
            viewModel.startRefreshing()
            viewModel.findDetailEvent(itemId)
        }

        binding.detailBtnLottieErorKoneksi.setOnClickListener {
            viewModel.startRefreshing()
            viewModel.findDetailEvent(itemId)
        }

        binding.detailBtnBack.setOnClickListener {
            finish()
        }

        binding.detailBtnRegisterNow.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            if (!url.isNullOrEmpty()){
                startActivity(intent)
            } else{
                // tampilakn notif
            }
        }
    }

    private fun setEventData(eventsItem: Event?, eventActive: Int){

        var remainingQuota = 0
        if (eventsItem?.registrants != null)
            remainingQuota = eventsItem.quota?.minus(eventsItem.registrants) ?: 0

        if (eventsItem != null){
            binding.detailTvOwnerValue.text= eventsItem.ownerName
            binding.detailTvJudul.text = eventsItem.name
            binding.detailTvTanggal.text = eventsItem.formatYear
            binding.detailTvJam.text = resources.getString(R.string.remaining_quota_value, eventsItem.formatBeginTime, eventsItem.formatEndTime)
            binding.detailTvSisakuotaValue.text = remainingQuota.toString()

            // remaining quota
            binding.detailProgres.max = eventsItem.quota ?: 0
            binding.detailProgres.progress = eventsItem.registrants ?: 0
            val animatorRemainingProgress = ObjectAnimator.ofInt(binding.detailProgres, "progress",0, eventsItem.registrants ?: 0)
            animatorRemainingProgress.duration = 1000
            animatorRemainingProgress.interpolator = DecelerateInterpolator() // efek Perlambatan
            animatorRemainingProgress.start()

            // event status
            val eventIsFinished = TimeUtils.isEventFinished(eventsItem.endTime.toString())
            binding.detailTvStatusValue.text = when(eventIsFinished){
                true -> {
                    binding.detailTvStatusValue.setTextColor(Color.RED)
                    resources.getString(R.string.finished)
                }
                false -> {
                    binding.detailTvStatusValue.setTextColor(Color.GREEN)
                    resources.getString(R.string.upcoming)
                }
            }

            binding.detailTvCategoryValue.text = eventsItem.category
            binding.detailTvLocationValue.text = eventsItem.cityName
            binding.detailTvKuotaValue.text = eventsItem.quota.toString()
            binding.detailTvPendaftarValue.text = eventsItem.registrants.toString()

            binding.detailTvSummaryValue.text = eventsItem.summary
            val htmlDescription = cleanHtml(eventsItem.description ?: "")
            binding.detailTvDescriptionValue.text = HtmlCompat.fromHtml(
                htmlDescription,
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )
            binding.detailTvDescriptionValue.movementMethod = LinkMovementMethod.getInstance() // kink bisa di klik

            Glide.with(this)
                .load(eventsItem.mediaCover)
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .thumbnail(0.25f)
                .into(binding.detailImgCover)
            Glide.with(this)
                .load(eventsItem.imageLogo)
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .thumbnail(0.25f)
                .into(binding.detailImgHeader)
        }
    }

    private fun cleanHtml(html: String): String{
        return html
            .replace(Regex("<img[^>]*>"), "")
    }

    companion object{
        const val EXTRA_ID = "extra id"
        const val EXTRA_EVENT_ACTIVE = "EXTRA_EVENT_ACTIVE"
        private const val TAG = "detailactivity"
        private const val UPCOMING = 1
        private const val FINISHED = 0
        private const val SCROLL_POSITION = "scrol_position"
        private const val APP_BAR_OFFSET = "app_bar_offset"
    }
}