package com.gkonstantakis.moviesapp.data.database

import androidx.room.*
import com.gkonstantakis.moviesapp.data.database.entities.Watchlist

@Dao
interface MoviesDao {

    @Query("SELECT * FROM watchlist")
    suspend fun getMoviesAndTvShows(): List<Watchlist>

    @Delete
    suspend fun deleteMovieOrShow(watchlist: Watchlist)

    @Query("SELECT * FROM watchlist WHERE title LIKE :title")
    suspend fun searchWatchlistByTitle(title: String): List<Watchlist>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovieOrTvShowToWatchlist(watchlist: Watchlist): Long

    @Query("SELECT * FROM watchlist WHERE ID = :id")
    suspend fun getMovieOrShowWithId(id: Int): List<Watchlist>
}