package com.example.dicodingeventaplication.ui.home

import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.ViewModelFactoryDsl
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import com.example.dicodingeventaplication.Resource
import com.example.dicodingeventaplication.data.repository.DicodingEventRepository
import com.example.dicodingeventaplication.data.respons.EventItem
import com.example.dicodingeventaplication.data.retrofit.ApiConfig
import com.example.dicodingeventaplication.databinding.FragmentHomeBinding
import com.example.dicodingeventaplication.ui.DialogUtils
import com.example.dicodingeventaplication.ui.detailEvent.DetailEventActivity
import com.example.dicodingeventaplication.ui.search.SearchActivity
import com.example.dicodingeventaplication.ui.search.viewModel.SearchViewModel
import com.example.dicodingeventaplication.ui.search.viewModel.SearchViewModelFactory
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

        // repositori
        val apiService = ApiConfig.getApiService()
        homeRepository = DicodingEventRepository(apiService, requireContext())

        // view model factory
        val viewModelFactory = SearchViewModelFactory(homeRepository)
        homeViewModel = ViewModelProvider(this, viewModelFactory)[HomeViewModel::class.java]// pengganti get

        // finished
        val linearLayout = LinearLayoutManager(requireActivity())
        binding.rvImgVertikalHome.layoutManager = linearLayout
        binding.rvImgVertikalHome.isScrollContainer = false

//        homeViewModel.findEventFinished()
//        homeViewModel.findEventUpcome()
        // akses dari view model dan meng observe
        // tiap data di observe
        homeViewModel.resultEventItemFinished.observe(viewLifecycleOwner){ event ->
           // memperbarui ketika ada perubahan
            when(event){
                is Resource.Loading -> {

                }
                is Resource.Success -> {
                    updateEventFinished(event.data?.take(5))
                }
                is Resource.Error -> {
                    updateEventFinished(emptyList())
//                    updateList(emptyList(), emptyList())
                    DialogUtils.showPopUpErrorDialog(requireContext(), event.message)
//                    if (queryIsSubmit) {
//                        queryIsSubmit = false
//                    }
                }
                is Resource.Empty -> {

                }

            }
        }
        homeViewModel.resultEventItemUpcome.observe(viewLifecycleOwner){ event ->
            // memperbarui ketika ada perubahan
            when(event){
                is Resource.Loading -> {

                }
                is Resource.Success -> {
                    updateEventUpcome(event.data?.take(5))
                }
                is Resource.Error -> {
                    updateEventUpcome(emptyList())
//                    updateList(emptyList(), emptyList())
                    DialogUtils.showPopUpErrorDialog(requireContext(), event.message)
//                    if (queryIsSubmit) {
//                        queryIsSubmit = false
//                    }
                }
                is Resource.Empty -> {

                }

            }
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

    // inisialize ui
    private fun updateEventFinished(eventData: List<EventItem>?){
        // inisialize adapter n click
        val adapter = HomeFinishedRVAdapter(requireContext()){ event ->
            val intent = Intent(requireContext(), DetailEventActivity::class.java)
            intent.putExtra(DetailEventActivity.EXTRA_ID, event.id)
            startActivity(intent)
        }
        adapter.submitList(eventData)
        binding.rvImgVertikalHome.adapter = adapter
    }

    private fun updateEventUpcome(eventData: List<EventItem>?){
        // inisialize adapter
        val adapterCorousel = HomeCorouselRVAdaptor(requireContext()){ event ->
            val intent = Intent(requireContext(), DetailEventActivity::class.java)
            intent.putExtra(DetailEventActivity.EXTRA_ID, event.id)
            startActivity(intent)
        }
        adapterCorousel.submitList(eventData)
        binding.vpItemCorousel.adapter = adapterCorousel
    }


//    private fun showLoading(isLoading: Boolean) {
//        if (isLoading) {
//            binding.progressBar.visibility = View.VISIBLE
//        } else {
//            binding.progressBar.visibility = View.GONE
//        }
//    }

    companion object{
        const val FINISHED = 0
        const val UPCOMING = 1
    }

}