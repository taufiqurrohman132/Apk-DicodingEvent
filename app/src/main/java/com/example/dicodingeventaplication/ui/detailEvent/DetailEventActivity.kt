package com.example.dicodingeventaplication.ui.detailEvent

import android.animation.ObjectAnimator
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.text.HtmlCompat
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.dicodingeventaplication.viewmodel.EventViewModelFactory
import com.example.dicodingeventaplication.viewmodel.NetworkViewModel
import com.example.dicodingeventaplication.R
import com.example.dicodingeventaplication.data.local.entity.EventEntity
import com.example.dicodingeventaplication.data.remote.model.Event
import com.example.dicodingeventaplication.databinding.ActivityDetailEventBinding
import com.example.dicodingeventaplication.utils.DialogUtils
import com.example.dicodingeventaplication.utils.Resource
import com.example.dicodingeventaplication.utils.TimeUtils
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.snackbar.Snackbar

class DetailEventActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailEventBinding

    private val networkViewModel: NetworkViewModel by lazy {
        ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        )[NetworkViewModel::class.java]
    }

    private val factory: EventViewModelFactory by lazy {
        EventViewModelFactory.getInstance(this)
    }

    private val viewModel: DetailEventViewModel by viewModels {
        factory
    }

    private var isPopUpShowing = false

    private var isExpanned = true

    private var appBarOffset = 0
    private var previewsProgress = -1

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

        val event = intent.getParcelableExtra<EventEntity>(EXTRA_EVENT)
        var isLaunch = true

        binding.detailSimmmerDes.startShimmer()
        binding.detailSimmmerHeader.startShimmer()
        binding.detailSimmmerDes.visibility = View.VISIBLE
        binding.detailSimmmerHeader.visibility = View.VISIBLE

        //buat status bar custom
        WindowCompat.setDecorFitsSystemWindows(window, false)

        networkViewModel.isInternetAvailible.observe(this) { isAvailible ->
            isLaunch = false
            if (isAvailible && !viewModel.isDetailSuccess){
                viewModel.startReload()
                Log.d(TAG, "onCreate: internet dipanggil ")
                viewModel.findDetailEvent(event?.id ?: 0)
            } else if (!isAvailible)
                Log.d(TAG, "onCreate: no internet dipanggil ")
                viewModel.findDetailEvent(event?.id ?: 0)
        }

        // akses find detail ketika pertama launch
        if (isLaunch){
            Log.d(TAG, "onCreate: instance dipanggil ")
            viewModel.findDetailEvent(event?.id ?: 0)
        }

        var url: String? = null

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

        viewModel.eventData.observe(this){ eventData ->
            if (!viewModel.isReload.value!!){
                when(eventData){
                    is Resource.Success ->{
                        setEventData(eventData.data)
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

        // refresh
        viewModel.isRefresing.observe(this){ isRefresh ->
            binding.detailSwipRefresh.isRefreshing = isRefresh
        }

        viewModel.isReload.observe(this){ isReload ->
            if (!viewModel.isDetailSuccess && isReload){
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
            isPopUpShowing = true
            if (!viewModel.isDetailSuccess){
                if (isPopUpShowing) return@observe

                it.getContentIfNotHandled()?.let { mesage ->
                    val snackbar = Snackbar.make(window.decorView.rootView, mesage, Snackbar.LENGTH_INDEFINITE)
                    val snackbarView = snackbar.view

                    val background = GradientDrawable()
                    background.cornerRadius = 40f

                    snackbarView.background = background
                    snackbar.setActionTextColor(resources.getColor(R.color.kuning,null))
                    snackbar.setBackgroundTint(resources.getColor(R.color.ungu_neon, null))
                    snackbar.setTextColor(resources.getColor(R.color.white, null))
                    snackbar.setAction("Try Again"){
                        viewModel.startReload()
                        viewModel.findDetailEvent(event?.id ?: 0)
                        isPopUpShowing = false
                    }
                    snackbar.setAnchorView(binding.detailBtnRegisterNow.id)
                    snackbar.show()
                }
            }
        }

        Log.d(TAG, "item id = ${event?.id}")
        Log.d(TAG, "item = $event")

        // swip
        binding.detailSwipRefresh.setColorSchemeColors(ContextCompat.getColor(this, R.color.biru_tua))
        binding.detailSwipRefresh.setProgressBackgroundColorSchemeColor(ContextCompat.getColor(this, R.color.icon))
        binding.detailSwipRefresh.setProgressViewOffset(true, 0, 200)

        binding.detailSwipRefresh.setOnRefreshListener {
            viewModel.startRefreshing()
            viewModel.startReload()
            viewModel.findDetailEvent(event?.id ?: 0)
        }

        binding.detailBtnLottieErorKoneksi.setOnClickListener {
            viewModel.startReload()
            viewModel.findDetailEvent(event?.id ?: 0)
        }

        binding.detailBtnBack.setOnClickListener {
            finish()
        }

        binding.detailBtnRegisterNow.setOnClickListener {
            if (url.isNullOrEmpty()){
                DialogUtils.showPopUpErrorDialog(it.context, "Failed to load URL. Check your internet connection.")

            } else{
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                Log.d(TAG, "onCreate: url $url")
                try {
                    startActivity(intent)
                } catch (e: Exception){
                    val snackbar = Snackbar.make(window.decorView.rootView, "Cannot open link.", Snackbar.LENGTH_LONG)
                    val snackbarView = snackbar.view

                    val background = GradientDrawable()
                    background.cornerRadius = 20f

                    snackbarView.background = background
                    snackbar.setBackgroundTint(resources.getColor(R.color.ungu_neon, null))
                    snackbar.setTextColor(resources.getColor(R.color.white, null))
                    snackbar.setAnchorView(binding.detailBtnRegisterNow.id)
                    snackbar.show()
                }

            }
        }

        binding.detailBtnAddFavorit.setOnClickListener {
            if (event != null) {
                viewModel.onFavoritClicked(event, System.currentTimeMillis())
                Log.d(TAG, "onCreate: add favorit clicked event is ${event.isBookmarked}")
            }else{
                Log.d(TAG, "onCreate: add favorit event null")
                Toast.makeText(this, "Event tidak Tersedia", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.getFavoritById(event?.id ?: 0).observe(this){ isFavorit ->
            binding.detailBtnAddFavorit.text = if (isFavorit.isBookmarked){
                resources.getString(R.string.unfavorit_now)
            }else{
                resources.getString(R.string.add_to_favorit)
            }

            binding.detailBtnAddFavorit.icon = if (isFavorit.isBookmarked){
                ContextCompat.getDrawable(this, R.drawable.ic_favorit)
            }else{
                null
            }
            Log.d(TAG, "onCreate: add favorit click is favorit ${isFavorit.isBookmarked}")
        }
    }

    private fun setEventData(eventsItem: Event?){

        var remainingQuota = 0
        if (eventsItem?.registrants != null)
            remainingQuota = eventsItem.quota?.minus(eventsItem.registrants) ?: 0

        if (eventsItem != null){
            binding.detailTvOwnerValue.text= eventsItem.ownerName
            binding.detailTvJudul.text = eventsItem.name
            binding.detailTvTanggal.text = eventsItem.formatYear
            binding.detailTvJam.text = resources.getString(R.string.time_value, eventsItem.formatBeginTime, eventsItem.formatEndTime)
            binding.detailTvSisakuotaValue.text = remainingQuota.toString()

            // remaining quota
            binding.detailProgres.max = eventsItem.quota ?: 0
//            // Set progress ke 0 untuk awal animasi
            Log.d(TAG, "setEventData: detail progres  ${binding.detailProgres.progress}")

            // jalankan animasi dari 0 ke jumlah registrants
            val targetProgress = eventsItem.registrants ?: 0
//            val before
            // biar tidak inisial ulang progres
            if (targetProgress != previewsProgress){
                Log.d(TAG, "setEventData: progress regris ")
                binding.detailProgres.progress = 0
                val animatorRemainingProgress = ObjectAnimator.ofInt(binding.detailProgres, "progress",0, targetProgress)
                animatorRemainingProgress.duration = 1000
                animatorRemainingProgress.interpolator = DecelerateInterpolator() // efek Perlambatan
                animatorRemainingProgress.start()

                previewsProgress = targetProgress
            }

            // event status
            val eventIsFinished = TimeUtils.isEventFinished(eventsItem.endTime.toString())
            val (statusText, statusColor) = if (eventIsFinished) {
                R.string.finished to Color.RED
            } else {
                R.string.upcoming to Color.GREEN
            }

            binding.detailTvStatusValue.apply {
                text = resources.getString(statusText)
                setTextColor(statusColor)
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
        const val EXTRA_EVENT = "extra objek"
        private const val TAG = "detailactivity"
        private const val SCROLL_POSITION = "scrol_position"
        private const val APP_BAR_OFFSET = "app_bar_offset"
    }
}