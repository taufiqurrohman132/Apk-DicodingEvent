package com.example.dicodingeventaplication.ui.search

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dicodingeventaplication.R
import com.example.dicodingeventaplication.Resource
import com.example.dicodingeventaplication.data.respons.EventItem
import com.example.dicodingeventaplication.data.retrofit.ApiConfig
import com.example.dicodingeventaplication.databinding.ActivitySearchBinding
import com.example.dicodingeventaplication.ui.DialogUtils
import com.example.dicodingeventaplication.ui.detailEvent.DetailEventActivity
import com.example.dicodingeventaplication.ui.search.filterDialog.FilterDialogFragment
import com.example.dicodingeventaplication.ui.search.viewModel.SearchViewModel
import com.example.dicodingeventaplication.ui.search.viewModel.SearchViewModelFactory

class SearchActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySearchBinding
    private lateinit var searchViewModel: SearchViewModel
    private lateinit var searchRepository: SearchRepository
    private lateinit var adapter: SearchRVAdapter
    private lateinit var ststusHeaderResult: String
//    private var activeQuery: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var activeQuery: Int = -1
        var searching = ""
        var queryIsSubmit = false
        val linearLayout = LinearLayoutManager(this)
        binding.rvSearch.layoutManager = linearLayout

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
                if (newText.isNullOrBlank()){
                    binding.searchCvStatus.visibility = View.GONE
                    updateList(searchViewModel.listhHistory.value ?: emptyList(), emptyList())
                    Log.d(TAG, "onQueryTextChange: null")
                }else{
                    queryIsSubmit = false
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

        getEventData()

        // inisialisasi repositori
        val apiService = ApiConfig.getApiService()
        searchRepository = SearchRepository(apiService, this)

        // pakai view model factory
        val viewModelFactory = SearchViewModelFactory(searchRepository)
        searchViewModel = ViewModelProvider(this, viewModelFactory)[SearchViewModel::class.java]// pengganti get

        // tampilkan history
        searchViewModel.loadSearchHistory()

        // observe history
        searchViewModel.listhHistory.observe(this){ historyList ->
            updateList(historyList, searchViewModel.searchResultEventItem.value?.data ?: emptyList())
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

        searchViewModel.searchResultEventItem.observe(this) { event ->
            when(event){
                is Resource.Loading -> {

                }
                is Resource.Success -> {
                    updateList(searchViewModel.listhHistory.value ?: emptyList(), event.data ?: emptyList())
                }
                is Resource.Error -> {
                    updateList(emptyList(), emptyList())
                    if (queryIsSubmit) {
                        DialogUtils.showPopUpErrorDialog(this, event.message)
                        queryIsSubmit = false
                    }
                }
                is Resource.Empty -> {

                }

            }
        }
    }

    private fun getEventData(){ //eventData: List<EventItem>?
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
            binding.searchCvStatus.visibility = View.GONE
            if (history.isNotEmpty()){
                list.add(SearchItem.Header)
            }
            list.addAll(history.map { SearchItem.HistoryItem(it) })
        }else {
//            binding.searchCvStatus.visibility = View.VISIBLE
            list.addAll(result.map { SearchItem.ResultItem(it) })
        }

        adapter.submitList(list)

        Log.d(TAG, "updateList: list size ${list.size}")
        Log.d(TAG, "updateList: histori size ${history.size}")
        Log.d(TAG, "updateList: histori result $result")
    }

    companion object{
        const val TAG = "sadapter"
    }
}