package com.example.dicodingeventaplication.ui.favorite

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dicodingeventaplication.R
import com.example.dicodingeventaplication.databinding.ActivityFavoriteBinding
import com.example.dicodingeventaplication.ui.detailEvent.DetailEventActivity
import com.example.dicodingeventaplication.ui.home.HomeViewModel
import com.example.dicodingeventaplication.viewmodel.EventViewModelFactory

class FavoriteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFavoriteBinding
//    private val viewModel: FavoritViewModel by viewModels()

    private val factory: EventViewModelFactory by lazy {
        EventViewModelFactory.getInstance(this)
    }

    private val favoritViewModel: FavoritViewModel by viewModels {
        factory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoriteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.statusBarColor = ContextCompat.getColor(this, R.color.white)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        binding.favoriteBtnBack.setOnClickListener { finish() }

        val linearLayout = LinearLayoutManager(this)
        binding.rvFavorite.layoutManager = linearLayout

        val adapter = FavoritAdapter(this) { event ->
            val intent = Intent(this, DetailEventActivity::class.java)
            intent.putExtra(DetailEventActivity.EXTRA_ID, event.id)
            startActivity(intent)
        }

        binding.rvFavorite.adapter = adapter

        favoritViewModel.getFavoritBookmark().observe(this){
            binding.upcomingSimmmer.stopShimmer()
            binding.upcomingSimmmer.visibility = View.INVISIBLE
            adapter.submitList(it)
        }
    }
}