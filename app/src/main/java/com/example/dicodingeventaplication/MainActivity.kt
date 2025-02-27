package com.example.dicodingeventaplication

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.dicodingeventaplication.databinding.ActivityMainBinding
import com.example.dicodingeventaplication.ui.finished.FinishedFragment
import com.example.dicodingeventaplication.ui.home.HomeFragment
import com.example.dicodingeventaplication.ui.upcoming.UpcomingFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var navController: NavController
    private val fragmentManager by lazy { supportFragmentManager }
    private val fragmentMap = mutableMapOf<Int, Fragment>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        val bottomNavView: BottomNavigationView = binding.navView

//        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
//        navController = navHostFragment.navController


        // ambil fragment yang sudah ada kalua ada
        if (savedInstanceState == null){
            fragmentMap[R.id.navigation_home] = HomeFragment()
            fragmentMap[R.id.navigation_upcoming] = UpcomingFragment()
            fragmentMap[R.id.navigation_finished] = FinishedFragment()

            /// tabahkan fragment pertama kali
            showFragment(R.id.navigation_home)
        } else{
            fragmentManager.fragments.forEach { fragment->
                when(fragment.tag){
                    R.id.navigation_home.toString() -> fragmentMap[R.id.navigation_home] = fragment
                    R.id.navigation_upcoming.toString() -> fragmentMap[R.id.navigation_upcoming] = fragment
                    R.id.navigation_finished.toString() -> fragmentMap[R.id.navigation_finished] = fragment
                }
            }
        }

        bottomNavView.setOnItemSelectedListener {
            showFragment(it.itemId)
            true
        }
    }

    private fun showFragment(fragmentId: Int){
        val fragmentTransaction = fragmentManager.beginTransaction()

        // sembunyikan semua fragment yang sebelumya
        fragmentMap.values.forEach { fragmentTransaction.hide(it) }

        // tampilkan dari map atau tambkhkan
        val fragment = fragmentMap[fragmentId] ?: return

        if (!fragment.isAdded){
            fragmentTransaction.add(R.id.nav_host_fragment_activity_main, fragment, fragmentId.toString())
        }
        fragmentTransaction.show(fragment)
        fragmentTransaction.commit()
    }
}