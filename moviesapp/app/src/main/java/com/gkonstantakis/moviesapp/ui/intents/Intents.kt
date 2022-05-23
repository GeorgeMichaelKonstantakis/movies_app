package com.gkonstantakis.moviesapp.ui.intents

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.gkonstantakis.moviesapp.ui.activities.DetailsActivity
import com.gkonstantakis.moviesapp.ui.activities.SearchActivity

class Intents {

    fun startDetailsActivity(context: Context,id: Int,isMovie: Boolean,isMovieInWatchlist: Boolean){
        val intent = Intent(context, DetailsActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION)
        intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
        intent.putExtra("MOVIE_ID", id)
        intent.putExtra("IS_MOVIE", isMovie)
        intent.putExtra("IS_MOVIE_IN_WATCHLIST", isMovieInWatchlist)
        try {
            ContextCompat.startActivity(context, intent, Bundle())
        } catch (e: Exception) {
        }
    }

    fun startSearchActivity(context: Context){
        val intent = Intent(context, SearchActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION)
        intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
        try {
            ContextCompat.startActivity(context, intent, Bundle())
        } catch (e: Exception) {
        }
    }
}