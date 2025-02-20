package com.example.dicodingeventaplication

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.dicodingeventaplication.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Kunci rotasi sementara
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_NOSENSOR

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //buat status bar custom
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                        View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR //icon jadi gelap ketika bg terang
                )

//        // Setelah UI siap, izinkan rotasi kembali
//        window.decorView.post {
//            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
//        }

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        navView.setupWithNavController(navController)
    }
}