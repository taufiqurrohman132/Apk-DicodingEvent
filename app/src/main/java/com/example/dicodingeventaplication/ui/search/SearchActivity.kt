package com.example.dicodingeventaplication.ui.search

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dicodingeventaplication.R
import com.example.dicodingeventaplication.Resource
import com.example.dicodingeventaplication.data.repository.DicodingEventRepository
import com.example.dicodingeventaplication.data.respons.EventItem
import com.example.dicodingeventaplication.data.retrofit.ApiConfig
import com.example.dicodingeventaplication.databinding.ActivitySearchBinding
import com.example.dicodingeventaplication.ui.detailEvent.DetailEventActivity
import com.example.dicodingeventaplication.ui.search.filterDialog.FilterDialogFragment
import com.example.dicodingeventaplication.EventViewModelFactory
import com.example.dicodingeventaplication.utils.DialogUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySearchBinding
    private lateinit var searchViewModel: SearchViewModel
    private lateinit var searchRepository: DicodingEventRepository
    private lateinit var adapter: SearchRVAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var activeQuery: Int = -1
        var searching = ""
        var queryIsSubmit = false
        val linearLayout = LinearLayoutManager(this)
        binding.rvSearch.layoutManager = linearLayout
        binding.rvSearch.itemAnimator = DefaultItemAnimator()

        binding.searchView.isIconified = false // menampilkan keyboard langsung

        // searching
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    queryIsSubmit = true
                    searchViewModel.searchEvent(it, 0)
                    binding.searchView.clearFocus()// menurunkan keyboard ketika di pencet search
                }
                Log.d("actsc", "query Data: onsucces")
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
//                updateList(emptyList(), emptyList())
                if (newText.isNullOrBlank()){
//                    binding.searchSimmer.stopShimmer()
//                    binding.searchSimmer.visibility = View.GONE
//                    binding.searchCvStatus.visibility = View.GONE
                    updateList(searchViewModel.listhHistory.value ?: emptyList(), emptyList())
                    Log.d(TAG, "onQueryTextChange: history ${searchViewModel.listhHistory.value}")
                    Log.d(TAG, "onQueryTextChange: null")
                }else{
                    queryIsSubmit = false

//                    // mulai simmer
//                    binding.searchSimmer.startShimmer()
//                    binding.searchSimmer.visibility = View.VISIBLE
//                    binding.rvSearch.visibility = View.GONE

//                    searchJob?.cancel()
//
//                    searchJob = lifecycleScope.launch {
//                        delay(1000)
//                        binding.searchSimmer.stopShimmer()
//                        binding.searchSimmer.visibility = View.GONE
//                    }

//                    updateList(emptyList(), emptyList())
                    searchViewModel.searchEvent(newText, activeQuery)
//                    updateList(emptyList(), emptyList())
                    binding.searchCvStatus.visibility = View.VISIBLE
                    // coba
                    searching = newText
                }
                Log.d("actsc", "query Data: onsucces")
                return true
            }

        })

        binding.searchBtnBack.setOnClickListener{ finish() }

        getAdapter()

        // inisialisasi repositori
        val apiService = ApiConfig.getApiService()
        searchRepository = DicodingEventRepository(apiService, this)

        // pakai view model factory
        val viewModelFactory = EventViewModelFactory(searchRepository)
        searchViewModel = ViewModelProvider(this, viewModelFactory)[SearchViewModel::class.java]// pengganti get

        // tampilkan history
        searchViewModel.loadSearchHistory()

        // observe history
        searchViewModel.listhHistory.observe(this){ historyList ->
            updateList(historyList, searchViewModel.searchResultEventItem.value.data ?: emptyList())
        }

        searchViewModel.activeQuery.observe(this){ activeValue->
            activeQuery = activeValue

            // update search
            searchViewModel.searchEvent(searching, activeQuery)
        }

        // filter
        binding.btnFilter.setOnClickListener {
            val dialog = FilterDialogFragment()
//            { selectedId ->
//                searchViewModel.selectButton(selectedId)
//            }
            dialog.show(supportFragmentManager, "Filter Dialog")
        }

        searchViewModel.selectButton.observe(this){ selectId ->
            binding.searchTvStatusActive.text = when(selectId){
                R.id.btn_state_upcone -> "Upcoming"
                R.id.btn_state_finish -> "Finished"
                else -> "All"
            }
        }

        lifecycleScope.launch {

            searchViewModel.searchResultEventItem.collect { event ->
//            if (binding.searchView.query.isNullOrBlank()){
//                binding.searchSimmer.stopShimmer()
//                binding.searchSimmer.visibility = View.GONE
//                binding.searchCvStatus.visibility = View.GONE
//                return@observe
//            }
                when(event){
                    is Resource.Loading -> {
                        // mulai simmer
//                        updateList(emptyList(), emptyList())
                        binding.searchSimmer.startShimmer()
                        binding.searchSimmer.visibility = View.VISIBLE
                        binding.rvSearch.visibility = View.INVISIBLE
                        Log.d(TAG, "onCreate: reaouse loading")
                    }
                    is Resource.Success -> {
                        updateList(searchViewModel.listhHistory.value ?: emptyList(), event.data ?: emptyList())
//                        binding.searchSimmer.stopShimmer()
//                        binding.searchSimmer.visibility = View.INVISIBLE
//                        binding.rvSearch.visibility = View.VISIBLE
                        Log.d(TAG, "onCreate: reaouse sukses")

                    }
                    is Resource.Error -> {
                        updateList(emptyList(), emptyList())
//                        binding.searchSimmer.stopShimmer()
//                        binding.searchSimmer.visibility = View.INVISIBLE
//                        binding.rvSearch.visibility = View.VISIBLE
                        Log.d(TAG, "onCreate: reaouse rerror")

                        if (queryIsSubmit) {
                            DialogUtils.showPopUpErrorDialog(this@SearchActivity, event.message)
                            queryIsSubmit = false
                        }
                    }
                    is Resource.ErrorConection -> {

                    }
                    is Resource.Empty -> {
//                        binding.rvSearch.visibility = View.VISIBLE
                        updateList(emptyList(), emptyList())
//                        binding.searchSimmer.stopShimmer()
//                        binding.searchSimmer.visibility = View.INVISIBLE
                        Log.d(TAG, "onCreate: reaouse empty")

                    }

                }
            }
        }
    }

    private fun getAdapter(){ //eventData: List<EventItem>?
        adapter = SearchRVAdapter(
            onDeleteClickItem = { item -> searchViewModel.removeFromHistory(item)},
            onClearHistory = { searchViewModel.clearHistory() },
            onItemClick = { event ->
                val intent = Intent(this@SearchActivity, DetailEventActivity::class.java)
                intent.putExtra(DetailEventActivity.EXTRA_ID, event.id)
                startActivity(intent)
                searchViewModel.saveToHistory(event)// save ke hsitory
                Log.d("actsc", "setEvent Data: onsucces")
            },
            context = this
        )
        binding.rvSearch.adapter = adapter
    }

    private fun updateList(history: List<EventItem>, result: List<EventItem>){
        val list = mutableListOf<SearchItem>()
        if (binding.searchView.query.isBlank()){
            if (history.isNotEmpty()){
                list.add(SearchItem.Header)
            }
            list.addAll(history.map { SearchItem.HistoryItem(it) })
            binding.searchCvStatus.visibility = View.GONE
        }else {
//            binding.searchCvStatus.visibility = View.VISIBLE
            list.addAll(result.map { SearchItem.ResultItem(it) })
        }

        adapter.submitList(ArrayList(list)){
            binding.rvSearch.post {
                lifecycleScope.launch {
                    delay(500)
                    binding.searchSimmer.stopShimmer()
                    binding.searchSimmer.visibility = View.INVISIBLE
                    binding.rvSearch.visibility = View.VISIBLE
                }
            }
        }

        Log.d(TAG, "updateList: list size ${list.size}")
        Log.d(TAG, "updateList: histori size ${history.size}")
        Log.d(TAG, "updateList: histori result $result")
    }

    companion object{
        const val TAG = "sadapter"
    }
}