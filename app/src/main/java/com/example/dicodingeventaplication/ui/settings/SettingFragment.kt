package com.example.dicodingeventaplication.ui.settings

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.dicodingeventaplication.background.EventWorker
import com.example.dicodingeventaplication.R
import com.example.dicodingeventaplication.data.local.datastore.SettingPreferences
import com.example.dicodingeventaplication.data.local.datastore.dataStore
import com.example.dicodingeventaplication.databinding.FragmentSettingBinding
import com.example.dicodingeventaplication.ui.main.MainViewModel
import com.example.dicodingeventaplication.viewmodel.MainViewModelFactory
import java.util.concurrent.TimeUnit

class SettingFragment : Fragment() {

    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding!!

    private lateinit var periodicWorkRequest: PeriodicWorkRequest

    private val mainViewModel: MainViewModel by activityViewModels {
        MainViewModelFactory(SettingPreferences.getInstance(requireContext().dataStore))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentSettingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.settingNestedScroll.scrollTo(0, mainViewModel.scrollY)

        binding.switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            binding.switchDarkMode.trackTintList = ContextCompat.getColorStateList(requireContext(), R.color.select_abu_ungu)
            mainViewModel.saveThemeSetting(isChecked)
        }

        mainViewModel.getThemeSettings().observe(viewLifecycleOwner){ isDark ->
            binding.switchDarkMode.isChecked = isDark
            AppCompatDelegate.setDefaultNightMode(
                if (isDark) AppCompatDelegate.MODE_NIGHT_YES
                else AppCompatDelegate.MODE_NIGHT_NO
            )
        }

        binding.switchNotif.setOnCheckedChangeListener { _, isChecked ->
            binding.switchNotif.trackTintList = ContextCompat.getColorStateList(requireContext(), R.color.select_abu_ungu)

            if (isChecked)
                startDailyReminderEvent()
            else
                cancelDailyReminder()

            mainViewModel.saveNotificationSetting(isChecked)
        }

        mainViewModel.getNotificationSettings().observe(viewLifecycleOwner){ isActive ->
            Log.d(TAG, "onViewCreated: observer notif $isActive")
            binding.switchNotif.isChecked = isActive
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mainViewModel.scrollY = binding.settingNestedScroll.scrollY
        _binding = null
    }

    private fun startDailyReminderEvent(){
        Log.d(TAG, "startDailyReminderEvent: dipanggil")
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        periodicWorkRequest = PeriodicWorkRequestBuilder<EventWorker>(
            1, TimeUnit.DAYS
        ).setConstraints(constraints)
            .build()

        WorkManager.getInstance(requireContext()).enqueueUniquePeriodicWork(
            "upcoming_event_check",
            ExistingPeriodicWorkPolicy.KEEP,
            periodicWorkRequest
        )
    }

    private fun cancelDailyReminder(){
        WorkManager.getInstance(requireContext()).cancelUniqueWork("upcoming_event_check")
    }

    companion object{
        private const val TAG = "settingfrag"
    }

}