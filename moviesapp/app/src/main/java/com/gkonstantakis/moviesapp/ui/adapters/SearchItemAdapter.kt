package com.gkonstantakis.moviesapp.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gkonstantakis.moviesapp.R
import com.gkonstantakis.moviesapp.data.models.SearchResult
import com.gkonstantakis.moviesapp.data.repositories.SearchRepository
import com.gkonstantakis.moviesapp.data.utils.Constant
import com.gkonstantakis.moviesapp.ui.intents.Intents
import kotlinx.coroutines.runBlocking

class SearchItemAdapter(var listSearchItems: MutableList<SearchResult>,val searchRepository: SearchRepository):
    RecyclerView.Adapter<SearchItemAdapter.SearchListViewHolder>() {

    inner class SearchListViewHolder(searchListView: View) : RecyclerView.ViewHolder(searchListView)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SearchItemAdapter.SearchListViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.search_list_item, parent, false)
        return SearchListViewHolder(view)
    }

    override fun onBindViewHolder(holder: SearchItemAdapter.SearchListViewHolder, position: Int) {
        holder.itemView.apply {
            val searchItem = listSearchItems[position]
            var searchItemPath: String

            var isMovieInWatchlist = false
            var isMovie = searchItem.isMovie!!
            runBlocking {
                isMovieInWatchlist = searchRepository.isMovieInWatchlist(searchItem.id!!)
                if (isMovieInWatchlist) {
                    isMovie = searchRepository.isItemMovie(searchItem.id!!)
                }
            }

            if (searchItem.posterPath != null) {
                searchItemPath = Constant.IMAGE_URL + searchItem.posterPath

                Glide.with(context).load(searchItemPath)
                    .into(this.findViewById<ImageButton>(R.id.search_item_image));
            }

            this.findViewById<ImageButton>(R.id.search_item_image).setOnClickListener {
                Intents().startDetailsActivity(
                    context,
                    searchItem.id!!,
                    isMovie,
                    isMovieInWatchlist
                )
            }

            this.findViewById<TextView>(R.id.search_item_title).text = searchItem.title

            this.findViewById<TextView>(R.id.search_item_ratings).text =
                searchItem.voteAverage.toString()

            this.findViewById<TextView>(R.id.search_item_release_date).text =
                searchItem.releaseDate!!
        }
    }

    fun clearList(){
        listSearchItems.clear()
    }

    fun addNewItems(items: MutableList<SearchResult>){
        listSearchItems.addAll(items)
    }

    override fun getItemCount(): Int {
        return listSearchItems.size
    }
}