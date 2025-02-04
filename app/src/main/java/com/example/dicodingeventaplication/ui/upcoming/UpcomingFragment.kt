package com.example.dicodingeventaplication.ui.upcoming

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dicodingeventaplication.Resource
import com.example.dicodingeventaplication.data.repository.DicodingEventRepository
import com.example.dicodingeventaplication.data.retrofit.ApiConfig
import com.example.dicodingeventaplication.databinding.FragmentUpcomingBinding
import com.example.dicodingeventaplication.ui.DialogUtils
import com.example.dicodingeventaplication.ui.detailEvent.DetailEventActivity
import com.example.dicodingeventaplication.ui.search.viewModel.SearchViewModelFactory

class UpcomingFragment : Fragment() {

    private var _binding: FragmentUpcomingBinding? = null
    private val binding get() = _binding!!

    private lateinit var upcomingViewModel: UpcomingViewModel
    private lateinit var upcomeRepository: DicodingEventRepository

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

        // repositori
        val apiService = ApiConfig.getApiService()
        upcomeRepository = DicodingEventRepository(apiService, requireContext())

        // view model factory
        val viewModelFactory = SearchViewModelFactory(upcomeRepository)
        upcomingViewModel = ViewModelProvider(this, viewModelFactory)[UpcomingViewModel::class.java]// pengganti get

        val linearLayout = LinearLayoutManager(requireContext())
        binding.rvUpcoming.layoutManager = linearLayout

        val adapterUpcoming = UpcomingRVAdapter(requireContext()){ event ->
            val intent = Intent(requireContext(), DetailEventActivity::class.java)
            intent.putExtra(DetailEventActivity.EXTRA_ID, event.id)
            startActivity(intent)
        }
        binding.rvUpcoming.adapter = adapterUpcoming

        upcomingViewModel.resultEventItemUpcome.observe(viewLifecycleOwner){ eventListResouse ->
            val eventList = upcomingViewModel.findEventUpcomeWithLoading(eventListResouse)

            when(eventListResouse){
                is Resource.Loading -> {
                    binding.upcomingSimmmer.startShimmer()
                    binding.upcomingSimmmer.visibility = View.VISIBLE
                    binding.rvUpcoming.visibility = View.GONE
                }
                is Resource.Success -> {
                    binding.upcomingSimmmer.stopShimmer()
                    binding.upcomingSimmmer.visibility = View.GONE
                    binding.rvUpcoming.visibility = View.VISIBLE
                    binding.upcomingSimmmer.animate().alpha(0f).setDuration(300).withEndAction {
                        adapterUpcoming.submitList(eventList)

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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}