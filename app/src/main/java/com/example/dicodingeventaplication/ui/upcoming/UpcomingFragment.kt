package com.example.dicodingeventaplication.ui.upcoming

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dicodingeventaplication.Resource
import com.example.dicodingeventaplication.data.repository.DicodingEventRepository
import com.example.dicodingeventaplication.data.retrofit.ApiConfig
import com.example.dicodingeventaplication.databinding.FragmentUpcomingBinding
import com.example.dicodingeventaplication.ui.detailEvent.DetailEventActivity
import com.example.dicodingeventaplication.EventViewModelFactory

class UpcomingFragment : Fragment() {

    private var _binding: FragmentUpcomingBinding? = null
    private val binding get() = _binding!!

    private lateinit var upcomingViewModel: UpcomingViewModel
    private lateinit var upcomeRepository: DicodingEventRepository

    private var menuItem: MenuItem? = null

//    private val offsetChangedListener = AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
//        val totalScrollRange = appBarLayout.totalScrollRange
//
//        if (Math.abs(verticalOffset) == totalScrollRange){
//            // jka kolap tamilkan title
////            binding.mainCollapsing.title = resources.getString(R.string.upcoming)
////            binding.mainToolbar.menu. = View.VISIBLE/
//            menuItem?.icon?.setTint(resources.getColor(R.color.black))
//
//        }else{
//            // jka expand
////            binding.mainCollapsing.title = ""
////            binding.upcomingSbCollaps.visibility = View.GONE
//            menuItem?.icon?.setTint(resources.getColor(R.color.white))
//        }
//    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUpcomingBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // menambahkan listerner appbar
//        binding.mainAppbar.addOnOffsetChangedListener(offsetChangedListener)

        // repositori
        val apiService = ApiConfig.getApiService()
        upcomeRepository = DicodingEventRepository(apiService, requireContext())

        // view model factory
        val viewModelFactory = EventViewModelFactory(upcomeRepository)
        upcomingViewModel = ViewModelProvider(this, viewModelFactory)[UpcomingViewModel::class.java]// pengganti get

        val linearLayout = LinearLayoutManager(requireContext())
        binding.rvUpcoming.layoutManager = linearLayout

        val adapterUpcoming = UpcomingRVAdapter(requireContext()){ event ->
            val intent = Intent(requireContext(), DetailEventActivity::class.java)
            intent.putExtra(DetailEventActivity.EXTRA_ID, event.id)
            startActivity(intent)
        }
        binding.rvUpcoming.adapter = adapterUpcoming

        // mulai simmer
        binding.upcomingSimmmer.startShimmer()
        binding.upcomingSimmmer.visibility = View.VISIBLE
        binding.rvUpcoming.visibility = View.GONE

        upcomingViewModel.resultEventItemUpcome.observe(viewLifecycleOwner){ eventList ->
            when(eventList){
                is Resource.Loading -> {
                }
                is Resource.Success -> {
                    binding.upcomingSimmmer.stopShimmer()
                    binding.upcomingSimmmer.visibility = View.GONE
                    binding.rvUpcoming.visibility = View.VISIBLE
                    binding.upcomingSimmmer.animate().alpha(0f).setDuration(300).withEndAction {
                        adapterUpcoming.submitList(eventList.data)
                    }
                }
                is Resource.Error -> {
                    binding.upcomingSimmmer.stopShimmer()
                    binding.upcomingSimmmer.visibility = View.GONE
                    binding.rvUpcoming.visibility = View.VISIBLE

                    adapterUpcoming.submitList(emptyList())
                }
                is Resource.Empty -> {

                }
            }
//            upcomingViewModel.findEventUpcome()

//            val listEvent = mutableListOf<UpcomingItem>()
//            eventList.data?.let { event ->
//                listEvent.addAll(event.map { UpcomingItem.ResultItem(it) })
//            }
//            adapterUpcoming.submitList(listEvent)
//            Handler(Looper.getMainLooper()).postDelayed({
//                val newData = listOf(
//                    UpcomingItem.ResultItem()
//                )
//            })

        }

//        adapterUpcoming.submitList(list)


    }

//    @Deprecated("Deprecated in Java")
//    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
//        inflater.inflate(R.menu.app_bar_menu, menu)
//        menuItem = menu.findItem(R.id.app_search)
//    }

    override fun onDestroyView() {
        super.onDestroyView()
        //hapus listener app bar
//        binding.mainAppbar.removeOnOffsetChangedListener(offsetChangedListener)
        _binding = null
    }

}