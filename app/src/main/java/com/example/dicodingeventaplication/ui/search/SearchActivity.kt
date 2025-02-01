package com.example.dicodingeventaplication.ui.search

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dicodingeventaplication.Resource
import com.example.dicodingeventaplication.data.respons.EventItem
import com.example.dicodingeventaplication.data.retrofit.ApiConfig
import com.example.dicodingeventaplication.databinding.ActivitySearchBinding
import com.example.dicodingeventaplication.ui.detailEvent.DetailEventActivity
import com.example.dicodingeventaplication.ui.search.viewModel.SearchViewModel
import com.example.dicodingeventaplication.ui.search.viewModel.SearchViewModelFactory

class SearchActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySearchBinding
    private lateinit var searchViewModel: SearchViewModel
    private lateinit var searchRepository: SearchRepository
    private lateinit var adapter: SearchRVAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val linearLayout = LinearLayoutManager(this)
        binding.rvSearch.layoutManager = linearLayout

        binding.searchView.isIconified = false // menampilkan keyboard langsung

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    searchViewModel.searchEvent(it)
                    binding.searchView.clearFocus()// menurunkan keyboard ketika di pencet search
                }
                Log.d("actsc", "query Data: onsucces")
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrBlank()){
                    updateList(searchViewModel.listhHistory.value ?: emptyList(), emptyList())
                    Log.d(TAG, "onQueryTextChange: null")
                }else{
                    searchViewModel.searchEvent(newText)
                }
                Log.d("actsc", "query Data: onsucces")
                return true
            }

        })

        binding.searchBtnBack.setOnClickListener{ finish() }

//        searchViewModel.searchResultEventItem.observe(this){ event ->
//            setEventData(event)
//        }
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


        searchViewModel.searchResultEventItem.observe(this) { event ->
            when(event){
                is Resource.Loading -> {

                }
                is Resource.Success -> {
                    updateList(searchViewModel.listhHistory.value ?: emptyList(), event.data ?: emptyList())
                }
                is Resource.Error -> {

                }
                is Resource.Empty -> {

                }

            }
        }
    }

    private fun getEventData(){ //eventData: List<EventItem>?
//        val searchAdapter = SearchRVAdapter(this){ event ->
//            val intent = Intent(this@SearchActivity, DetailEventActivity::class.java)
//            intent.putExtra(DetailEventActivity.EXTRA_ID, event.id)
//            startActivity(intent)
//            Log.d("actsc", "setEvent Data: onsucces")
//        }
//        searchAdapter.submitList(eventData)
//        binding.rvSearch.adapter = searchAdapter

        adapter = SearchRVAdapter(
//            onDeleteClickItem = { item -> searchViewModel.removeFromHistory(item)},
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

//        // observe history
//        searchViewModel.listhHistory.observe(this){ historyList ->
//            updateList(historyList, searchViewModel.searchResultEventItem.value?.data ?: emptyList())
//        }

        // observe result
//        searchViewModel.searchResultEventItem.observe(this){ result ->
//            updateList(searchViewModel.listhHistory.value ?: emptyList(), result.data ?: emptyList())
//        }

//        // result
//        searchViewModel.searchResultEventItem.observe(this){ resultList ->
//            if (binding.searchView.query.isNotEmpty()){
//                adapter.submitList(resultList)
//            }
//        }
    }

    private fun updateList(history: List<EventItem>, result: List<EventItem>){
        val list = mutableListOf<SearchItem>()

//        if ( binding.searchView.query.isBlank()){
//            // tampilkan histori dengan header
//
//        } else
        if (binding.searchView.query.isBlank()){
            if (history.isNotEmpty()){
                list.add(SearchItem.Header)
            }
            list.addAll(history.map { SearchItem.HistoryItem(it) })
        }else {
            list.addAll(result.map { SearchItem.ResultItem(it) })
        }

        Log.d(TAG, "updateList: list size ${list.size}")
        adapter.submitList(list)
        Log.d(TAG, "updateList: histori size ${history.size}")
        Log.d(TAG, "updateList: histori result $result")
    }

    companion object{
        const val TAG = "sadapter"
    }
}