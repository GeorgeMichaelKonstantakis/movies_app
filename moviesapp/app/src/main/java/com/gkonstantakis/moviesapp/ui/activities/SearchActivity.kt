package com.gkonstantakis.moviesapp.ui.activities

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gkonstantakis.moviesapp.MoviesApplication
import com.gkonstantakis.moviesapp.R
import com.gkonstantakis.moviesapp.data.models.SearchResult
import com.gkonstantakis.moviesapp.data.state.DataState
import com.gkonstantakis.moviesapp.ui.adapters.SearchItemAdapter
import com.gkonstantakis.moviesapp.ui.utils.SearchEventParams
import com.gkonstantakis.moviesapp.ui.view_models.SearchStateEvent
import com.gkonstantakis.moviesapp.ui.view_models.SearchViewModel

class SearchActivity : AppCompatActivity() {

    private lateinit var moviesApplication: MoviesApplication
    private var viewModel: SearchViewModel? = null

    private var isInWatchlist = false

    private var watchlistResults: MutableList<SearchResult>? = null

    private var searchParent: ConstraintLayout? = null
    private var progressBar: ProgressBar? = null
    private var inputText: EditText? = null
    private lateinit var moviesListRecyclerView: RecyclerView
    private var moviesListAdapter: SearchItemAdapter? = null
    private var watchlistButton: Button? = null

    private var countPaging = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        moviesApplication = (application as MoviesApplication)

        viewModel = SearchViewModel(moviesApplication.searchRepository)

        watchlistResults = ArrayList<SearchResult>()

        searchParent = findViewById(R.id.search_parent)

        moviesListAdapter = SearchItemAdapter(
            ArrayList<SearchResult>(),
            moviesApplication.searchRepository
        )
        val layoutManager: LinearLayoutManager? = LinearLayoutManager(this);
        moviesListRecyclerView = this.findViewById(R.id.search_list)
        layoutManager?.orientation = LinearLayoutManager.VERTICAL;
        moviesListRecyclerView.layoutManager = layoutManager
        moviesListRecyclerView.adapter = moviesListAdapter

        inputText = findViewById(R.id.search_input_text)
        inputText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                val inputText = p0.toString()
                if (!isInWatchlist) {
                    if (inputText.isNotEmpty()) {
                        viewModel!!.setStateEvent(
                            SearchStateEvent.NetworkSearchMovies,
                            SearchEventParams(null, inputText, countPaging)
                        )
                    }
                } else {
                    if (inputText.isNotEmpty()) {
                        countPaging = 1;
                        viewModel!!.setStateEvent(
                            SearchStateEvent.WatchlistSearchMovies,
                            SearchEventParams(inputText, null, 1)
                        )
                    }
                }
            }

        })

        moviesListRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val visibleItemCount = layoutManager!!.childCount
                val totalItemCount = layoutManager.itemCount
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                    && firstVisibleItemPosition >= 0
                ) {
                    if(!isInWatchlist){
                        countPaging++
                        viewModel!!.setStateEvent(
                            SearchStateEvent.NetworkSearchMovies,
                            SearchEventParams(null, inputText?.text.toString(), countPaging)
                        )
                    }
                }
            }

        })

        progressBar = findViewById(R.id.progress_bar)

        watchlistButton = findViewById(R.id.view_watchlist)
        watchlistButton?.setOnClickListener {
            if (isInWatchlist) {
                countPaging = 1
                changeWatchlistButtonStyle(WatchlistUserAction.EXIT_WATCHLIST)
                clearAdapterMoviesList()
                moviesListAdapter?.notifyDataSetChanged()
            } else {
                countPaging = 1
                viewModel!!.setStateEvent(
                    SearchStateEvent.WatchlistGetMovies,
                    SearchEventParams(null, null, null)
                )
                changeWatchlistButtonStyle(WatchlistUserAction.VIEW_WATCHLIST)
            }
        }

        subscribeObservers(this)
    }

    private fun subscribeObservers(context: Context) {
        viewModel?.dataState?.observe(this, Observer { datastate ->
            when (datastate) {
                is DataState.SuccessNetworkSearch<List<SearchResult>> -> {
                    displayMoviesList(datastate.data as MutableList<SearchResult>)
                }
                is DataState.SuccessDatabaseGetWatchlist<List<SearchResult>> -> {
                    watchlistResults = null
                    watchlistResults = (datastate.data as MutableList<SearchResult>)
                    displayWatchlist(watchlistResults!!)
                }
                is DataState.SuccessDatabaseSearchWatchlist<List<SearchResult>> -> {
                    watchlistResults = null
                    watchlistResults = (datastate.data as MutableList<SearchResult>)
                    displayWatchlist(watchlistResults!!)
                }
                is DataState.Error -> {
                    displayProgressBar(false)
                }
                is DataState.Loading -> {
                    displayProgressBar(true)
                }
            }
        })
    }

    fun displayMoviesList(data: MutableList<SearchResult>) {
        if(countPaging<2){
            clearAdapterMoviesList()
            moviesListAdapter!!.notifyDataSetChanged()
        }
        addNewDataToMoviesListAdapter(data)
        moviesListAdapter!!.notifyDataSetChanged()
        displayProgressBar(false)
    }

    fun displayWatchlist(data: MutableList<SearchResult>) {
        clearAdapterMoviesList()
        moviesListAdapter!!.notifyDataSetChanged()
        addNewDataToMoviesListAdapter(data)
        moviesListAdapter!!.notifyDataSetChanged()
        displayProgressBar(false)
    }

    fun clearAdapterMoviesList() {
        moviesListAdapter?.clearList()
    }

    fun addNewDataToMoviesListAdapter(data: MutableList<SearchResult>){
        moviesListAdapter?.addNewItems(data)
    }

    fun changeWatchlistButtonStyle(action: WatchlistUserAction) {
        if (action == WatchlistUserAction.VIEW_WATCHLIST) {
            searchParent?.background = resources.getDrawable(R.drawable.watchlist_background)
            watchlistButton?.text = resources.getString(R.string.exit_watchlist)
            watchlistButton?.background =
                resources.getDrawable(R.drawable.remove_from_watchlist_button)
            isInWatchlist = true
        } else {
            searchParent?.background = resources.getDrawable(R.color.white)
            watchlistButton?.background = resources.getDrawable(R.drawable.add_to_watchlist_button)
            watchlistButton?.text = resources.getString(R.string.view_watchlist)
            isInWatchlist = false
        }
    }

    fun displayProgressBar(display: Boolean) {
        if (display) {
            progressBar?.visibility = View.VISIBLE
        } else {
            progressBar?.visibility = View.GONE
        }
    }

    enum class WatchlistUserAction {
        VIEW_WATCHLIST, EXIT_WATCHLIST
    }
}