package com.example.dicodingeventaplication.ui.favorite

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dicodingeventaplication.R
import com.example.dicodingeventaplication.SettingPreferences
import com.example.dicodingeventaplication.dataStore
import com.example.dicodingeventaplication.databinding.ActivityFavoriteBinding
import com.example.dicodingeventaplication.ui.detailEvent.DetailEventActivity
import com.example.dicodingeventaplication.viewmodel.EventViewModelFactory
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class FavoriteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFavoriteBinding
    private lateinit var adapter: FavoritAdapter
//    private val viewModel: FavoritViewModel by viewModels()

    private val factory: EventViewModelFactory by lazy {
        EventViewModelFactory.getInstance(this)
    }

    private val favoritViewModel: FavoritViewModel by viewModels {
        factory
    }

    private var savedScrollState: Parcelable? = null

    companion object{
        private const val SCROLL_POSITION = "scrol_position"
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val rvPosition = binding.rvFavorite.layoutManager?.onSaveInstanceState()
        outState.putParcelable(SCROLL_POSITION, rvPosition)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        savedScrollState = savedInstanceState.getParcelable(SCROLL_POSITION)
//        binding.rvFavorite.layoutManager?.onRestoreInstanceState(rvPosition)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFavoriteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.statusBarColor = ContextCompat.getColor(this, R.color.white)
        // icon status bar
        val preferences = SettingPreferences.getInstance(dataStore)
        lifecycleScope.launch {
            val theme = preferences.getThemeSetting().first()
            applyStatusBarTheme(theme)
        }
        binding.favoriteBtnBack.setOnClickListener { finish() }
        binding.favoriteSize.text = getString(R.string.favorit_null)

        val linearLayout = LinearLayoutManager(this)
        binding.rvFavorite.layoutManager = linearLayout

        adapter = FavoritAdapter(this,
            onItemClick = { event ->
                val intent = Intent(this, DetailEventActivity::class.java)
                intent.putExtra(DetailEventActivity.EXTRA_EVENT, event)
                startActivity(intent)
            },
            onDelete = { event ->
                favoritViewModel.deleteFavorit(event)
            },
            onSwipeChanged = {
                Log.d("TAG", "onCreate: update button")
                updateButtonEdit()
            }
        )

        binding.rvFavorite.adapter = adapter

        // ketika rv di scrol
//        binding.rvFavorite.addOnScrollListener(object : OnScrollListener(){
//            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
//                super.onScrolled(recyclerView, dx, dy)
//                updateButtonEdit()
//                Log.d("TAG", "onScrolled: rv")
//            }
//        })
        binding.favoritBtnEdit.setOnClickListener{
            if (adapter.swipeLayouts.any { it.isRightOpen }){
                adapter.swipeLayouts.forEach { swipeLayout ->
                    swipeLayout.close(true)
                }
            }else{
                adapter.swipeLayouts.forEach { swipeLayout ->
                    swipeLayout.openRight(true)
                }
            }

            updateButtonEdit() // teks button
            adapter.isAllOpenItem = !adapter.isAllOpenItem
        }

        // observe favorit
        favoritViewModel.getFavoritBookmark().observe(this){
            adapter.submitList(it)
            binding.favoriteSize.text = getString(R.string.favorit_size, it.size.toString())

            // akses
            if (savedScrollState != null){
                binding.rvFavorite.layoutManager?.onRestoreInstanceState(savedScrollState)
                savedScrollState = null // agar nggak ke-restore dua kali
            }

            if (it.isEmpty())
                binding.favoritTvNotFavorit.isVisible = true
            else
                binding.favoritTvNotFavorit.isVisible = false
        }
    }

    private fun applyStatusBarTheme(isDark: Boolean) {
        if (isDark) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            window.statusBarColor = Color.BLACK
        } else {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            window.statusBarColor = Color.WHITE
        }
    }

    private fun updateButtonEdit(){
        val anyOpen = adapter.swipeLayouts.any { it.isRightOpen }
        binding.favoritBtnEdit.text = if (anyOpen){
            resources.getString(R.string.selesai)
        }else
            resources.getString(R.string.edit)
    }
}