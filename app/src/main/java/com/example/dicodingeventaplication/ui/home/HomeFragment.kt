package com.example.dicodingeventaplication.ui.home

import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.dicodingeventaplication.Resource
import com.example.dicodingeventaplication.data.repository.DicodingEventRepository
import com.example.dicodingeventaplication.data.respons.EventItem
import com.example.dicodingeventaplication.data.retrofit.ApiConfig
import com.example.dicodingeventaplication.databinding.FragmentHomeBinding
import com.example.dicodingeventaplication.Utils.DialogUtils
import com.example.dicodingeventaplication.ui.detailEvent.DetailEventActivity
import com.example.dicodingeventaplication.ui.search.SearchActivity
import com.example.dicodingeventaplication.EventViewModelFactory
import com.example.dicodingeventaplication.R
import kotlin.math.abs

class HomeFragment : Fragment() {
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var homeRepository: DicodingEventRepository

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
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
//            page.scaleY = (0.80f + r * 0.20f)

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
                        page.elevation = 0f
                        page.translationZ = 0f
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

        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // mulai simmer
        binding.homeUpcomingSimmmer.visibility = View.VISIBLE
//        binding.vpItemCorousel.visibility = View.INVISIBLE
//        binding.homeUpcomingSimmmer.apply {
//            binding.homeUpcomingSimmmer.visibility = View.VISIBLE
//        }
//        binding.homeUpcomingSimmmer.postDelayed({
//            binding.homeUpcomingSimmmer.visibility = View.INVISIBLE
//            binding.homeLottieCorousel.visibility = View.INVISIBLE
//            binding.homeUpcomingSimmmer.stopShimmer()
//        }, 1000)

        binding.homeFinishedSimmmer.visibility = View.VISIBLE
        binding.homeProgres.visibility = View.VISIBLE


        // blur img
//        Glide.with(requireContext())
//            .load(R.drawable.dbs)
//            .transform(BlurTransformation(25))
//            .into(binding.imgpopHeaderHome)

        // repositori
        val apiService = ApiConfig.getApiService()
        homeRepository = DicodingEventRepository(apiService, requireContext())

        // view model factory
        val viewModelFactory = EventViewModelFactory(homeRepository)
        homeViewModel = ViewModelProvider(this, viewModelFactory)[HomeViewModel::class.java]// pengganti get

        // inisialize adapter
        val adapterCorousel = HomeCorouselRVAdaptor(requireContext()){ event ->
            val intent = Intent(requireContext(), DetailEventActivity::class.java)
            intent.putExtra(DetailEventActivity.EXTRA_ID, event.id)
            startActivity(intent)
        }
        binding.vpItemCorousel.adapter = adapterCorousel

        // finished
        val linearLayout = LinearLayoutManager(requireActivity()).apply {
            initialPrefetchItemCount = 4 // load 4 item sebelum muncul di layar
        }
        binding.rvHomeFinished.layoutManager = linearLayout
        binding.rvHomeFinished.isScrollContainer = false
        binding.rvHomeFinished.setItemViewCacheSize(3)

        val adapterFinished = HomeFinishedRVAdapter(requireContext(),
            onItemClick = { event ->
                val intent = Intent(requireContext(), DetailEventActivity::class.java)
                intent.putExtra(DetailEventActivity.EXTRA_ID, event.id)
                startActivity(intent)}
        )
        binding.rvHomeFinished.adapter = adapterFinished

        val sharedPool = RecyclerView.RecycledViewPool()
        binding.rvHomeFinished.setRecycledViewPool(sharedPool)

        // akses dari view model dan meng observe
        // tiap data di observe
        homeViewModel.imageHeaderUrl.observe(viewLifecycleOwner){ url ->
            Log.d(TAG, "URL gambar setelah rotasi: $url")
            if (!url.isNullOrEmpty()){
                Glide.with(requireContext())
                    .load(url)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .skipMemoryCache(true)
                    .thumbnail(0.25f)
                    .into(binding.imgpopHeaderHome)
            } else Log.e(TAG, "URL gambar kosong setelah rotasi!")
        }
        homeViewModel.headerEvent.observe(viewLifecycleOwner){ event ->
            // memperbarui ketika ada perubahan
            when(event){
                is Resource.Loading -> {

                }
                is Resource.Success -> {
                    binding.homeProgres.visibility = View.INVISIBLE
                    binding.homeHeaderRefresh.visibility = View.INVISIBLE
                }
                is Resource.Error -> {
                    binding.imgpopHeaderHome.setImageResource(0)
                    binding.homeProgres.visibility = View.INVISIBLE
                    binding.homeHeaderRefresh.visibility = View.VISIBLE
                }
                is Resource.ErrorConection -> {
                    binding.homeProgres.visibility = View.INVISIBLE
                    if (event.data != null)
                        binding.homeHeaderRefresh.visibility = View.VISIBLE
                }
                is Resource.Empty -> {
                    binding.homeProgres.visibility = View.INVISIBLE
                }

            }
            Log.d(TAG, "heder: $event")
        }

        homeViewModel.resultEventItemUpcome.observe(viewLifecycleOwner){ event ->
            // memperbarui ketika ada perubahan
            when(event){
                is Resource.Success -> {
                    adapterCorousel.submitList(event.data?.take(5)?.toList()){
                        binding.vpItemCorousel.post {
                            binding.homeUpcomingSimmmer.visibility = View.INVISIBLE
                            binding.homeLottieCorousel.visibility = View.INVISIBLE
                            binding.homeUpcomingSimmmer.stopShimmer()
                            binding.homeLottieErrorCorousel.visibility = View.INVISIBLE
                        }
                    }
                }
                is Resource.Error -> {
                    adapterCorousel.submitList(emptyList()){
                        binding.vpItemCorousel.post {
                            binding.homeUpcomingSimmmer.visibility = View.INVISIBLE
                            binding.homeLottieCorousel.visibility = View.INVISIBLE
                            binding.homeUpcomingSimmmer.stopShimmer()
                            binding.homeLottieErrorCorousel.visibility = View.VISIBLE
                        }
                    }
                    DialogUtils.showPopUpErrorDialog(requireActivity(), event.message)
                }
                is Resource.ErrorConection -> {
                    Log.d(TAG, "corousel currrent list = ${adapterCorousel.currentList.isEmpty()}")
                    DialogUtils.showPopUpErrorDialog(requireActivity(), event.message)
                    binding.homeUpcomingSimmmer.visibility = View.INVISIBLE
                    binding.homeUpcomingSimmmer.stopShimmer()
                    binding.homeLottieErrorCorousel.visibility = View.INVISIBLE

                    if (adapterCorousel.currentList.isEmpty()){
                        binding.homeLottieCorousel.visibility = View.VISIBLE
                    }
                }
                else -> {
                }
            }
            Log.d(TAG, "upcoming: $event")
            Log.d(TAG, "upcoming: ${binding.homeUpcomingSimmmer.isShimmerStarted}")
        }

        homeViewModel.resultEventItemFinished.observe(viewLifecycleOwner){ event ->
           // memperbarui ketika ada perubahan
            when(event){
                is Resource.Loading -> {
                }
                is Resource.Success -> {
                    adapterFinished.submitList(event.data?.toList()){
                        binding.rvHomeFinished.post {
                            binding.homeFinishedSimmmer.visibility = View.INVISIBLE
                            binding.homeFinishedSimmmer.stopShimmer()
                        }
                    }
//                    binding.homeFinishedSimmmer.stopShimmer()
                    Log.d(TAG, "finished: ${event.data}")
                }
                is Resource.Error -> {
                    adapterFinished.setError(event.message)
                    adapterFinished.submitList(event.data?.toList()){
                        binding.rvHomeFinished.post {
                            binding.homeFinishedSimmmer.visibility = View.INVISIBLE
                            binding.homeFinishedSimmmer.stopShimmer()
                        }
                    }
                }
                is Resource.ErrorConection -> {
                    adapterFinished.setError(event.message)
                    adapterFinished.submitList(event.data?.toList()){
                        binding.rvHomeFinished.post {
                            binding.homeFinishedSimmmer.visibility = View.INVISIBLE
                            binding.homeFinishedSimmmer.stopShimmer()
                        }
                    }
                }
                is Resource.Empty -> {
                    binding.homeFinishedSimmmer.visibility = View.INVISIBLE
                    binding.homeFinishedSimmmer.stopShimmer()
                }
            }
            Log.d(TAG, "finished: $event")
        }

        // swip refres
        binding.homeSwipRefresh.setColorSchemeColors(resources.getColor(R.color.biru_tua))
        binding.homeSwipRefresh.setProgressBackgroundColorSchemeColor(resources.getColor(R.color.white))
        binding.homeSwipRefresh.setProgressViewOffset(true, 0, 200)
        binding.homeSwipRefresh.setOnRefreshListener {
            homeViewModel.findImageHeader{
                binding.homeSwipRefresh.isRefreshing = false
            }
        }

        binding.homeHeaderRefresh.setOnClickListener {
            homeViewModel.findImageHeader()

            binding.homeHeaderRefresh.visibility = View.INVISIBLE
            binding.homeProgres.visibility = View.VISIBLE
        }

        binding.sbHome.setOnClickListener {
            val intent = Intent(requireActivity(), SearchActivity()::class.java)
            startActivity(intent)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

//    private fun getImageHeader(eventData: List<EventItem?>?){
//        val event = eventData?.let {
//            if (it.size > 6) it[6] else null
//        }
//
//        Glide.with(requireActivity())
//            .load(event?.mediaCover)
//            .diskCacheStrategy(DiskCacheStrategy.ALL)
//            .thumbnail(0.25f)
//            .into(binding.imgpopHeaderHome)
//    }

    companion object{
        const val FINISHED = 0
        const val UPCOMING = 1
        const val TAG = "home"
    }

}