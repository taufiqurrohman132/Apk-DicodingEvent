package com.example.dicodingeventaplication.ui.search

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dicodingeventaplication.R
import com.example.dicodingeventaplication.utils.Resource
import com.example.dicodingeventaplication.data.repository.DicodingEventRepository
import com.example.dicodingeventaplication.data.retrofit.ApiConfig
import com.example.dicodingeventaplication.databinding.ActivitySearchBinding
import com.example.dicodingeventaplication.ui.detailEvent.DetailEventActivity
import com.example.dicodingeventaplication.ui.search.filterDialog.FilterDialogFragment
import com.example.dicodingeventaplication.EventViewModelFactory
import com.example.dicodingeventaplication.utils.DialogUtils
import com.example.dicodingeventaplication.viewmodel.SearchViewModel

class SearchActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySearchBinding
    private lateinit var searchViewModel: SearchViewModel
    private lateinit var searchRepository: DicodingEventRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.statusBarColor = ContextCompat.getColor(this, R.color.white)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        // inisialisasi repositori
        val apiService = ApiConfig.getApiService()
        searchRepository = DicodingEventRepository(apiService, this)

        // pakai view model factory
        val viewModelFactory = EventViewModelFactory(searchRepository)
        searchViewModel = ViewModelProvider(this, viewModelFactory)[SearchViewModel::class.java]// pengganti get

        var activeQuery: Int = -1
        var search = ""
        var queryIsSubmit = false

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
            onDeleteClickItem = { item -> searchViewModel.removeFromHistory(item)},
            onItemClick = { event ->
                val intent = Intent(this@SearchActivity, DetailEventActivity::class.java)
                intent.putExtra(DetailEventActivity.EXTRA_ID, event.id)
                startActivity(intent)
                Handler(Looper.getMainLooper()).postDelayed({
                    searchViewModel.saveToHistory(event)
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
                searchViewModel.saveToHistory(event)// save ke hsitory
                Log.d("actsc", "setEvent Data: onsucces")
            }
        )
        binding.rvSearchResult.adapter = adapterResult

        binding.searchView.isIconified = false // menampilkan keyboard langsung

        // tampilkan history
        searchViewModel.loadSearchHistory{
            binding.searchHeadarHistory.visibility = View.VISIBLE
        }

        // observe history
        searchViewModel.listhHistory.observe(this){ historyList ->
            adapterHistory.submitList(historyList)
            Log.d(TAG, "onCreate: history $historyList")
        }

        searchViewModel.activeQuery.observe(this){ activeValue->

            // update search
            if (activeQuery != activeValue){
                activeQuery = activeValue
                searchViewModel.searchEvent(search, activeQuery)
            }
        }

        searchViewModel.selectButton.observe(this){ selectId ->
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
                    searchViewModel.searchEvent(it, activeQuery)
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

//                        searching = ""
                    adapterHistory.submitList(searchViewModel.listhHistory.value ?: emptyList())
                    Log.d(TAG, "onQueryTextChange: history ${searchViewModel.listhHistory.value}")
                    Log.d(TAG, "onQueryTextChange: null")
                }else{
                    queryIsSubmit = false

                    binding.searchHeaderResult.visibility = View.VISIBLE

                    binding.searchHeadarHistory.visibility = View.INVISIBLE
                    binding.rvSearchHistory.visibility = View.INVISIBLE

                    searchViewModel.searchEvent(newText, activeQuery)
//                    if (searchViewModel.searching != newText) {
//                        searchViewModel.setSearch(newText)
//
//                        Log.d(TAG, "onQueryTextChange: searchin !=  newteks")
//                        adapterResult.submitList(emptyList())
//                    } // tes

//                        searching = newText
                }
                Log.d("actsc", "query Data: onsucces")
                return true
            }
        })

        searchViewModel.searchResultEventItem.observe(this) { event ->
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
            searchViewModel.clearHistory{
                binding.searchHeadarHistory.visibility = View.INVISIBLE
            }
        }
    }

    companion object{
        const val TAG = "sadapter"
        private const val SEARCHING = "search"
    }
}