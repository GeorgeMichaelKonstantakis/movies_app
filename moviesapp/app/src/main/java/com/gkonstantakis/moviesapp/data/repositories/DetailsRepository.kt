package com.gkonstantakis.moviesapp.data.repositories

import android.util.Log
import com.gkonstantakis.moviesapp.data.database.MoviesDao
import com.gkonstantakis.moviesapp.data.mapping.database.DatabaseWatchlistMapper
import com.gkonstantakis.moviesapp.data.mapping.network.NetworkMovieMapper
import com.gkonstantakis.moviesapp.data.mapping.network.NetworkTrailersMapper
import com.gkonstantakis.moviesapp.data.mapping.network.NetworkTvShowMapper
import com.gkonstantakis.moviesapp.data.models.Movie
import com.gkonstantakis.moviesapp.data.models.SearchResult
import com.gkonstantakis.moviesapp.data.models.Trailer
import com.gkonstantakis.moviesapp.data.models.TvShow
import com.gkonstantakis.moviesapp.data.network.MovieNetworkService
import com.gkonstantakis.moviesapp.data.state.DataState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.Exception

class DetailsRepository(
    private val moviesDao: MoviesDao,
    private val movieNetworkService: MovieNetworkService,
    private val networkMovieMapper: NetworkMovieMapper,
    private val networkTvShowMapper: NetworkTvShowMapper,
    private val networkTrailersMapper: NetworkTrailersMapper,
    private val databaseWatchlistMapper: DatabaseWatchlistMapper
) {

    suspend fun getMovieById(id: Int): Flow<DataState<List<Movie>>> = flow {
        emit(DataState.Loading)
        try {
            val movies: ArrayList<Movie> = ArrayList()
            val networkMovie = movieNetworkService.getMovieById(id)
            val movie = networkMovieMapper.mapFromEntity(networkMovie)
            movies.add(movie)
            emit(DataState.SuccessNetworkMovie(movies))
        } catch (e: Exception) {
        }
    }

    suspend fun getTvShowById(id: Int): Flow<DataState<List<TvShow>>> = flow {
        emit(DataState.Loading)
        try {
            val tvShows: ArrayList<TvShow> = ArrayList()
            val networkTvShow = movieNetworkService.getTvShowById(id)
            val tvShow = networkTvShowMapper.mapFromEntity(networkTvShow)
            tvShows.add(tvShow)
           emit(DataState.SuccessNetworkTvShow(tvShows))
        } catch (e: Exception) {
        }
    }

    suspend fun deleteMovieOrShowFromWatchlist(searchResult: SearchResult): Flow<DataState<List<SearchResult>>> =
        flow {
            try {
                moviesDao.deleteMovieOrShow(databaseWatchlistMapper.mapToEntity(searchResult))
                var listOfResults = ArrayList<SearchResult>()
                listOfResults.add(searchResult)
                emit(DataState.SuccessDeleteMovieFromWatchlist(listOfResults))
            } catch (e: Exception) {
            }
        }

    suspend fun insertMovieOrShowToWatchlist(searchResult: SearchResult): Flow<DataState<List<SearchResult>>> =
        flow {
            try {
                moviesDao.insertMovieOrTvShowToWatchlist(
                    databaseWatchlistMapper.mapToEntity(
                        searchResult
                    )
                )
                var listOfResults = ArrayList<SearchResult>()
                listOfResults.add(searchResult)
                emit(DataState.SuccessInsertMovieToWatchlist(listOfResults))
            } catch (e: Exception) {
            }
        }

    suspend fun getMovieTrailersById(id: Int): Flow<DataState<List<Trailer>>> = flow {
        emit(DataState.Loading)
        try {
            val networkTrailers = movieNetworkService.getMovieTrailersById(id)
            val trailers = networkTrailersMapper.mapFromListOfEntities(networkTrailers)
            emit(DataState.SuccessNetworkTrailers(trailers))
        } catch (e: Exception) {
            emit(DataState.Error(e))
        }
    }

    suspend fun getTvShowTrailersById(id: Int): Flow<DataState<List<Trailer>>> = flow {
        emit(DataState.Loading)
        try {
            val networkTrailers = movieNetworkService.getTvShowTrailersById(id)

            val trailers = networkTrailersMapper.mapFromListOfEntities(networkTrailers)
            emit(DataState.SuccessNetworkTrailers(trailers))
        } catch (e: Exception) {
           // emit(DataState.Error(e))
        }
    }
}