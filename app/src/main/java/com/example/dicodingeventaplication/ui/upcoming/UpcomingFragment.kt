package com.example.dicodingeventaplication.ui.upcoming

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dicodingeventaplication.databinding.FragmentUpcomingBinding

class UpcomingFragment : Fragment() {

    private var _binding: FragmentUpcomingBinding? = null
    private val binding get() = _binding!!

    private val upcomingViewModel: UpcomingViewModel by viewModels()

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

        val list = listOf("","", "", "", "", "", "", "", "", "", "", "")
        val linearLayout = LinearLayoutManager(requireContext())
        val adapterUpcoming = UpcomingRVAdapter()
        binding.rvUpcoming.layoutManager = linearLayout
        adapterUpcoming.submitList(list)
        binding.rvUpcoming.adapter = adapterUpcoming

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}