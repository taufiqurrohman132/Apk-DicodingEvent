package com.example.dicodingeventaplication.ui.home

import android.Manifest
import android.content.Intent
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.dicodingeventaplication.utils.Resource
import com.example.dicodingeventaplication.databinding.FragmentHomeBinding
import com.example.dicodingeventaplication.utils.DialogUtils
import com.example.dicodingeventaplication.ui.detailEvent.DetailEventActivity
import com.example.dicodingeventaplication.ui.search.SearchActivity
import com.example.dicodingeventaplication.viewmodel.EventViewModelFactory
import com.example.dicodingeventaplication.viewmodel.NetworkViewModel
import com.example.dicodingeventaplication.R
import com.example.dicodingeventaplication.data.local.entity.EventEntity
import com.example.dicodingeventaplication.ui.favorite.FavoriteActivity
import com.example.dicodingeventaplication.ui.upcoming.UpcomingFragment
import com.example.dicodingeventaplication.utils.FavoritHelper
import kotlin.math.abs

class HomeFragment : Fragment() {

    private var eventHeader: EventEntity? = null

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val networkViewModel: NetworkViewModel by lazy {
        ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
        )[NetworkViewModel::class.java]
    }

    private val factory: EventViewModelFactory by lazy {
        EventViewModelFactory.getInstance(requireActivity())
    }

    private val homeViewModel: HomeViewModel by viewModels {
        factory
    }

    private var shouldShowPermission = true
    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(requireContext(), "Notifications permission granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Notifications permission rejected", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        setupViewPager()
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // reques permision
        view.post {
            if (Build.VERSION.SDK_INT >= 33) {
                if (shouldShowPermission){
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    shouldShowPermission = false
                    Log.d(TAG, "onViewCreated: permision di panggil")
                }
            }
        }

        binding.homeNestedScroll.scrollTo(0, homeViewModel.scrollY)

        // observer network
        networkViewModel.isInternetAvailible.observe(viewLifecycleOwner){ isAvailible ->
            if (isAvailible){
                if (!homeViewModel.isHeaderSuccess){
                    homeViewModel.startReload()
                    homeViewModel.findImageHeader()
                } else if (!homeViewModel.isUpcomingSuccess){
                    homeViewModel.startReload()
                    homeViewModel.findEventUpcome()
                } else if (!homeViewModel.isFinishedSuccess){
                    homeViewModel.startReload()
                    homeViewModel.findEventFinished()
                }
            }else{
                Log.d(TAG, "onViewCreated: status upcome ${homeViewModel.isUpcomingSuccess}")
                if (!homeViewModel.isHeaderSuccess) {
                    homeViewModel.findImageHeader()
                }
                if (!homeViewModel.isUpcomingSuccess) {
                    homeViewModel.findEventUpcome()
                }
                if (!homeViewModel.isFinishedSuccess) {
                    homeViewModel.findEventFinished()
                }
            }
        }

        // inisialize adapter
        val adapterCorousel = HomeCorouselRVAdaptor(requireActivity(),
            onItemClick = { event ->
                val intent = Intent(requireContext(), DetailEventActivity::class.java)
                intent.putExtra(DetailEventActivity.EXTRA_EVENT, event)
                startActivity(intent)
            },
            onBookmarkClick = {favorit ->
                Log.d(UpcomingFragment.TAG, "onViewCreated: isbookmark ${favorit.isBookmarked}")
                homeViewModel.onFavoritClicked(favorit, !favorit.isBookmarked, System.currentTimeMillis())
            }
        )
        binding.vpItemCorousel.adapter = adapterCorousel

        // finished
        val linearLayout = LinearLayoutManager(requireActivity()).apply {
            initialPrefetchItemCount = 4 // load 4 item sebelum muncul di layar
        }
        binding.apply {
            rvHomeFinished.layoutManager = linearLayout
            rvHomeFinished.isScrollContainer = false
            rvHomeFinished.setItemViewCacheSize(3)
        }

        val adapterFinished = HomeFinishedRVAdapter(requireActivity()) { event ->
            val intent = Intent(requireContext(), DetailEventActivity::class.java)
            intent.putExtra(DetailEventActivity.EXTRA_EVENT, event)
            startActivity(intent)
        }

        binding.rvHomeFinished.adapter = adapterFinished

        val sharedPool = RecyclerView.RecycledViewPool()
        binding.rvHomeFinished.setRecycledViewPool(sharedPool)

        // akses dari view model dan meng observe
        // tiap data di observe
        homeViewModel.headerEvent.observe(viewLifecycleOwner){ event ->
            // memperbarui ketika ada perubahan
            when(event){
                is Resource.Loading -> {
                    if (!homeViewModel.isHeaderSuccess) {
                        binding.apply {
                            homeProgres.visibility = View.VISIBLE
                            homeHeaderRefresh.visibility = View.INVISIBLE
                        }
                    }
                }
                is Resource.Success -> {
                    binding.apply {
                        homeHeaderRefresh.visibility = View.INVISIBLE
                        homeHeaderBtnFavorit.visibility = View.VISIBLE
                        imgpopHeaderHome.foreground = ContextCompat.getDrawable(requireContext(), R.drawable.gradient_top_left)
                    }

                    eventHeader = getImageHeader(event.data)
                    homeViewModel.isHeaderSuccess()
                    binding.homeProgres.isVisible = false

                    FavoritHelper.updateButtonText(eventHeader?.isBookmarked, binding.homeHeaderBtnFavorit)
                }
                is Resource.Error -> {
                    binding.homeProgres.isVisible = false
                    if (!homeViewModel.hasLocalDataFinish.value){
                        binding.apply {
                            imgpopHeaderHome.setImageResource(0)
                            homeHeaderRefresh.visibility = View.VISIBLE
                            homeHeaderBtnFavorit.visibility = View.INVISIBLE
                            imgpopHeaderHome.foreground = null
                        }
                    }
//                    binding.homeHeaderRefresh.isVisible = true
                }
                is Resource.ErrorConection -> {
                    binding.homeProgres.isVisible = false
                    if (!homeViewModel.isHeaderSuccess && !homeViewModel.hasLocalDataFinish.value)
                        binding.homeHeaderRefresh.isVisible = true
                    Log.d(TAG, "onViewCreated: header event data ${event.data}")
                }
                is Resource.Empty -> {
                }

                else -> {}
            }
            Log.d(TAG, "heder: $event")
        }

        homeViewModel.resultEventItemUpcome.observe(viewLifecycleOwner){ event ->
            // memperbarui ketika ada perubahan
            if (!homeViewModel.isReload.value!!){
                when(event){
                    is Resource.Success -> {
                        adapterCorousel.submitList(event.data?.take(5)?.toList()){
                            binding.vpItemCorousel.post {
                                binding.apply {
                                    homeUpcomingSimmmer.visibility = View.INVISIBLE
                                    homeUpcomingSimmmer.stopShimmer()
                                    homeLottieCorousel.visibility = View.INVISIBLE
                                    homeLottieErrorCorousel.visibility = View.INVISIBLE
                                }
                            }
                        }
                        homeViewModel.isUpcomingSuccess()
                    }
                    is Resource.Error -> {
                        adapterCorousel.submitList(emptyList()){
                            binding.vpItemCorousel.post {
                                binding.apply {
                                    homeUpcomingSimmmer.visibility = View.INVISIBLE
                                    homeLottieCorousel.visibility = View.INVISIBLE
                                    homeUpcomingSimmmer.stopShimmer()
                                    if (!homeViewModel.hasLocalDataFinish.value)
                                        homeLottieErrorCorousel.visibility = View.VISIBLE
                                }
                            }
                        }
                    }
                    is Resource.ErrorConection -> {
                        Log.d(TAG, "corousel currrent list = ${adapterCorousel.currentList.isEmpty()}")
                        binding.apply {
                            homeUpcomingSimmmer.visibility = View.INVISIBLE
                            homeUpcomingSimmmer.stopShimmer()
                            homeLottieErrorCorousel.visibility = View.INVISIBLE
                        }

                        if (adapterCorousel.currentList.isEmpty() && !homeViewModel.isUpcomingSuccess && !homeViewModel.hasLocalDataUpcome.value){
                            binding.homeLottieCorousel.visibility = View.VISIBLE
                            Log.d(
                                TAG,
                                "onViewCreated: lottie corou visible ${binding.homeLottieCorousel.isVisible}"
                            )
                        }
                    }
                    else -> {
                    }
                }
            }
            Log.d(TAG, "upcoming: event is empty ${event?.data?.isEmpty()}, is null ${event?.data.isNullOrEmpty()}")
            Log.d(TAG, "upcoming: ${binding.homeUpcomingSimmmer.isShimmerStarted}")
            Log.d(TAG, "upcoming: observe $event")

            Log.d(TAG, "onViewCreated: lottie corousel ${binding.homeLottieCorousel.isVisible}")
        }

        homeViewModel.resultEventItemFinished.observe(viewLifecycleOwner){ event ->
           // memperbarui ketika ada perubahan
            if (!homeViewModel.isReload.value!!){
                when(event){
                    is Resource.Loading -> {
                    }
                    is Resource.Success -> {
                        adapterFinished.submitList(event.data?.toList()){
                            binding.rvHomeFinished.post {
                                binding.apply {
                                    homeFinishedSimmmer.visibility = View.INVISIBLE
                                    homeFinishedSimmmer.stopShimmer()
                                }
                            }
                        }
                        homeViewModel.isFinishedSuccess()
                        Log.d(TAG, "finished: ${event.data}")
                    }
                    is Resource.Error -> {
                        adapterFinished.setError(event.message)
                        adapterFinished.submitList(event.data?.toList()){
                            binding.rvHomeFinished.post {
                                binding.apply {
                                    homeFinishedSimmmer.visibility = View.INVISIBLE
                                    homeFinishedSimmmer.stopShimmer()
                                }
                            }
                        }
                    }
                    is Resource.ErrorConection -> {
                        adapterFinished.setError(event.message)
                        adapterFinished.submitList(event.data?.toList()){
                            binding.rvHomeFinished.post {
                                binding.apply {
                                    homeFinishedSimmmer.visibility = View.INVISIBLE
                                    homeFinishedSimmmer.stopShimmer()
                                }
                            }
                        }
                    }
                    is Resource.Empty -> {
                        binding.apply {
                            homeFinishedSimmmer.visibility = View.INVISIBLE
                            homeFinishedSimmmer.stopShimmer()
                        }
                    }

                    else -> {}
                }
            }
            Log.d(TAG, "finished: $event")
        }

        // observe swip refrss
        homeViewModel.isRefreshing.observe(viewLifecycleOwner){ isRefresh ->
            binding.homeSwipRefresh.isRefreshing = isRefresh
        }

        // loading simmer
        homeViewModel.isReload.observe(viewLifecycleOwner){ isReload ->
            if ((binding.homeLottieCorousel.isVisible || adapterFinished.currentList.any { it == null } || binding.homeLottieErrorCorousel.isVisible) && isReload){
                Log.d(TAG, "onViewCreated: stat simmer")
                Log.d(TAG, "onViewCreated: finished current list ${adapterFinished.currentList}")
                Log.d(TAG, "onViewCreated: finished current list ${ adapterFinished.currentList.any { it == null }}")
                binding.apply {
                    homeHeaderRefresh.isVisible = !isReload
                    homeProgres.isVisible = isReload
                }

                adapterCorousel.submitList(emptyList())
                adapterFinished.submitList(emptyList()){
                    // mulai simmer
                    binding.apply {
                        homeHeaderBtnFavorit.visibility = View.INVISIBLE
                        homeHeaderTvName.text = ""
                        imgpopHeaderHome.foreground = null

                        homeLottieCorousel.visibility = View.INVISIBLE
                        homeLottieErrorCorousel.visibility = View.INVISIBLE

                        homeUpcomingSimmmer.startShimmer()
                        homeFinishedSimmmer.startShimmer()
                        homeUpcomingSimmmer.visibility = View.VISIBLE
                        homeFinishedSimmmer.visibility = View.VISIBLE
                    }
                }
            }
        }

        homeViewModel.dialogNotifError.observe(viewLifecycleOwner){
            it.getContentIfNotHandled()?.let { dialogMessage ->
                DialogUtils.showPopUpErrorDialog(requireActivity(), dialogMessage)
            }
        }

        // swip refres
        binding.apply {
            homeSwipRefresh.setColorSchemeColors(resources.getColor(R.color.biru_tua))
            homeSwipRefresh.setProgressBackgroundColorSchemeColor(resources.getColor(R.color.icon))
            homeSwipRefresh.setProgressViewOffset(true, 0, 200)
        }

        binding.homeSwipRefresh.setOnRefreshListener {
            homeViewModel.startRefreshing()
            homeViewModel.startReload()
            homeViewModel.findImageHeader()
        }

        binding.homeHeaderRefresh.setOnClickListener {
            if (!homeViewModel.isReload.value!!){
                homeViewModel.startRefreshing()
                homeViewModel.startReload()
                homeViewModel.findImageHeader()
            }
        }

        binding.imgHeaderHome.setOnClickListener {
            if (homeViewModel.isHeaderSuccess && eventHeader != null){
                val intent = Intent(requireContext(), DetailEventActivity::class.java)
                intent.putExtra(DetailEventActivity.EXTRA_EVENT, eventHeader)
                startActivity(intent)
            }
        }

        // btn favorit header
        binding.homeHeaderBtnFavorit.setOnClickListener {
            eventHeader?.let{
                Log.d(UpcomingFragment.TAG, "onViewCreated: isbookmark ${eventHeader?.isBookmarked}")
                homeViewModel.onFavoritClicked(eventHeader!!, !eventHeader!!.isBookmarked, System.currentTimeMillis())
                FavoritHelper.updateButtonText(!eventHeader!!.isBookmarked, binding.homeHeaderBtnFavorit)
            }
        }

        binding.sbHome.setOnClickListener {
            val intent = Intent(requireActivity(), SearchActivity()::class.java)
            startActivity(intent)
        }

        binding.btnFavorit.setOnClickListener {
            val intent = Intent(requireActivity(), FavoriteActivity()::class.java)
            startActivity(intent)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG, "onDestroyView: ")
        homeViewModel.scrollY = binding.homeNestedScroll.scrollY
        _binding = null
    }

    private fun getImageHeader(eventData: List<EventEntity?>?): EventEntity? {
        val event = eventData?.let {
            if (it.size > 6) it[6] else null
        }
        Glide.with(requireActivity())
            .load(event?.imgCover)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .override(400, 200)
            .thumbnail(0.50f)
            .into(binding.imgpopHeaderHome)

        binding.homeHeaderTvName.text = event?.name ?: ""

        return event
    }

    private fun setupViewPager(){
        binding.vpItemCorousel.apply {
            clipChildren = false
            clipToPadding = false
            offscreenPageLimit = 3
            (getChildAt(0) as RecyclerView).overScrollMode =
                RecyclerView.OVER_SCROLL_NEVER
        }

        val compositePageTransformer = CompositePageTransformer()
        compositePageTransformer.addTransformer(
            MarginPageTransformer((8 * Resources.getSystem().displayMetrics.density).toInt())
        )

        compositePageTransformer.addTransformer { page, position ->
            val r = 1 - abs(position)
            // Halaman tengah tidak mengalami zoom
            if (position == 0f) {
                // Halaman tengah tetap dengan skala normal
                page.scaleY = 1f
                page.elevation = 8f
                page.translationZ = 8f
            } else {
                // Halaman di kiri atau kanan mendapat efek skala
                if (position < 0) {
                    // Halaman di kiri
                    page.scaleY = 0.85f + r * 0.15f
                    page.elevation = 6f
                    page.translationZ = 6f
                    if (position == -1f) {
                        page.elevation = 2f
                        page.translationZ = 2f
                    }
                } else {
                    // Halaman di kanan
                    page.scaleY = 0.80f + r * 0.20f
                    page.elevation = 6f
                    page.translationZ = 6f
                    if (position == 1f){
                        page.elevation = 4f
                        page.translationZ = 4f
                    }
                }
            }
        }
        binding.vpItemCorousel.setPageTransformer(compositePageTransformer)

    }

    companion object{
        const val FINISHED = 0
        const val UPCOMING = 1
        const val TAG = "home"
    }

}