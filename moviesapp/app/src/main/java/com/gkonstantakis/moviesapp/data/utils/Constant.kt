package com.gkonstantakis.moviesapp.data.utils

class Constant {

    companion object {
        const val BASE_URL = "https://api.themoviedb.org/3/"
        const val IMAGE_URL = "https://image.tmdb.org/t/p/w500/"
        const val YOUTUBE_VIDEO_URL = "https://www.youtube.com/watch"
        const val API_KEY = "6b2e856adafcc7be98bdf0d8b076851c"
        const val MIN_PAGE_NUM = 1
        const val MAX_PAGE_NUM = 1000

        fun youtubeTrailer(key: String): String {
            return "$YOUTUBE_VIDEO_URL?v=$key"
        }
    }
}