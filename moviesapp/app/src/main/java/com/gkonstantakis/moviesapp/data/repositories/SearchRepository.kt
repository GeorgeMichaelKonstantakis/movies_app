package com.gkonstantakis.moviesapp.data.repositories

import android.util.Log
import com.gkonstantakis.moviesapp.data.database.MoviesDao
import com.gkonstantakis.moviesapp.data.mapping.database.DatabaseWatchlistMapper
import com.gkonstantakis.moviesapp.data.mapping.network.NetworkSearchItemMapper
import com.gkonstantakis.moviesapp.data.models.SearchResult
import com.gkonstantakis.moviesapp.data.network.MovieNetworkService
import com.gkonstantakis.moviesapp.data.network.entities.search_entity.inner_objects.Result
import com.gkonstantakis.moviesapp.data.state.DataState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SearchRepository(
    private val moviesDao: MoviesDao,
    private val movieNetworkService: MovieNetworkService,
    private val networkSearchItemMapper: NetworkSearchItemMapper,
    private val databaseWatchlistMapper: DatabaseWatchlistMapper
) {

    suspend fun getSearchResults(query: String, page: Int): Flow<DataState<List<SearchResult>>> =
        flow {
            emit(DataState.Loading)
            try {
                val networkSearchResults = movieNetworkService.getMoviesAndTvShowsBySearch(
                    query = query,
                    page = page
                )
                val validSearchResults: ArrayList<Result> = ArrayList()
                networkSearchResults.results.forEach {
                    if (it.knownFor == null) {
                        validSearchResults.add(it)
                    }
                }
                val searchResults =
                    networkSearchItemMapper.mapFromListOfEntities(validSearchResults)
                emit(DataState.SuccessNetworkSearch(searchResults))
            } catch (e: Exception) {
               // emit(DataState.Error(e))
            }
        }

    suspend fun getWatchlistResults(): Flow<DataState<List<SearchResult>>> = flow {
        emit(DataState.Loading)
        try {
            val watchlistResults = moviesDao.getMoviesAndTvShows()
            val searchResults = databaseWatchlistMapper.mapFromListOfEntities(watchlistResults)
            emit(DataState.SuccessDatabaseGetWatchlist(searchResults))
        } catch (e: Exception) {
            emit(DataState.Error(e))
        }
    }

    suspend fun getSearchWatchlistResults(title: String): Flow<DataState<List<SearchResult>>> =
        flow {
            emit(DataState.Loading)
            try {
                val watchListResults = moviesDao.searchWatchlistByTitle(title)
                val searchResults = databaseWatchlistMapper.mapFromListOfEntities(watchListResults)
                emit(DataState.SuccessDatabaseSearchWatchlist(searchResults))
            } catch (e: Exception) {
                emit(DataState.Error(e))
            }
        }

    suspend fun isMovieInWatchlist(id: Int): Boolean {
        try {
            val movie = moviesDao.getMovieOrShowWithId(id).firstOrNull()
            return movie != null
        } catch (e: Exception) {
            return false
        }
    }

    suspend fun isItemMovie(id: Int): Boolean {
        try {
            val movie = moviesDao.getMovieOrShowWithId(id).firstOrNull()
            return movie?.isMovie == true
        } catch (e: Exception) {
            return false
        }
    }

}