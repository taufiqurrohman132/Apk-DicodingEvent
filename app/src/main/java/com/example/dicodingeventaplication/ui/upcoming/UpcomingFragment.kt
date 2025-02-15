package com.example.dicodingeventaplication.ui.upcoming

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
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
import com.example.dicodingeventaplication.R
import com.example.dicodingeventaplication.utils.DialogUtils

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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val rvPosition = binding.rvUpcoming.layoutManager?.onSaveInstanceState()
        outState.putParcelable(SCROLL_POSITION, rvPosition)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        val rvPosition = savedInstanceState?.getParcelable<Parcelable>(SCROLL_POSITION)
        binding.rvUpcoming.layoutManager?.onRestoreInstanceState(rvPosition)
    }

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

        // mulai simmer
        binding.upcomingSimmmer.startShimmer()
        binding.upcomingSimmmer.visibility = View.VISIBLE

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

        upcomingViewModel.resultEventItemUpcome.observe(viewLifecycleOwner){ eventList ->
            when(eventList){
                is Resource.Loading -> {
                }
                is Resource.Success -> {
                    binding.upcomingSimmmer.stopShimmer()
                    binding.upcomingSimmmer.visibility = View.INVISIBLE
                    binding.upcomingLottieEmpty.visibility = View.INVISIBLE
                    binding.upcomingLottieError.visibility = View.INVISIBLE

                    adapterUpcoming.submitList(eventList.data)
                }
                is Resource.Error -> {
                    binding.upcomingSimmmer.stopShimmer()
                    binding.upcomingSimmmer.visibility = View.INVISIBLE
                    binding.upcomingLottieEmpty.visibility = View.INVISIBLE
                    binding.upcomingLottieError.visibility = View.VISIBLE

                    DialogUtils.showPopUpErrorDialog(requireActivity(), eventList.message)

                    adapterUpcoming.submitList(emptyList())
                }
                is Resource.ErrorConection -> {
                    binding.upcomingSimmmer.stopShimmer()
                    binding.upcomingSimmmer.visibility = View.INVISIBLE
                    binding.upcomingLottieError.visibility = View.INVISIBLE
                    DialogUtils.showPopUpErrorDialog(requireActivity(), eventList.message)
                }
                is Resource.Empty -> {
                    adapterUpcoming.submitList(emptyList())

                    binding.upcomingSimmmer.stopShimmer()
                    binding.upcomingSimmmer.visibility = View.INVISIBLE
                    binding.upcomingLottieEmpty.visibility = View.VISIBLE
                    binding.upcomingLottieError.visibility = View.INVISIBLE
                }
            }
            Log.d(TAG, "upcoming: $eventList")
        }

        // swip
        binding.upcomingSwipRefresh.setColorSchemeColors(resources.getColor(R.color.biru_tua))
        binding.upcomingSwipRefresh.setProgressBackgroundColorSchemeColor(resources.getColor(R.color.white))
        binding.upcomingSwipRefresh.setProgressViewOffset(true, 0, 200)
        binding.upcomingSwipRefresh.setOnRefreshListener {
            upcomingViewModel.findEventUpcome{
                binding.upcomingSwipRefresh.isRefreshing = false
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        //hapus listener app bar
//        binding.mainAppbar.removeOnOffsetChangedListener(offsetChangedListener)
        _binding = null
    }

    companion object{
        const val TAG = "upcome"
        private const val SCROLL_POSITION = "scrol_position"
    }

}