package com.gkonstantakis.moviesapp.ui.view_models

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gkonstantakis.moviesapp.data.models.Movie
import com.gkonstantakis.moviesapp.data.models.SearchResult
import com.gkonstantakis.moviesapp.data.models.Trailer
import com.gkonstantakis.moviesapp.data.models.TvShow
import com.gkonstantakis.moviesapp.data.repositories.DetailsRepository
import com.gkonstantakis.moviesapp.data.state.DataState
import com.gkonstantakis.moviesapp.ui.utils.DetailsEventParams
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class DetailsViewModel(
    private val detailsRepository: DetailsRepository
): ViewModel() {

    private val _movieDataState: MutableLiveData<DataState<List<Movie>>> = MutableLiveData()
    val movieDataState: LiveData<DataState<List<Movie>>>
        get() = _movieDataState

    private val _tvDataState: MutableLiveData<DataState<List<TvShow>>> = MutableLiveData()
    val tvDataState: LiveData<DataState<List<TvShow>>>
        get() = _tvDataState

    private val _trailersDataState: MutableLiveData<DataState<List<Trailer>>> = MutableLiveData()
    val trailersDataState: LiveData<DataState<List<Trailer>>>
        get() = _trailersDataState

    private val _searchDataState: MutableLiveData<DataState<List<SearchResult>>> = MutableLiveData()
    val searchDataState: LiveData<DataState<List<SearchResult>>>
        get() = _searchDataState

    fun setStateEvent(detailsStateEvent: DetailsStateEvent, params: DetailsEventParams) {
        viewModelScope.launch {
            when (detailsStateEvent) {
                is DetailsStateEvent.NetworkGetMovie -> {
                    detailsRepository.getMovieById(params.id!!)
                        .onEach { dataState ->
                            _movieDataState.value = dataState
                        }.launchIn(viewModelScope)
                }
                is DetailsStateEvent.NetworkGetTvShow -> {
                    detailsRepository.getTvShowById(params.id!!).onEach { dataState ->
                        _tvDataState.value = dataState
                    }.launchIn(viewModelScope)
                }
                is DetailsStateEvent.NetworkGetMovieTrailers -> {
                    detailsRepository.getMovieTrailersById(params.id!!).onEach { dataState ->
                        _trailersDataState.value = dataState
                    }.launchIn(viewModelScope)
                }
                is DetailsStateEvent.NetworkGetTvShowTrailers -> {
                    detailsRepository.getTvShowTrailersById(params.id!!).onEach { dataState ->
                        _trailersDataState.value = dataState
                    }.launchIn(viewModelScope)
                }
                is DetailsStateEvent.DeleteFromWatchlist -> {
                    detailsRepository.deleteMovieOrShowFromWatchlist(params.searchResult!!)
                        .onEach { dataState ->
                            _searchDataState.value = dataState
                        }.launchIn(viewModelScope)
                }
                is DetailsStateEvent.InsertToWatchlist -> {
                    detailsRepository.insertMovieOrShowToWatchlist(params.searchResult!!)
                        .onEach { dataState ->
                            _searchDataState.value = dataState
                        }.launchIn(viewModelScope)
                }
           }
        }
    }
}

sealed class DetailsStateEvent {
    object NetworkGetMovie : DetailsStateEvent()

    object NetworkGetTvShow : DetailsStateEvent()

    object DeleteFromWatchlist : DetailsStateEvent()

    object InsertToWatchlist : DetailsStateEvent()

    object NetworkGetMovieTrailers : DetailsStateEvent()

    object NetworkGetTvShowTrailers : DetailsStateEvent()

    object None : DetailsStateEvent()
}