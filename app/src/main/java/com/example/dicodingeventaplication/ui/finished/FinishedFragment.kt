package com.example.dicodingeventaplication.ui.finished

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.dicodingeventaplication.data.repository.DicodingEventRepository
import com.example.dicodingeventaplication.data.retrofit.ApiConfig
import com.example.dicodingeventaplication.databinding.FragmentFinishedBinding
import com.example.dicodingeventaplication.ui.detailEvent.DetailEventActivity
import com.example.dicodingeventaplication.EventViewModelFactory
import com.example.dicodingeventaplication.R
import com.example.dicodingeventaplication.utils.Resource
import com.example.dicodingeventaplication.utils.DialogUtils
import com.example.dicodingeventaplication.viewmodel.FinishedViewModel

class FinishedFragment : Fragment() {

    private var _binding: FragmentFinishedBinding? = null
    private val binding get() = _binding!!

    private lateinit var finishedViewModel: FinishedViewModel
    private lateinit var finishedRepository: DicodingEventRepository

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val rvPosition = binding.rvFinished.layoutManager?.onSaveInstanceState()
        outState.putParcelable(SCROLL_POSITION, rvPosition)
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

        // repository
        val apiService = ApiConfig.getApiService()
        finishedRepository = DicodingEventRepository(apiService, requireContext())

        val viewModelFactory = EventViewModelFactory(finishedRepository)
        finishedViewModel =
            ViewModelProvider(this, viewModelFactory)[FinishedViewModel::class.java]

        val stragledLayout = StaggeredGridLayoutManager(2,  StaggeredGridLayoutManager.VERTICAL)
        binding.rvFinished.layoutManager = stragledLayout

        val adapterFinished = FinishedRVAdapter(requireContext()) { event ->
            val intent = Intent(requireContext(), DetailEventActivity::class.java)
            intent.putExtra(DetailEventActivity.EXTRA_ID, event.id)
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
                        finishedViewModel.isFinishedSuccess()
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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object{
        private const val SCROLL_POSITION = "scrol_position"
        private const val TAG = "upcomingfrag"
    }
}