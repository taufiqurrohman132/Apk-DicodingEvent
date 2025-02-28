package com.example.dicodingeventaplication.ui.search

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dicodingeventaplication.R
import com.example.dicodingeventaplication.utils.Resource
import com.example.dicodingeventaplication.data.repository.DicodingEventRepository
import com.example.dicodingeventaplication.databinding.ActivitySearchBinding
import com.example.dicodingeventaplication.ui.detailEvent.DetailEventActivity
import com.example.dicodingeventaplication.ui.search.filterDialog.FilterDialogFragment
import com.example.dicodingeventaplication.viewmodel.EventViewModelFactory
import com.example.dicodingeventaplication.viewmodel.NetworkViewModel
import com.example.dicodingeventaplication.ui.search.filterDialog.DialogListener
import com.example.dicodingeventaplication.utils.DialogUtils

class SearchActivity : AppCompatActivity(), DialogListener {
    private lateinit var binding: ActivitySearchBinding

    private val networkViewModel: NetworkViewModel by lazy {
        ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        )[NetworkViewModel::class.java]
    }

    private val repository: DicodingEventRepository by lazy {
        DicodingEventRepository(this)
    }

    private val searchViewModel: SearchViewModel by lazy {
        ViewModelProvider(this, EventViewModelFactory(repository))[SearchViewModel::class.java]// pengganti get
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.statusBarColor = ContextCompat.getColor(this, R.color.white)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        var activeQuery: Int = -1
        var search = ""
        var queryIsSubmit = false

        // observe koneksi
        networkViewModel.isInternetAvailible.observe(this){ isAvailible ->
            if (isAvailible && !searchViewModel.isSearchSuccess){
                this@SearchActivity.searchViewModel.searchEvent(search, activeQuery)
            }
        }

        binding.searchView.isIconified = false // menampilkan keyboard langsung

        val linearLayoutResult = LinearLayoutManager(this)
        binding.rvSearchResult.layoutManager = linearLayoutResult
        binding.rvSearchResult.itemAnimator = DefaultItemAnimator()

        val linearLayoutHistory = LinearLayoutManager(this)
        binding.rvSearchHistory.layoutManager = linearLayoutHistory
        binding.rvSearchHistory.itemAnimator = DefaultItemAnimator()

        binding.searchSimmer.visibility = View.INVISIBLE
        binding.rvSearchResult.visibility = View.INVISIBLE
        binding.searchHeaderResult.visibility = View.INVISIBLE

        val adapterHistory = SearchHistoryRVAdapter(
            onDeleteClickItem = { item ->
                this.searchViewModel.removeFromHistory(item)
            },
            onItemClick = { event ->
                val intent = Intent(this@SearchActivity, DetailEventActivity::class.java)
                intent.putExtra(DetailEventActivity.EXTRA_ID, event.id)
                startActivity(intent)
                Handler(Looper.getMainLooper()).postDelayed({
                    this.searchViewModel.saveToHistory(event)
                    Log.d("actsc", "setEvent Data: onsucces")
                }, 500)
                Log.d("actsc", "setEvent Data: onsucces")
            },
            context = this
        )
        binding.rvSearchHistory.adapter = adapterHistory

        val adapterResult = SearchResultRVAdapter(
            context = this,
            onItemClick = {event ->
                val intent = Intent(this@SearchActivity, DetailEventActivity::class.java)
                intent.putExtra(DetailEventActivity.EXTRA_ID, event.id)
                startActivity(intent)
                this.searchViewModel.saveToHistory(event)// save ke hsitory
                Log.d("actsc", "setEvent Data: onsucces")
            }
        )
        binding.rvSearchResult.adapter = adapterResult

        // tampilkan history
        this.searchViewModel.loadSearchHistory{
            binding.searchHeadarHistory.visibility = View.VISIBLE
        }

        // observe history
        this.searchViewModel.listHistory.observe(this){ historyList ->
            adapterHistory.submitList(historyList)
            if (historyList.isEmpty())
                binding.searchHeadarHistory.visibility = View.INVISIBLE

            Log.d(TAG, "onCreate: history $historyList")
        }

        this.searchViewModel.activeQuery.observe(this){ activeValue->
            // update search
            if (activeQuery != activeValue){
                activeQuery = activeValue
                this.searchViewModel.searchEvent(search, activeQuery)
            }
        }

        this.searchViewModel.selectButton.observe(this){ selectId ->
            binding.searchTvStatusActive.text = when(selectId){
                R.id.btn_state_upcone -> "Upcoming"
                R.id.btn_state_finish -> "Finished"
                else -> "All"
            }
        }

        // searching
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    queryIsSubmit = true
                    this@SearchActivity.searchViewModel.searchEvent(it, activeQuery)
                    binding.searchView.clearFocus()// menurunkan keyboard ketika di pencet search
                }
                Log.d(TAG, "query Data: onsucces")
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapterResult.submitList(emptyList())
                search = newText ?: ""

                binding.rvSearchResult.visibility = View.INVISIBLE
                Log.d(TAG, "query Data: onchange")
                Log.d(TAG, "query Data: newteks $newText")
                if (newText.isNullOrBlank()){
                    binding.searchHeaderResult.visibility = View.INVISIBLE
                    binding.searchSimmer.visibility = View.INVISIBLE

                    binding.searchLottieEror.visibility = View.INVISIBLE
                    binding.searchLottieNotResult.visibility = View.INVISIBLE
                    binding.searchLottieNotInternet.visibility = View.INVISIBLE

                    if (adapterHistory.currentList.isNotEmpty()){
                        binding.searchHeadarHistory.visibility = View.VISIBLE
                        binding.rvSearchHistory.visibility = View.VISIBLE
                    }

                    adapterHistory.submitList(this@SearchActivity.searchViewModel.listHistory.value ?: emptyList())
                    Log.d(TAG, "onQueryTextChange: history ${this@SearchActivity.searchViewModel.listHistory.value}")
                    Log.d(TAG, "onQueryTextChange: null")
                }else{
                    queryIsSubmit = false

                    binding.searchHeaderResult.visibility = View.VISIBLE

                    binding.searchHeadarHistory.visibility = View.INVISIBLE
                    binding.rvSearchHistory.visibility = View.INVISIBLE

                    this@SearchActivity.searchViewModel.searchEvent(newText, activeQuery)
                }
                Log.d("actsc", "query Data: onsucces")
                return true
            }
        })

        this.searchViewModel.searchResultEventItem.observe(this) { event ->
            adapterResult.submitList(emptyList())

            if (binding.searchView.query.isNotEmpty()){
                when(event){
                    is Resource.Loading -> {
                        // mulai simmer
                        binding.rvSearchResult.visibility = View.INVISIBLE
                        binding.searchSimmer.startShimmer()
                        binding.searchSimmer.visibility = View.VISIBLE

                        binding.searchLottieEror.visibility = View.INVISIBLE
                        binding.searchLottieNotResult.visibility = View.INVISIBLE
                        binding.searchLottieNotInternet.visibility = View.INVISIBLE

                        Log.d(TAG, "onCreate: reaouse loading")
                    }
                    is Resource.Success -> {
                        binding.rvSearchResult.visibility = View.VISIBLE
                        adapterResult.submitList(event.data ?: emptyList())
                        binding.searchSimmer.stopShimmer()
                        binding.searchSimmer.visibility = View.INVISIBLE
                        Log.d(TAG, "onCreate: reaouse sukses")

                        binding.searchLottieEror.visibility = View.INVISIBLE
                        binding.searchLottieNotResult.visibility = View.INVISIBLE
                        binding.searchLottieNotInternet.visibility = View.INVISIBLE

                        this@SearchActivity.searchViewModel.isSearchSuccess = true
                    }
                    is Resource.Error -> {
                        binding.searchSimmer.stopShimmer()
                        binding.searchSimmer.visibility = View.INVISIBLE
                        binding.rvSearchResult.visibility = View.INVISIBLE
                        Log.d(TAG, "onCreate: reaouse rerror")

                        if (queryIsSubmit) {
                            DialogUtils.showPopUpErrorDialog(this@SearchActivity, event.message)
                            queryIsSubmit = false
                        }

                        binding.searchLottieNotResult.visibility = View.INVISIBLE
                        binding.searchLottieNotInternet.visibility = View.INVISIBLE

                        if (adapterResult.currentList.isEmpty()){
                            binding.searchLottieEror.visibility = View.VISIBLE
                            binding.searchLottieErorLottie.playAnimation()
                        }
                    }
                    is Resource.ErrorConection -> {
                        binding.searchSimmer.stopShimmer()
                        binding.searchSimmer.visibility = View.INVISIBLE

                        if (queryIsSubmit) {
                            DialogUtils.showPopUpErrorDialog(this@SearchActivity, event.message)
                            queryIsSubmit = false
                        }

                        Log.d(TAG, "onCreate: reaouse rerror konect")
                        binding.searchLottieEror.visibility = View.INVISIBLE
                        binding.searchLottieNotResult.visibility = View.INVISIBLE
                        if (adapterResult.currentList.isEmpty()){
                            binding.searchLottieNotInternet.visibility = View.VISIBLE
                            binding.searchLottieNotInternetLottie.playAnimation()
                        }

                        this@SearchActivity.searchViewModel.isSearchSuccess = false
                        Log.d(TAG, "onCreate: eror conect ${adapterResult.currentList}")
                    }
                    is Resource.Empty -> {
                        binding.rvSearchResult.visibility = View.INVISIBLE
                        binding.searchSimmer.stopShimmer()
                        binding.searchSimmer.visibility = View.INVISIBLE
                        Log.d(TAG, "onCreate: reaouse empty")

                        binding.searchLottieEror.visibility = View.INVISIBLE
                        binding.searchLottieNotResult.visibility = View.VISIBLE
                        binding.searchLottieNotInternet.visibility = View.INVISIBLE

                        binding.searchLottieNotResultLottie.playAnimation()
                    }

                }
            }
        }
        // click
        binding.searchBtnBack.setOnClickListener{ finish() }

        // filter
        binding.btnFilter.setOnClickListener {
            val dialog = FilterDialogFragment()
            dialog.show(supportFragmentManager, "Filter Dialog")
        }

        // clear history
        binding.tvClearHistory.setOnClickListener {
            this.searchViewModel.clearHistory{
                binding.searchHeadarHistory.visibility = View.INVISIBLE
            }
        }
    }

    override fun showToant(message: String) {
        Toast.makeText(this, "Switch to $message Event", Toast.LENGTH_SHORT).show()
    }

    companion object{
        const val TAG = "sadapter"
    }
}