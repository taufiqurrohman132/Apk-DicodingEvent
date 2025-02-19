package com.example.dicodingeventaplication.ui.search.filterDialog

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.dicodingeventaplication.R
import com.example.dicodingeventaplication.databinding.FragmentFilterDialogBinding
import com.example.dicodingeventaplication.ui.search.SearchViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class FilterDialogFragment : DialogFragment() {//private val onBtnClick: (Int) -> Unit // tidak di sarankan pakai construktor
    private var _binding: FragmentFilterDialogBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SearchViewModel by activityViewModels() // ambil dari activity

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return dialog
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentFilterDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val buttons = listOf(
            binding.btnStateAll,
            binding.btnStateUpcone,
            binding.btnStateFinish,
        )

        buttons.forEach{ button ->
            button.setOnClickListener{
//                lifecycleScope.launch {
//
//                }
                viewModel.selectButton(button.id)
                button.setBackgroundColor(
                    if(button.isSelected){
                        resources.getColor(R.color.biru_tua)
                    }else{
                        resources.getColor(R.color.abu_abu_terang2)
                    }
                )

//                delay(500)
                button.post {
                    dismiss()
                }
            }
        }

        // observe pilihan terakhir agar tetap aktif
        viewModel.selectButton.observe(viewLifecycleOwner){ selectedId ->
            buttons.forEach{ button ->
                button.isSelected = (button.id == selectedId)
//                button.setBackgroundColor(
//                    if(button.isSelected){
//                        resources.getColor(R.color.biru_tua)
//                    }else{
//                        resources.getColor(R.color.abu_abu_terang2)
//                    }
//                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}