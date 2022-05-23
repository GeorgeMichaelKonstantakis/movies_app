package com.gkonstantakis.moviesapp.data.mapping.database

import com.gkonstantakis.moviesapp.data.database.entities.Watchlist
import com.gkonstantakis.moviesapp.data.models.SearchResult

class DatabaseWatchlistMapper {

    fun mapToEntity(searchResult: SearchResult): Watchlist{
        return Watchlist(
            id = searchResult.id!!,
            posterPath = searchResult.posterPath!!,
            releaseDate = searchResult.releaseDate!!,
            title = searchResult.title,
            voteAverage = searchResult.voteAverage?.toDouble(),
            isMovie = searchResult.isMovie
        )
    }

    fun mapFromListOfEntities(entities: List<Watchlist>): List<SearchResult> {
        val searchItems = ArrayList<SearchResult>()
        for (entity in entities) {
            searchItems.add(
                SearchResult(
                    id = entity.id,
                    posterPath = entity.posterPath,
                    releaseDate = entity.releaseDate,
                    title = entity.title,
                    voteAverage = entity.voteAverage,
                    isMovie = entity.isMovie
                )
            )
        }
        return searchItems
    }
}