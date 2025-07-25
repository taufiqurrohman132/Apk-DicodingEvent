package com.example.dicodingeventaplication.ui.search.filterDialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.example.dicodingeventaplication.R
import com.example.dicodingeventaplication.databinding.FragmentFilterDialogBinding
import com.example.dicodingeventaplication.ui.search.SearchViewModel

class FilterDialogFragment : DialogFragment() { // tidak di sarankan pakai construktor
    private var _binding: FragmentFilterDialogBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SearchViewModel by activityViewModels() // ambil dari activity

    private var listener: DialogListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is DialogListener){
            listener = context
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

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
                viewModel.selectButton(button.id)
                button.setBackgroundColor(
                    if(button.isSelected){
                        ContextCompat.getColor(requireContext(), R.color.bottom_nav_icon)
                    }else{
                        ContextCompat.getColor(requireContext(), R.color.abu_abu_terang2)
                    }
                )

                button.post {
                    listener?.showToant(button.text.toString())
                    dismiss()
                }
            }
        }

        // observe pilihan terakhir agar tetap aktif
        viewModel.selectButton.observe(viewLifecycleOwner){ selectedId ->
            buttons.forEach{ button ->
                button.isSelected = (button.id == selectedId)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}

interface DialogListener {
    fun showToant(message: String)
}

