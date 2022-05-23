package com.gkonstantakis.moviesapp.data.mapping.network

import com.gkonstantakis.moviesapp.data.models.Movie
import com.gkonstantakis.moviesapp.data.network.entities.movie_entity.MovieNetworkEntity

class NetworkMovieMapper {

    fun mapFromEntity(entity: MovieNetworkEntity): Movie {
        return Movie(
            id = entity.id,
            originalTitle = entity.originalTitle,
            posterPath = entity.posterPath,
            releaseDate = entity.releaseDate,
            title = entity.title,
            voteAverage = entity.voteAverage,
            overview = entity.overview,
            genre = entity.genres?.get(0)?.name
        )
    }
}