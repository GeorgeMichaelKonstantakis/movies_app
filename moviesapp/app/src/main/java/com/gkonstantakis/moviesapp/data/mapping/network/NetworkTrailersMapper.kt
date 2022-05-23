package com.gkonstantakis.moviesapp.data.mapping.network

import com.gkonstantakis.moviesapp.data.models.Trailer
import com.gkonstantakis.moviesapp.data.network.entities.videos_entity.VideosEntity

class NetworkTrailersMapper {

    fun mapFromListOfEntities(entity: VideosEntity): List<Trailer> {
        val trailers = ArrayList<Trailer>()
        val results = entity.results
        if (!results.isNullOrEmpty()) {
            val result = results[0]
            trailers.add(
                Trailer(
                    id = result.id,
                    key = result.key,
                    site = result.site,
                    name = result.name
                )
            )
        }
        return trailers
    }
}