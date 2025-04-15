package com.example.dicodingeventaplication.ui.finished

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.SearchView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.dicodingeventaplication.data.repository.DicodingEventRepository
import com.example.dicodingeventaplication.databinding.FragmentFinishedBinding
import com.example.dicodingeventaplication.ui.detailEvent.DetailEventActivity
import com.example.dicodingeventaplication.viewmodel.EventViewModelFactory
import com.example.dicodingeventaplication.viewmodel.NetworkViewModel
import com.example.dicodingeventaplication.R
import com.example.dicodingeventaplication.ui.home.HomeViewModel
import com.example.dicodingeventaplication.ui.search.SearchResultRVAdapter
import com.example.dicodingeventaplication.utils.Resource
import com.example.dicodingeventaplication.utils.DialogUtils
import com.google.android.material.R as ResMaterial
import com.google.android.material.appbar.AppBarLayout
import kotlin.math.abs

class FinishedFragment : Fragment() {

    private var _binding: FragmentFinishedBinding? = null
    private val binding get() = _binding!!

    private lateinit var backPressedCallback: OnBackPressedCallback

    private var isExpanned = true

    private var isCollapseSearch = false

    private var appBarOffset = 0

    private val networkViewModel: NetworkViewModel by lazy {
        ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
        )[NetworkViewModel::class.java]
    }

//    private val finishedRepository: DicodingEventRepository by lazy {
//        DicodingEventRepository( requireContext())
//    }
//
//    private val finishedViewModel: FinishedViewModel by lazy {
//        ViewModelProvider(this, EventViewModelFactory(finishedRepository))[FinishedViewModel::class.java]
//    }

    private val factory: EventViewModelFactory by lazy {
        EventViewModelFactory.getInstance(requireActivity())
    }

    private val finishedViewModel: FinishedViewModel by viewModels {
        factory
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val rvPosition = binding.rvFinished.layoutManager?.onSaveInstanceState()
        outState.putParcelable(SCROLL_POSITION, rvPosition)
        outState.putInt(APP_BAR_OFFSET, appBarOffset)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        val rvPosition = savedInstanceState?.getParcelable<Parcelable>(SCROLL_POSITION)
        binding.rvFinished.layoutManager?.onRestoreInstanceState(rvPosition)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFinishedBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (savedInstanceState != null) {
            appBarOffset = savedInstanceState.getInt(APP_BAR_OFFSET, 0)
        }

        // observer network
        networkViewModel.isInternetAvailible.observe(viewLifecycleOwner){ isAvailible ->
            if (isAvailible && !finishedViewModel.isFinishedSuccess){
                Log.d(TAG, "onViewCreated: refres otomatis")
                finishedViewModel.startReload()
                finishedViewModel.findEventFinished()
            }
        }

        // pulihkan tampilan search
        binding.finishedSearch.isVisible = finishedViewModel.isSearching
        if (binding.finishedSv.query.isNotBlank()){
            binding.finishedSearch.setBackgroundColor(
                ContextCompat.getColor(requireContext(),R.color.biru_tua)
            )
        }else{
            binding.finishedSearch.setBackgroundColor(
                ContextCompat.getColor(requireContext(),R.color.biru_tua_trans60)
            )
        }

        // pulihkan offset
        binding.finishedCoordinator.post {
            val params = binding.finishedAppbar.layoutParams as CoordinatorLayout.LayoutParams
            val behavior = params.behavior as? AppBarLayout.Behavior
            behavior?.setTopAndBottomOffset(appBarOffset)
        }

        // view if collaps
        if (finishedViewModel.isCollapse) {
            Log.d(TAG, "onViewCreated: iscolaps")
            binding.finishedCvTotal.alpha = 0.0f
        } else
            binding.finishedCvTotal.alpha = 1.0f

        // listener app bar
        binding.finishedAppbar.addOnOffsetChangedListener{ appBarLayout, verticalOffset ->
            Log.d(TAG, "onViewCreated: add on offset")
            binding.finishedAppbar.totalScrollRange
            val scrollRange = appBarLayout.totalScrollRange

            finishedViewModel.isCollapse = verticalOffset == -scrollRange // collaps nilainya -scroll range
            isExpanned = verticalOffset == 0 // expaned jika offset 0
            appBarOffset = verticalOffset // inisialis untuk simpan posisi app bar

            val alphaValue = 1f - (abs(verticalOffset) / scrollRange) // hitung transparasi
            binding.finishedCvTotal.animate().alpha(alphaValue).setDuration(200).start()

            // view if colaps
            val iconSearch = binding.finishedToolbar.menu.findItem(R.id.app_search).icon
            if (finishedViewModel.isCollapse)
                iconSearch?.setTint(Color.BLACK)
            else
                iconSearch?.setTint(Color.WHITE)

            Log.d(TAG, "onViewCreated: scroll range $scrollRange")
            Log.d(TAG, "onViewCreated: scroll expand $isExpanned")
            Log.d(TAG, "onViewCreated: alpha $alphaValue")
            Log.d(TAG, "onViewCreated: scroll collaps ${finishedViewModel.isCollapse}")
            Log.d(TAG, "onViewCreated: scroll offset $verticalOffset")
        }

        // mencegah swip jika colap
        binding.finishedSwipRefresh.setOnChildScrollUpCallback { _, _ ->
            !isExpanned // cegah swip refrash jika colap
        }

        val stragledLayout = StaggeredGridLayoutManager(2,  StaggeredGridLayoutManager.VERTICAL)
        binding.rvFinished.layoutManager = stragledLayout

        val adapterFinished = FinishedRVAdapter(requireContext()) { event ->
            val intent = Intent(requireContext(), DetailEventActivity::class.java)
            intent.putExtra(DetailEventActivity.EXTRA_ID, event.id)
            startActivity(intent)
        }
        binding.rvFinished.adapter = adapterFinished

        finishedViewModel.resultEventItemFinished.observe(viewLifecycleOwner){ event ->
            if (!finishedViewModel.isReload.value!!){
                Log.d(TAG, "onViewCreated: resouse state")
                when(event){
                    is Resource.Loading -> {
                    }
                    is Resource.Success -> {
                        adapterFinished.submitList(event.data)
                        binding.finishedSimmmer.stopShimmer()
                        binding.finishedSimmmer.visibility = View.INVISIBLE
                        binding.finishedLottieError.visibility = View.INVISIBLE
                        binding.finishedLottieErrorKoneksi.visibility = View.INVISIBLE

                        finishedViewModel.markFinishedSuccess()
                        binding.finishedTotalEvent.text = adapterFinished.currentList.size.toString()
                    }
                    is Resource.Error -> {
                        adapterFinished.submitList(emptyList())
                        binding.finishedSimmmer.stopShimmer()
                        binding.finishedSimmmer.visibility = View.INVISIBLE
                        binding.finishedLottieError.visibility = View.VISIBLE
                        binding.finishedLottieErrorKoneksi.visibility = View.INVISIBLE

                    }
                    is Resource.ErrorConection -> {
                        binding.finishedSimmmer.stopShimmer()
                        binding.finishedSimmmer.visibility = View.INVISIBLE
                        binding.finishedLottieError.visibility = View.INVISIBLE
                        if (!finishedViewModel.isFinishedSuccess)
                            binding.finishedLottieErrorKoneksi.visibility = View.VISIBLE
                    }
                    is Resource.Empty -> {
                        adapterFinished.submitList(emptyList())
                        binding.finishedSimmmer.stopShimmer()
                        binding.finishedSimmmer.visibility = View.INVISIBLE
                        binding.finishedLottieError.visibility = View.INVISIBLE
                        binding.finishedLottieErrorKoneksi.visibility = View.INVISIBLE
                    }

                    else -> {}
                }
            }
            Log.d(TAG, "onViewCreated: state $event")
        }

        // refresh
        finishedViewModel.isRefresing.observe(viewLifecycleOwner){ isRefresh ->
            binding.finishedSwipRefresh.isRefreshing = isRefresh
        }

        finishedViewModel.isReload.observe(viewLifecycleOwner){ isReload ->
            Log.d(TAG, "onViewCreated: isrefreshing observe $isReload")
            if ((binding.finishedLottieErrorKoneksi.isVisible || binding.finishedLottieError.isVisible) && isReload){
                Log.d(TAG, "onViewCreated: isrefresh")
                adapterFinished.submitList(emptyList()){
                    // mulai simer
                    binding.finishedLottieError.visibility = View.INVISIBLE
                    binding.finishedLottieErrorKoneksi.visibility = View.INVISIBLE

                    binding.finishedSimmmer.startShimmer()
                    binding.finishedSimmmer.visibility = View.VISIBLE
                }
            }
        }

        // notif
        finishedViewModel.dialogNotifError.observe(viewLifecycleOwner){
            it.getContentIfNotHandled()?.let { dialogMesege ->
                DialogUtils.showPopUpErrorDialog(requireActivity(), dialogMesege)
            }
        }

        binding.finishedBtnLottieErorKoneksi.setOnClickListener {
            finishedViewModel.startRefreshing()
            finishedViewModel.startReload()
            finishedViewModel.findEventFinished()
        }

        // swip
        binding.finishedSwipRefresh.setColorSchemeColors(ContextCompat.getColor(requireContext(), R.color.biru_tua))
        binding.finishedSwipRefresh.setProgressBackgroundColorSchemeColor(ContextCompat.getColor(requireContext(), R.color.white))
        binding.finishedSwipRefresh.setProgressViewOffset(true, 0, 200)

        binding.finishedSwipRefresh.setOnRefreshListener {
            finishedViewModel.startRefreshing()
            finishedViewModel.startReload()
            finishedViewModel.findEventFinished()
        }

        // SEARCHING
        binding.finishedRvSearch.layoutManager = LinearLayoutManager(requireContext())
        val adapterSearch = FinishedSearchAdapter(
            context = requireActivity(),
            onItemClick = { eventItem ->
                val intent = Intent(requireContext(), DetailEventActivity::class.java)
                intent.putExtra(DetailEventActivity.EXTRA_ID, eventItem.id)
                startActivity(intent)
                Log.d("actsc", "setEvent Data: onsucces")
            },
            textColor = Color.WHITE,
            theme = ResMaterial.style.ThemeOverlay_MaterialComponents_Dark
        )
        binding.finishedRvSearch.adapter = adapterSearch

        binding.finishedToolbar.setOnMenuItemClickListener { menuItem ->
            when(menuItem.itemId){
                R.id.app_search -> {
                    isCollapseSearch = finishedViewModel.isCollapse
                    binding.finishedSv.isIconified = false // menampilkan keyboard langsung

                    finishedViewModel.isSearching =  true
                    binding.finishedSearch.setBackgroundColor(
                        ContextCompat.getColor(requireContext(),R.color.biru_tua_trans60)
                    )

                    Log.d(TAG, "onCreateMenu: search item clicked & $isCollapseSearch")
                    binding.finishedAppbar.setExpanded(false, true)
                    binding.finishedSearch.visibility = View.VISIBLE
                    true
                }
                else -> false
            }
        }

        binding.finishedSv.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                binding.finishedSearchLottieNotResult.visibility = View.INVISIBLE

                if (!newText.isNullOrBlank()){
                    Log.d(TAG, "onQueryTextChange: newteks not null ${newText.isNotEmpty()}")
                    binding.finishedSearch.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.biru_tua))
                    binding.finishedRvSearch.visibility = View.VISIBLE

                    finishedViewModel.searchEvent(newText)
                }else{
                    adapterSearch.submitList(emptyList())

                    binding.finishedRvSearch.visibility = View.INVISIBLE
                    binding.finishedSearch.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.biru_tua_trans60))
                }
                return true
            }

        })

        finishedViewModel.listSearchResult.observe(viewLifecycleOwner){ items ->
            if (binding.finishedSv.query.isNotBlank()){
                when(items){
                    is Resource.Success -> {
                        adapterSearch.submitList(items.data)
                        binding.finishedSearchLottieNotResult.visibility = View.INVISIBLE
                    }
                    is Resource.Empty ->{
                        adapterSearch.submitList(emptyList())
                        binding.finishedSearchLottieNotResultLottie.playAnimation()
                        binding.finishedSearchLottieNotResult.visibility = View.VISIBLE
                    }
                    else -> {}
                }
            }
            Log.d(TAG, "onViewCreated: list search $items")
        }

        binding.finishedBtnCancelSearch.setOnClickListener {
            binding.finishedSearch.visibility = View.GONE
            adapterSearch.submitList(emptyList())
            binding.rvFinished.isNestedScrollingEnabled = true
            binding.finishedSearchLottieNotResult.visibility = View.INVISIBLE

            binding.finishedSv.setQuery("", false)
            finishedViewModel.isSearching =  false

            if (!isCollapseSearch){
                Log.d(TAG, "onViewCreated: expand tre")
                binding.finishedAppbar.setExpanded(true, true)
            }else{
                binding.finishedAppbar.setExpanded(false, true)
            }
        }

        // back ketika is searching visible
        backPressedCallback = object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                val viewSearch = binding.finishedSearch
                if (viewSearch.isVisible) {
                    binding.finishedBtnCancelSearch.performClick()
                }
                else
                    findNavController().popBackStack() /// biarkan sistem menangani back
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, backPressedCallback)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        backPressedCallback.remove()
        _binding = null
    }

    companion object{
        private const val SCROLL_POSITION = "scrol_position"
        const val TAG = "upcomingfrag"
        private const val APP_BAR_OFFSET = "app_bar_offset"
    }
}