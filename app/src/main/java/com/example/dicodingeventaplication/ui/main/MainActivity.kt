package com.example.dicodingeventaplication.ui.main

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.ViewModelFactoryDsl
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.example.dicodingeventaplication.R
import com.example.dicodingeventaplication.SettingPreferences
import com.example.dicodingeventaplication.dataStore
import com.example.dicodingeventaplication.databinding.ActivityMainBinding
import com.example.dicodingeventaplication.viewmodel.MainViewModelFactory
import com.google.android.material.switchmaterial.SwitchMaterial

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var lastClickTime = 0L

//    private val requestPermissionLauncher =
//        registerForActivityResult(
//            ActivityResultContracts.RequestPermission()
//        ) { isGranted: Boolean ->
//            if (isGranted) {
//                Toast.makeText(this, "Notifications permission granted", Toast.LENGTH_SHORT).show()
//            } else {
//                Toast.makeText(this, "Notifications permission rejected", Toast.LENGTH_SHORT).show()
//            }
//        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val pref = SettingPreferences.getInstance(application.dataStore)
        val mainViewModel = ViewModelProvider(this, MainViewModelFactory(pref))[MainViewModel::class.java]

        WindowCompat.setDecorFitsSystemWindows(window, false)

        val bottomNavView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        bottomNavView.setupWithNavController(navController)

        // tanpa mengganti navigasi otomatisnya setup with nav controller
        bottomNavView.setOnItemSelectedListener { item ->
            val curretTime = System.currentTimeMillis()

            // cegah klik jika kurang dari 500 ms
            if (curretTime - lastClickTime < 300) {
                return@setOnItemSelectedListener false
            }

            lastClickTime = curretTime
            NavigationUI.onNavDestinationSelected(item, navController)
            return@setOnItemSelectedListener true
        }

//        mainViewModel.getThemeSettings().observe(this){ idDarkModeActive ->
//            if (idDarkModeActive){
//                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
//            } else{
//                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
//            }
//        }

//        // reques permision
//        if (Build.VERSION.SDK_INT >= 33) {
//            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
//        }


    }
}