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
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.res.ResourcesCompat
import androidx.core.text.HtmlCompat
import androidx.core.view.WindowCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.dicodingeventaplication.viewmodel.EventViewModelFactory
import com.example.dicodingeventaplication.viewmodel.NetworkViewModel
import com.example.dicodingeventaplication.R
import com.example.dicodingeventaplication.data.repository.DicodingEventRepository
import com.example.dicodingeventaplication.data.remote.model.Event
import com.example.dicodingeventaplication.databinding.ActivityDetailEventBinding
import com.example.dicodingeventaplication.ui.finished.FinishedViewModel
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

    private val itemId: Int by lazy {
        intent.getIntExtra(EXTRA_ID, 0)
    }

//    private val detailRepository: DicodingEventRepository by lazy {
//        DicodingEventRepository(this)
//    }
//    private val viewModel: DetailEventViewModel by lazy {
//        ViewModelProvider(this, EventViewModelFactory(detailRepository, itemId))[DetailEventViewModel::class.java]
//    }

    private val factory: EventViewModelFactory by lazy {
        EventViewModelFactory.getInstance(this)
    }

    private val viewModel: DetailEventViewModel by viewModels {
        factory
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

        networkViewModel.isInternetAvailible.observe(this) { isAvailible ->
            if (isAvailible && !viewModel.isDetailSuccess){
                viewModel.startReload()
                viewModel.findDetailEvent(itemId)
            }
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

        viewModel.listEventData.observe(this){ eventData ->
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
            if (!viewModel.isDetailSuccess){

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
                        viewModel.findDetailEvent(itemId)
                    }
                    snackbar.setAnchorView(binding.detailBtnRegisterNow.id)
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
            viewModel.startReload()
            viewModel.findDetailEvent(itemId)
        }

        binding.detailBtnLottieErorKoneksi.setOnClickListener {
            viewModel.startReload()
            viewModel.findDetailEvent(itemId)
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
        private const val TAG = "detailactivity"
        private const val SCROLL_POSITION = "scrol_position"
        private const val APP_BAR_OFFSET = "app_bar_offset"
    }
}