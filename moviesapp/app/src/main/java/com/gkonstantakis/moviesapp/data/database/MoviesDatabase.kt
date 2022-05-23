package com.gkonstantakis.moviesapp.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.gkonstantakis.moviesapp.data.database.entities.Watchlist

@Database(entities = [Watchlist::class], version = 1)
abstract class MoviesDatabase: RoomDatabase() {

    abstract fun moviesDao(): MoviesDao

    companion object {
        const val MOVIE_DATABASE: String = "movie_db"
    }
}