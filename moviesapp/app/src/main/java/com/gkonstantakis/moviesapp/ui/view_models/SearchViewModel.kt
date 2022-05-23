package com.gkonstantakis.moviesapp.ui.view_models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gkonstantakis.moviesapp.data.models.SearchResult
import com.gkonstantakis.moviesapp.data.repositories.SearchRepository
import com.gkonstantakis.moviesapp.data.state.DataState
import com.gkonstantakis.moviesapp.ui.utils.SearchEventParams
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class SearchViewModel(
    private val searchRepository: SearchRepository
) : ViewModel() {

    private val _dataState: MutableLiveData<DataState<List<SearchResult>>> = MutableLiveData()

    val dataState: LiveData<DataState<List<SearchResult>>>
        get() = _dataState

    fun setStateEvent(searchStateEvent: SearchStateEvent, params: SearchEventParams) {
        viewModelScope.launch {
            when (searchStateEvent) {
                is SearchStateEvent.NetworkSearchMovies -> {
                    searchRepository.getSearchResults(params.query!!, params.page!!)
                        .onEach { dataState ->
                            _dataState.value = dataState
                        }.launchIn(viewModelScope)
                }
                is SearchStateEvent.WatchlistSearchMovies -> {
                    searchRepository.getSearchWatchlistResults(params.title!!).onEach { dataState ->
                        _dataState.value = dataState
                    }.launchIn(viewModelScope)
                }
                is SearchStateEvent.WatchlistGetMovies -> {
                    searchRepository.getWatchlistResults().onEach { dataState ->
                        _dataState.value = dataState
                    }.launchIn(viewModelScope)
                }
            }
        }
    }
}

sealed class SearchStateEvent {
    object NetworkSearchMovies : SearchStateEvent()

    object WatchlistSearchMovies : SearchStateEvent()

    object WatchlistGetMovies : SearchStateEvent()

    object None : SearchStateEvent()
}