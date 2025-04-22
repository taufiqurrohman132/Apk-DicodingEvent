package com.example.dicodingeventaplication.ui.favorite

import android.content.Intent
import android.graphics.Canvas
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dicodingeventaplication.R
import com.example.dicodingeventaplication.databinding.ActivityFavoriteBinding
import com.example.dicodingeventaplication.ui.detailEvent.DetailEventActivity
import com.example.dicodingeventaplication.ui.home.HomeViewModel
import com.example.dicodingeventaplication.ui.upcoming.UpcomingFragment
import com.example.dicodingeventaplication.ui.upcoming.UpcomingFragment.Companion
import com.example.dicodingeventaplication.viewmodel.EventViewModelFactory
import com.zerobranch.layout.SwipeLayout

class FavoriteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFavoriteBinding
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
        savedScrollState = savedInstanceState.getParcelable<Parcelable>(SCROLL_POSITION)
//        binding.rvFavorite.layoutManager?.onRestoreInstanceState(rvPosition)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFavoriteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.statusBarColor = ContextCompat.getColor(this, R.color.white)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        binding.favoriteBtnBack.setOnClickListener { finish() }
        binding.favoriteSize.text = getString(R.string.favorit_null)

        val linearLayout = LinearLayoutManager(this)
        binding.rvFavorite.layoutManager = linearLayout

        val adapter = FavoritAdapter(this,
            onItemClick = { event ->
                val intent = Intent(this, DetailEventActivity::class.java)
                intent.putExtra(DetailEventActivity.EXTRA_EVENT, event)
                startActivity(intent)
            },
            onDelete = { event ->
                favoritViewModel.deleteFavorit(event)
            }
        )

        binding.rvFavorite.adapter = adapter

        // observe favorit
        favoritViewModel.getFavoritBookmark().observe(this){
            adapter.submitList(it)
            binding.favoriteSize.text = getString(R.string.favorit_size, it.size.toString())

            // akses
            if (savedScrollState != null){
                binding.rvFavorite.layoutManager?.onRestoreInstanceState(savedScrollState)
                savedScrollState = null // agar nggak ke-restore dua kali
            }
        }
    }


    private fun itemTouchHelper(adapter: FavoritAdapter) = ItemTouchHelper(
        object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                adapter.notifyItemChanged(viewHolder.adapterPosition)
                Toast.makeText(this@FavoriteActivity, "1 Item Favorit di hapus", Toast.LENGTH_SHORT).show()
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
            }
        }
    )
}