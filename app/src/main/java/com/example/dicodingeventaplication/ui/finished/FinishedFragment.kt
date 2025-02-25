package com.example.dicodingeventaplication.ui.finished

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView.OnQueryTextListener
import android.widget.Toast
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.dicodingeventaplication.data.repository.DicodingEventRepository
import com.example.dicodingeventaplication.databinding.FragmentFinishedBinding
import com.example.dicodingeventaplication.ui.detailEvent.DetailEventActivity
import com.example.dicodingeventaplication.EventViewModelFactory
import com.example.dicodingeventaplication.R
import com.example.dicodingeventaplication.ui.home.HomeFragment
import com.example.dicodingeventaplication.ui.search.SearchResultRVAdapter
import com.example.dicodingeventaplication.utils.Resource
import com.example.dicodingeventaplication.utils.DialogUtils
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.search.SearchView
import kotlin.math.abs

class FinishedFragment : Fragment() {

    private var _binding: FragmentFinishedBinding? = null
    private val binding get() = _binding!!

    private var isExpanned = true

    private var appBarOffset = 0

    private lateinit var searchView: SearchView

    private val finishedRepository: DicodingEventRepository by lazy {
        DicodingEventRepository( requireContext())
    }

    private val finishedViewModel: FinishedViewModel by lazy {
        ViewModelProvider(this, EventViewModelFactory(finishedRepository))[FinishedViewModel::class.java]
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

        if ( savedInstanceState != null) {
            appBarOffset = savedInstanceState.getInt(APP_BAR_OFFSET, 0)
        }

        // pulihkan offset
        binding.finishedCoordinator.post {
            val params = binding.finishedAppbar.layoutParams as CoordinatorLayout.LayoutParams
            val behavior = params.behavior as? AppBarLayout.Behavior
            behavior?.setTopAndBottomOffset(appBarOffset)
        }

        // listener app bar
        binding.finishedAppbar.addOnOffsetChangedListener{ appBarLayout, verticalOffset ->
            binding.finishedAppbar.totalScrollRange
            isExpanned = verticalOffset == 0 // expaned jika offset 0
            appBarOffset = verticalOffset // inisialis untuk simpan posisi app bar

            val scrollRange = appBarLayout.totalScrollRange
            val alphaValue = 1f - (abs(verticalOffset) / scrollRange) // hitung transparasi

            binding.finishedCvTotal.animate().alpha(alphaValue).setDuration(300).start()
            Log.d(TAG, "onViewCreated: scroll range $scrollRange")
        }

        // mencegah swip jika colap
        binding.finishedSwipRefresh.setOnChildScrollUpCallback { _, _ ->
            !isExpanned // cegah swip jika colap
        }

        val stragledLayout = StaggeredGridLayoutManager(2,  StaggeredGridLayoutManager.VERTICAL)
        binding.rvFinished.layoutManager = stragledLayout

        val adapterFinished = FinishedRVAdapter(requireContext()) { event ->
            val intent = Intent(requireContext(), DetailEventActivity::class.java)
            intent.putExtra(DetailEventActivity.EXTRA_ID, event.id)
            intent.putExtra(DetailEventActivity.EXTRA_EVENT_ACTIVE, HomeFragment.FINISHED)
            startActivity(intent)
        }
        binding.rvFinished.adapter = adapterFinished

        finishedViewModel.resultEventItemFinished.observe(viewLifecycleOwner){ event ->
            if (!finishedViewModel.isRefresing.value!!){
                Log.d(TAG, "onViewCreated: resouse state")
                when(event){
                    is Resource.Loading -> {
                    }
                    is Resource.Success -> {
                        adapterFinished.submitList(event.data){
                            binding.finishedSimmmer.stopShimmer()
                            binding.finishedSimmmer.visibility = View.INVISIBLE
                            binding.finishedLottieError.visibility = View.INVISIBLE
                            binding.finishedLottieErrorKoneksi.visibility = View.INVISIBLE
                        }
                        finishedViewModel.markFinishedSuccess()
                        binding.finishedTotalEvent.text = adapterFinished.currentList.size.toString()
                    }
                    is Resource.Error -> {
                        adapterFinished.submitList(emptyList()){
                            binding.finishedSimmmer.stopShimmer()
                            binding.finishedSimmmer.visibility = View.INVISIBLE
                            binding.finishedLottieError.visibility = View.VISIBLE
                            binding.finishedLottieErrorKoneksi.visibility = View.INVISIBLE
                        }
                    }
                    is Resource.ErrorConection -> {
                        binding.finishedSimmmer.stopShimmer()
                        binding.finishedSimmmer.visibility = View.INVISIBLE
                        binding.finishedLottieError.visibility = View.INVISIBLE
                        if (!finishedViewModel.isFinishedSuccess)
                            binding.finishedLottieErrorKoneksi.visibility = View.VISIBLE
                    }
                    is Resource.Empty -> {
                        adapterFinished.submitList(emptyList()){
                            binding.finishedSimmmer.stopShimmer()
                            binding.finishedSimmmer.visibility = View.INVISIBLE
                            binding.finishedLottieError.visibility = View.INVISIBLE
                            binding.finishedLottieErrorKoneksi.visibility = View.INVISIBLE
                        }
                    }
                }
            }
            Log.d(TAG, "onViewCreated: state $event")
        }

        finishedViewModel.isRefresing.observe(viewLifecycleOwner){ isRefresh ->
            binding.finishedSwipRefresh.isRefreshing = isRefresh
            Log.d(TAG, "onViewCreated: isrefreshing observe $isRefresh")
            if ((binding.finishedLottieErrorKoneksi.isVisible || binding.finishedLottieError.isVisible) && isRefresh){
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
            finishedViewModel.findEventFinished()
        }

        // swip
        binding.finishedSwipRefresh.setColorSchemeColors(resources.getColor(R.color.biru_tua))
        binding.finishedSwipRefresh.setProgressBackgroundColorSchemeColor(resources.getColor(R.color.white))
        binding.finishedSwipRefresh.setProgressViewOffset(true, 0, 200)

        binding.finishedSwipRefresh.setOnRefreshListener {
            finishedViewModel.startRefreshing()
            finishedViewModel.findEventFinished()
        }

        // searching
//        binding.finishedRvSearch.layoutManager = LinearLayoutManager(requireContext())
//        binding.finishedRvSearch.itemAnimator = DefaultItemAnimator()
//
//        val adapterSearch = SearchResultRVAdapter(
//            context = requireContext(),
//            onItemClick = { eventItem ->
//                val intent = Intent(requireContext(), DetailEventActivity::class.java)
//                intent.putExtra(DetailEventActivity.EXTRA_ID, eventItem.id)
//                startActivity(intent)
//                Log.d("actsc", "setEvent Data: onsucces")
//
//            }
//        )
//        binding.finishedRvSearch.adapter = adapterSearch
        binding.finishedRvSearch.layoutManager = LinearLayoutManager(requireContext())
        binding.finishedRvSearch.adapter = SearchResultRVAdapter(requireContext()){}

        binding.finishedSv.setOnQueryTextListener(object : OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }

        })

//        searchView.

    }

//    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
//        super.onCreateOptionsMenu(menu, inflater)
//        inflater.inflate(R.menu.app_bar_menu, menu)
//
//        val searchItem = menu.findItem(R.id.app_search)
//        searchView = searchItem.actionView as SearchView
//
//        searchView.hint = "Cari"
//        searchView.editText.addTextChangedListener(object : TextWatcher{
//            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
//
//            }
//
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//
//            }
//
//            override fun afterTextChanged(s: Editable?) {
//            }
//        })
//    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object{
        private const val SCROLL_POSITION = "scrol_position"
        private const val TAG = "upcomingfrag"
        private const val APP_BAR_OFFSET = "app_bar_offset"
    }
}