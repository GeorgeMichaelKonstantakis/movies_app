package com.gkonstantakis.moviesapp

import android.app.Application
import androidx.room.Room
import com.gkonstantakis.moviesapp.data.database.MoviesDao
import com.gkonstantakis.moviesapp.data.database.MoviesDatabase
import com.gkonstantakis.moviesapp.data.mapping.database.DatabaseWatchlistMapper
import com.gkonstantakis.moviesapp.data.mapping.network.NetworkMovieMapper
import com.gkonstantakis.moviesapp.data.mapping.network.NetworkSearchItemMapper
import com.gkonstantakis.moviesapp.data.mapping.network.NetworkTrailersMapper
import com.gkonstantakis.moviesapp.data.mapping.network.NetworkTvShowMapper
import com.gkonstantakis.moviesapp.data.network.MovieNetworkService
import com.gkonstantakis.moviesapp.data.repositories.DetailsRepository
import com.gkonstantakis.moviesapp.data.repositories.SearchRepository
import com.gkonstantakis.moviesapp.data.utils.Constant
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MoviesApplication : Application() {

    lateinit var moviesDB: MoviesDatabase
    lateinit var moviesDao: MoviesDao
    lateinit var moviesNetworkService: MovieNetworkService
    lateinit var searchRepository: SearchRepository
    lateinit var detailsRepository: DetailsRepository

    override fun onCreate() {
        super.onCreate()
        moviesDB = Room
            .databaseBuilder(
                applicationContext,
                MoviesDatabase::class.java,
                MoviesDatabase.MOVIE_DATABASE
            )
            .build()
        moviesDao = (moviesDB as MoviesDatabase).moviesDao()
        moviesNetworkService =
            provideGsonBuilder().let {
                provideNetwork(it).build().create(MovieNetworkService::class.java)
            }
        searchRepository =
            moviesNetworkService.let {
                SearchRepository(
                    moviesDao,
                    moviesNetworkService,
                    NetworkSearchItemMapper(),
                    DatabaseWatchlistMapper()
                )
            }

        detailsRepository = moviesNetworkService.let {
            DetailsRepository(
                moviesDao,
                moviesNetworkService,
                NetworkMovieMapper(),
                NetworkTvShowMapper(),
                NetworkTrailersMapper(),
                DatabaseWatchlistMapper()
            )
        }
    }

    fun provideGsonBuilder(): Gson {
        return GsonBuilder().excludeFieldsWithoutExposeAnnotation().create()
    }

    fun provideNetwork(gson: Gson): Retrofit.Builder {
        return Retrofit.Builder()
            .baseUrl(Constant.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
    }
}