package com.application.moviex.adapters

import android.annotation.SuppressLint
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.application.moviex.R
import com.application.moviex.models.MovieModel
import com.application.moviex.utils.MovieCredentials
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.search_movie_list_item.view.*

class MovieRecyclerAdapter(
    private var onMovieClickListener: OnMovieClickListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mMovies: List<MovieModel>? = null

    companion object {
        private const val DISPLAY_POPULAR: Int = 1
        private const val DISPLAY_SEARCH: Int = 2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        lateinit var view: View
        return if (viewType == DISPLAY_SEARCH) {
            view = LayoutInflater.from(parent.context)
                .inflate(R.layout.search_movie_list_item, parent, false)
            SearchMovieViewHolder(view, onMovieClickListener)
        } else {
            view = LayoutInflater.from(parent.context)
                .inflate(R.layout.popular_movie_list_item, parent, false)
            PopularMovieViewHolder(view, onMovieClickListener)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == DISPLAY_SEARCH) {
            Glide.with(holder.itemView.context)
                .load(Uri.parse("https://image.tmdb.org/t/p/w500/" + mMovies!![position].poster_path))
                .into(holder.itemView.movie_image)

            holder.itemView.movie_title.text = mMovies!![position].title
            holder.itemView.movie_overview.text = mMovies!![position].overview

            holder.itemView.movie_rating_bar.rating = (mMovies!![position].vote_average / 2)
        } else {
            Glide.with(holder.itemView.context)
                .load(Uri.parse("https://image.tmdb.org/t/p/w500/" + mMovies!![position].poster_path))
                .into(holder.itemView.movie_image)

            holder.itemView.movie_title.text = mMovies!![position].title
            holder.itemView.movie_overview.text = mMovies!![position].overview

            holder.itemView.movie_rating_bar.rating = (mMovies!![position].vote_average / 2)
        }
    }

    override fun getItemCount(): Int {
        return if (mMovies != null) {
            mMovies!!.size
        } else {
            0
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setmMovies(mMovies: List<MovieModel>) {
        this.mMovies = mMovies
        notifyDataSetChanged()
    }

    fun getSelectedMovie(position: Int): MovieModel? {
        if (mMovies != null) {
            if (mMovies!!.isNotEmpty()) {
                return mMovies!![position]
            }
        }
        return null
    }

    override fun getItemViewType(position: Int): Int {
        return if (MovieCredentials.POPULAR) {
            DISPLAY_POPULAR
        } else {
            DISPLAY_SEARCH
        }
    }
}