package com.example.dicodingeventaplication.ui.upcoming

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dicodingeventaplication.utils.Resource
import com.example.dicodingeventaplication.databinding.FragmentUpcomingBinding
import com.example.dicodingeventaplication.ui.detailEvent.DetailEventActivity
import com.example.dicodingeventaplication.viewmodel.EventViewModelFactory
import com.example.dicodingeventaplication.viewmodel.NetworkViewModel
import com.example.dicodingeventaplication.R
import com.example.dicodingeventaplication.utils.DialogUtils
import com.google.android.material.appbar.AppBarLayout

class UpcomingFragment : Fragment() {

    private var _binding: FragmentUpcomingBinding? = null
    private val binding get() = _binding!!

    private val networkViewModel: NetworkViewModel by lazy {
        ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
        )[NetworkViewModel::class.java]
    }

//    private val upcomeRepository: DicodingEventRepository by lazy {
//        DicodingEventRepository
//    }
//
//    private val upcomingViewModel: UpcomingViewModel by lazy {
//        ViewModelProvider(this, EventViewModelFactory(upcomeRepository))[UpcomingViewModel::class.java]// pengganti get
//    }

    private val factory: EventViewModelFactory by lazy {
        EventViewModelFactory.getInstance(requireActivity())
    }

    private val upcomingViewModel: UpcomingViewModel by viewModels {
        factory
    }

    private var isExpanned = true

    private var appBarOffset = 0

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val rvPosition = binding.rvUpcoming.layoutManager?.onSaveInstanceState()
        outState.putParcelable(SCROLL_POSITION, rvPosition)
        outState.putInt(
            APP_BAR_OFFSET,
            appBarOffset
        )
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

        if (savedInstanceState != null) {
            appBarOffset = savedInstanceState.getInt(APP_BAR_OFFSET, 0)
        }

        // observer network
        networkViewModel.isInternetAvailible.observe(viewLifecycleOwner) { isAvailible ->
            Log.d(TAG, "onViewCreated: refres otomatis internet $isAvailible ${!upcomingViewModel.isUpcomingSuccess} ${!upcomingViewModel.isUpcomingEmpty}")
            if (isAvailible && (!upcomingViewModel.isUpcomingSuccess && !upcomingViewModel.isUpcomingEmpty)) {
                Log.d(TAG, "onViewCreated: is refresh")
                upcomingViewModel.startReload()
                upcomingViewModel.findEventUpcome()
            }
        }

        // pulihkan offset
        binding.upcomeCoordinator.post {
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
        binding.upcomingSwipRefresh.setOnChildScrollUpCallback { _, _ ->
            !isExpanned // cegah swip jika colap
        }

        val linearLayout = LinearLayoutManager(requireContext())
        binding.rvUpcoming.layoutManager = linearLayout

        val adapterUpcoming = UpcomingRVAdapter(
            requireContext(),
            onItemClick = { event ->
                val intent = Intent(requireContext(), DetailEventActivity::class.java)
                intent.putExtra(DetailEventActivity.EXTRA_EVENT, event)
                startActivity(intent)
            },
            onBookmarkClick = {favorit ->
                Log.d(TAG, "onViewCreated: isbookmark ${favorit.isBookmarked}")
                upcomingViewModel.onFavoritClicked(favorit, !favorit.isBookmarked)
            }
        )
        binding.rvUpcoming.adapter = adapterUpcoming

        upcomingViewModel.resultEventItemUpcome.observe(viewLifecycleOwner){ eventList ->
            Log.d(TAG, "upcoming: $eventList")
            if (!upcomingViewModel.isReload.value!!){
                when(eventList){
                    is Resource.Loading -> {
                    }
                    is Resource.Success -> {
                        adapterUpcoming.submitList(eventList.data)
                        binding.upcomingSimmmer.stopShimmer()
                        binding.upcomingSimmmer.visibility = View.INVISIBLE
                        binding.upcomingLottieEmpty.visibility = View.INVISIBLE
                        binding.upcomingLottieError.visibility = View.INVISIBLE
                        binding.upcomingLottieErrorKoneksi.visibility = View.INVISIBLE
                        upcomingViewModel.markUpcomingSuccess()
                    }
                    is Resource.Error -> {
                        adapterUpcoming.submitList(emptyList())
                        binding.upcomingSimmmer.stopShimmer()
                        binding.upcomingSimmmer.visibility = View.INVISIBLE
                        binding.upcomingLottieEmpty.visibility = View.INVISIBLE
                        binding.upcomingLottieError.visibility = View.VISIBLE
                        binding.upcomingLottieErrorKoneksi.visibility = View.INVISIBLE
                    }
                    is Resource.ErrorConection -> {
                        binding.upcomingSimmmer.stopShimmer()
                        binding.upcomingSimmmer.visibility = View.INVISIBLE
                        binding.upcomingLottieError.visibility = View.INVISIBLE
                        if (!upcomingViewModel.isUpcomingSuccess && (!upcomingViewModel.isUpcomingEmpty))
                            binding.upcomingLottieErrorKoneksi.visibility = View.VISIBLE
                        else if (!upcomingViewModel.isUpcomingSuccess)
                            binding.upcomingLottieEmpty.visibility = View.VISIBLE
                    }
                    is Resource.Empty -> {
                        adapterUpcoming.submitList(emptyList())
                        binding.upcomingSimmmer.stopShimmer()
                        binding.upcomingSimmmer.visibility = View.INVISIBLE
                        binding.upcomingLottieEmpty.visibility = View.VISIBLE
                        binding.upcomingLottieError.visibility = View.INVISIBLE
                        binding.upcomingLottieErrorKoneksi.visibility = View.INVISIBLE
                        upcomingViewModel.markUpcomingEmpty()
                    }
                    else ->{}
                }
            }
        }

        // refresh
        upcomingViewModel.isRefreshing.observe(viewLifecycleOwner){ isRefresh ->
            binding.upcomingSwipRefresh.isRefreshing = isRefresh
        }

        upcomingViewModel.isReload.observe(viewLifecycleOwner){ isReload ->
            if (binding.upcomingLottieError.isVisible || binding.upcomingLottieErrorKoneksi.isVisible && isReload){
                Log.d(TAG, "onViewCreated: stat simmer")

                adapterUpcoming.submitList(emptyList())
                // mulai simer
                binding.upcomingLottieError.visibility = View.INVISIBLE
                binding.upcomingLottieErrorKoneksi.visibility = View.INVISIBLE

                binding.upcomingSimmmer.startShimmer()
                binding.upcomingSimmmer.visibility = View.VISIBLE
            }
        }

        // notif dialgo
        upcomingViewModel.dialogNotifError.observe(viewLifecycleOwner){
            it.getContentIfNotHandled()?.let { dialogMesege ->
                Log.d(TAG, "onViewCreated: dialog show")
                DialogUtils.showPopUpErrorDialog(requireActivity(), dialogMesege)
            }
        }

        binding.upcomeBtnLottieErorKoneksi.setOnClickListener {
            upcomingViewModel.startRefreshing()
            upcomingViewModel.startReload()
            upcomingViewModel.findEventUpcome()
        }

        // swip
        binding.upcomingSwipRefresh.setColorSchemeColors(ContextCompat.getColor(requireContext(), R.color.biru_tua))
        binding.upcomingSwipRefresh.setProgressBackgroundColorSchemeColor(ContextCompat.getColor(requireContext(), R.color.icon))
        binding.upcomingSwipRefresh.setProgressViewOffset(true, 0, 200)

        binding.upcomingSwipRefresh.setOnRefreshListener {
            upcomingViewModel.startRefreshing()
            upcomingViewModel.startReload()
            upcomingViewModel.findEventUpcome()
        }
    }
//
//    private fun startDailyReminderEvent(){
//        val periodicRequest = PeriodicWorkRequestBuilder<EventWorker>(
//            1, TimeUnit.DAYS
//        ).build()
//
//        WorkManager.getInstance(requireContext()).enqueueUniquePeriodicWork(
//            "percobaan",
//            ExistingPeriodicWorkPolicy.REPLACE,
//            periodicRequest
//        )
//    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object{
        const val TAG = "upcome"
        private const val SCROLL_POSITION = "scrol_position"
        private const val APP_BAR_OFFSET = "app_bar_offset"
    }

}