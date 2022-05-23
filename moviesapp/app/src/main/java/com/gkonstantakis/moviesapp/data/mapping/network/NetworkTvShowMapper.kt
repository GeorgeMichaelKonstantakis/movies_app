package com.gkonstantakis.moviesapp.data.mapping.network

import android.util.Log
import com.gkonstantakis.moviesapp.data.models.TvShow
import com.gkonstantakis.moviesapp.data.network.entities.tv_entity.TvNetworkEntity

class NetworkTvShowMapper {

    fun mapFromEntity(entity: TvNetworkEntity): TvShow {
        return TvShow(
            id = entity.id,
            originalTitle = entity.originalName,
            posterPath = entity.posterPath,
            firstAirDate = entity.firstAirDate,
            title = entity.name,
            voteAverage = entity.voteAverage,
            overview = entity.overview!!,
            genre = entity.genres?.get(0)?.name
        )
    }
}