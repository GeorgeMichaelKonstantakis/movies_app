package com.gkonstantakis.moviesapp.data.network.entities.tv_entity.inner_objects

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Networks(

    @SerializedName("name")
    @Expose
    var name: String?,

    @SerializedName("id")
    @Expose
    var id: Int?,

    @SerializedName("logo_path")
    @Expose
    var logoPath: String?,

    @SerializedName("origin_country")
    @Expose
    var originCountry: String?
) {
}