package com.gkonstantakis.moviesapp.ui.utils

import com.gkonstantakis.moviesapp.data.models.SearchResult


data class DetailsEventParams(
    var id: Int?,
    var searchResult: SearchResult?
){
}