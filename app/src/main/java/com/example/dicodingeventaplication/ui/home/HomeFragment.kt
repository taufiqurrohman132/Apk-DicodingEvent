package com.example.dicodingeventaplication.ui.home

import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import com.example.dicodingeventaplication.data.respons.EventItem
import com.example.dicodingeventaplication.databinding.FragmentHomeBinding
import com.example.dicodingeventaplication.ui.detailEvent.DetailEventActivity
import com.example.dicodingeventaplication.ui.search.SearchActivity
import kotlin.math.abs

class HomeFragment : Fragment() {
    private val homeViewModel: HomeViewModel by viewModels()

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

        // list finished
        val linearLayout = LinearLayoutManager(requireActivity())
        binding.rvImgVertikalHome.layoutManager = linearLayout
        binding.rvImgVertikalHome.isScrollContainer = false

        // akses dari view model dan meng observe
        // tiap data di observe
        homeViewModel.listEventData.observe(viewLifecycleOwner){ listEventItem ->
            setEventData(listEventItem)// memperbarui ketika ada perubahan
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
    private fun setEventData(eventData: List<EventItem>){
        // inisialize adapter n click
        val adapter = HomeFinishedRVAdapter(requireContext()){ event ->
            val intent = Intent(requireContext(), DetailEventActivity::class.java)
            intent.putExtra(DetailEventActivity.EXTRA_ID, event.id)
            startActivity(intent)
        }
        adapter.submitList(eventData)
        binding.rvImgVertikalHome.adapter = adapter

        val adapterCorousel = HomeCorouselRVAdaptor(requireContext())
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

}