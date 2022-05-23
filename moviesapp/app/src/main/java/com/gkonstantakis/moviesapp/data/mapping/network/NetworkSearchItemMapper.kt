package com.gkonstantakis.moviesapp.data.mapping.network

import com.gkonstantakis.moviesapp.data.models.SearchResult
import com.gkonstantakis.moviesapp.data.network.entities.search_entity.inner_objects.Result

class NetworkSearchItemMapper {

    fun mapFromListOfEntities(entities: List<Result>): List<SearchResult> {
        val searchItems = ArrayList<SearchResult>()
        for (result in entities) {
            val titleMovie: String
            var isMovie = false
            if(result.title != null){
                titleMovie = result.title!!
                isMovie = true
            } else {
                titleMovie = result.name!!
                isMovie = false
            }

            if(result.releaseDate != null){
                searchItems.add(
                    SearchResult(
                        id = result.id!!,
                        posterPath = result.posterPasth,
                        releaseDate = result.releaseDate!!,
                        title = titleMovie,
                        voteAverage = result.voteAverage!!,
                        isMovie = isMovie
                    )
                )
            } else if(result.firstAirDate != null){
                searchItems.add(
                    SearchResult(
                        id = result.id!!,
                        posterPath = result.posterPasth,
                        releaseDate = result.firstAirDate!!,
                        title = titleMovie,
                        voteAverage = result.voteAverage!!,
                        isMovie = isMovie
                    )
                )
            }
        }
        return searchItems
    }
}