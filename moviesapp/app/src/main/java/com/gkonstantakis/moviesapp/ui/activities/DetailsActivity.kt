package com.gkonstantakis.moviesapp.ui.activities

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.gkonstantakis.moviesapp.MoviesApplication
import com.gkonstantakis.moviesapp.R
import com.gkonstantakis.moviesapp.data.models.Movie
import com.gkonstantakis.moviesapp.data.models.SearchResult
import com.gkonstantakis.moviesapp.data.models.Trailer
import com.gkonstantakis.moviesapp.data.models.TvShow
import com.gkonstantakis.moviesapp.data.state.DataState
import com.gkonstantakis.moviesapp.data.utils.Constant
import com.gkonstantakis.moviesapp.ui.intents.Intents
import com.gkonstantakis.moviesapp.ui.utils.DetailsEventParams
import com.gkonstantakis.moviesapp.ui.view_models.DetailsStateEvent
import com.gkonstantakis.moviesapp.ui.view_models.DetailsViewModel
import java.util.ArrayList


class DetailsActivity : AppCompatActivity() {

    private lateinit var moviesApplication: MoviesApplication
    private var viewModel: DetailsViewModel? = null

    private var id: Int? = null
    private var isMovie = false
    private var isMovieInWatchlist = false

    private var dismissButton: Button? = null
    private var movieImage: ImageView? = null
    private var movieTitle: TextView? = null
    private var movieGenre: TextView? = null
    private var movieOverview: TextView? = null
    private var movieTrailer: WebView? = null
    private var watchlistButton: Button? = null

    private var movie: Movie? = null
    private var tvShow: TvShow? = null
    private var trailer: Trailer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        moviesApplication = (application as MoviesApplication)

        viewModel = DetailsViewModel(moviesApplication.detailsRepository)

        id = intent.getIntExtra("MOVIE_ID", 0)
        isMovie = intent.getBooleanExtra("IS_MOVIE", false)
        isMovieInWatchlist = intent.getBooleanExtra("IS_MOVIE_IN_WATCHLIST", false)

        subscribeObservers(this)

        movieImage = findViewById(R.id.movie_thumbnail_image) as ImageView
        movieTitle = findViewById(R.id.movie_title) as TextView
        movieGenre = findViewById(R.id.movie_genre) as TextView
        movieOverview = findViewById(R.id.movie_overview) as TextView
        movieTrailer = findViewById(R.id.movie_trailer) as WebView

        if(isMovie){
            viewModel!!.setStateEvent(
                DetailsStateEvent.NetworkGetMovie,
                DetailsEventParams(id, null)
            )
        } else{
            viewModel!!.setStateEvent(
                DetailsStateEvent.NetworkGetTvShow,
                DetailsEventParams(id, null)
            )
        }

        initWatchlistButton()

        dismissButton = findViewById<Button>(R.id.dismiss_movie)
        dismissButton?.setOnClickListener {
            Intents().startSearchActivity(this)
        }
    }

    private fun subscribeObservers(context: Context) {
        viewModel?.movieDataState?.observe(this, Observer { datastate ->
            when (datastate) {
                is DataState.SuccessNetworkMovie<List<Movie>> -> {
                    movie = datastate.data[0]
                    displayMovieInfo(movie!!)
                    viewModel!!.setStateEvent(
                        DetailsStateEvent.NetworkGetMovieTrailers,
                        DetailsEventParams(id, null)
                    )
                }
            }
        })

        viewModel?.tvDataState?.observe(this, Observer { datastate ->
            when (datastate) {
                is DataState.SuccessNetworkTvShow<List<TvShow>> -> {
                    tvShow = datastate.data[0]
                    displayTvShowInfo(tvShow!!)
                    viewModel!!.setStateEvent(
                        DetailsStateEvent.NetworkGetTvShowTrailers,
                        DetailsEventParams(id, null)
                    )
                }
            }
        })

        viewModel?.trailersDataState?.observe(this, Observer { datastate ->
            when (datastate) {
                is DataState.SuccessNetworkTrailers<List<Trailer>> -> {
                    val trailerKey = getYoutubeTrailerKey(datastate.data)
                    if (trailerKey != null) {
                        displayVideo(trailerKey)
                    }
                }
            }
        })

        viewModel?.searchDataState?.observe(this, Observer { datastate ->
            when (datastate) {
                is DataState.SuccessInsertMovieToWatchlist<List<SearchResult>> -> {
                    changeWatchlistButtonStyle(WatchlistUserAction.ADD_TO_WATCHLIST)
                }
                is DataState.SuccessDeleteMovieFromWatchlist<List<SearchResult>> -> {
                    changeWatchlistButtonStyle(WatchlistUserAction.DELETE_FROM_WATCHLIST)
                }
            }
        })

    }

    fun initWatchlistButton() {
        watchlistButton = findViewById(R.id.watchlist_button)
        if (isMovieInWatchlist) {
            watchlistButton?.text = resources.getString(R.string.remove_movie_from_watchlist_text)
            watchlistButton?.background =
                resources.getDrawable(R.drawable.remove_from_watchlist_button)
        } else {
            watchlistButton?.background = resources.getDrawable(R.drawable.add_to_watchlist_button)
            watchlistButton?.text = resources.getString(R.string.add_movie_to_watchlist_text)
        }
        watchlistButton?.setOnClickListener {
            var watchlist: SearchResult
            if (isMovie) {
                watchlist = SearchResult(
                    id = movie!!.id,
                    posterPath = movie!!.posterPath,
                    releaseDate = movie!!.releaseDate,
                    title = movie!!.title,
                    voteAverage = movie!!.voteAverage,
                    isMovie = true
                )
            } else {
                watchlist = SearchResult(
                    id = tvShow?.id,
                    posterPath = tvShow?.posterPath,
                    releaseDate = tvShow?.firstAirDate,
                    title = tvShow?.title,
                    voteAverage = tvShow?.voteAverage,
                    isMovie = false
                )
            }
            if (isMovieInWatchlist) {
                viewModel!!.setStateEvent(
                    DetailsStateEvent.DeleteFromWatchlist,
                    DetailsEventParams(null, watchlist)
                )
            } else {
                viewModel!!.setStateEvent(
                    DetailsStateEvent.InsertToWatchlist,
                    DetailsEventParams(null, watchlist)
                )
            }
        }
    }

    fun changeWatchlistButtonStyle(action: WatchlistUserAction){
        if(action == WatchlistUserAction.ADD_TO_WATCHLIST){
            watchlistButton?.text = resources.getString(R.string.remove_movie_from_watchlist_text)
            watchlistButton?.background =
                resources.getDrawable(R.drawable.remove_from_watchlist_button)
            isMovieInWatchlist = true
        } else {
            watchlistButton?.background = resources.getDrawable(R.drawable.add_to_watchlist_button)
            watchlistButton?.text = resources.getString(R.string.add_movie_to_watchlist_text)
            isMovieInWatchlist = false
        }
    }

    fun displayMovieInfo(movie: Movie) {
        val moviePath: String
        if (movie.posterPath != null) {
            moviePath = Constant.IMAGE_URL + movie.posterPath

            Glide.with(this).load(moviePath)
                .into(this.findViewById<ImageButton>(R.id.movie_thumbnail_image));
        }

        movieTitle?.text = movie.title
        movieGenre?.text = movie.genre
        movieOverview?.text = movie.overview
    }

    fun displayTvShowInfo(tvShow: TvShow) {
        val tvShowPath: String
        if (tvShow.posterPath != null) {
            tvShowPath = Constant.IMAGE_URL + tvShow.posterPath

            Glide.with(this).load(tvShowPath)
                .into(this.findViewById<ImageButton>(R.id.movie_thumbnail_image));
        }

        movieTitle?.text = tvShow.title
        movieGenre?.text = tvShow.genre
        movieOverview?.text = tvShow.overview
    }

    fun displayVideo(key: String){
        val videoStr =
            "<html><body><iframe ${trailerDimensions()} src=\"https://www.youtube.com/embed/${key}\" frameborder=\"0\" allowfullscreen></iframe></body></html>"

        movieTrailer?.setWebViewClient(object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                return false
            }
        })
        val ws: WebSettings = movieTrailer?.getSettings()!!
        ws.javaScriptEnabled = true
        movieTrailer?.loadData(videoStr, "text/html", "utf-8")
    }

    fun getYoutubeTrailerKey(trailers: List<Trailer>?): String? {
        var trailerKey: String? = null
        try {
            if (!trailers.isNullOrEmpty()) {
                for (trailer in trailers) {
                    if (trailer.site == "YouTube") {
                        trailerKey = trailer.key
                        break
                    }
                }
            }
        } catch (e: Exception) {
        }
        return trailerKey
    }

    fun trailerDimensions(): String{
        val height = 315
        val width = 420

        val aspectRatio: Double = (width.toDouble()/ height.toDouble())

        val viewResources = this.resources
        var widthScreenPixels = (viewResources.displayMetrics.widthPixels)
        var heightScreenPixels = (viewResources.displayMetrics.heightPixels)
        val scale: Double = (viewResources!!.displayMetrics.density).toDouble()

        var viewMarginHorizontal: Double = (viewResources.getDimension(R.dimen._20sdp).toDouble())

        val finalWidth = widthScreenPixels - viewMarginHorizontal
        val finalHeight = (((1/aspectRatio).toDouble())*(finalWidth.toDouble())).toInt()

        return "width=\"${finalWidth/scale}\" height=\"${finalHeight/scale}\""
    }

    enum class WatchlistUserAction {
        DELETE_FROM_WATCHLIST, ADD_TO_WATCHLIST
    }
}