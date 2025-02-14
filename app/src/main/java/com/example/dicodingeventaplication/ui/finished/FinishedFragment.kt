package com.example.dicodingeventaplication.ui.finished

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.dicodingeventaplication.data.repository.DicodingEventRepository
import com.example.dicodingeventaplication.data.retrofit.ApiConfig
import com.example.dicodingeventaplication.databinding.FragmentFinishedBinding
import com.example.dicodingeventaplication.ui.detailEvent.DetailEventActivity
import com.example.dicodingeventaplication.EventViewModelFactory
import com.example.dicodingeventaplication.R
import com.example.dicodingeventaplication.Resource
import com.example.dicodingeventaplication.Utils.DialogUtils

class FinishedFragment : Fragment() {

    private var _binding: FragmentFinishedBinding? = null
    private val binding get() = _binding!!

    private lateinit var finishedViewModel: FinishedViewModel
    private lateinit var finishedRepository: DicodingEventRepository

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
//        finishedViewModel =
//            ViewModelProvider(this, )[FinishedViewModel::class.java]

        _binding = FragmentFinishedBinding.inflate(inflater, container, false)
        val root: View = binding.root

//        val textView: TextView = binding.textNotifications
//        finishedViewModel.text.observe(viewLifecycleOwner) {
//            textView.text = it
//        }
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.finishedSimmmer.startShimmer()
        binding.finishedSimmmer.visibility = View.VISIBLE

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

        finishedViewModel.resultEventItemUpcome.observe(viewLifecycleOwner){ event ->
            when(event){
                is Resource.Loading -> {
                }
                is Resource.Success -> {
                    binding.finishedSimmmer.stopShimmer()
                    binding.finishedSimmmer.visibility = View.INVISIBLE

                    adapterFinished.submitList(event.data)
                }
                is Resource.Error -> {
                    binding.finishedSimmmer.stopShimmer()
                    binding.finishedSimmmer.visibility = View.INVISIBLE

                    DialogUtils.showPopUpErrorDialog(requireActivity(), event.message)

                    adapterFinished.submitList(emptyList())
                }
                is Resource.ErrorConection -> {
                    binding.finishedSimmmer.stopShimmer()
                    binding.finishedSimmmer.visibility = View.INVISIBLE

                }
                is Resource.Empty -> {
                    adapterFinished.submitList(emptyList())
                }

            }
        }

        // swip
        binding.finishedSwipRefresh.setColorSchemeColors(resources.getColor(R.color.biru_tua))
        binding.finishedSwipRefresh.setProgressBackgroundColorSchemeColor(resources.getColor(R.color.white))
        binding.finishedSwipRefresh.setProgressViewOffset(true, 0, 200)
        binding.finishedSwipRefresh.setOnRefreshListener {
            finishedViewModel.findEventUpcome{
                binding.finishedSwipRefresh.isRefreshing = false
            }
        }
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}