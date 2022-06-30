package com.application.moviex.adapters

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.application.moviex.R
import com.makeramen.roundedimageview.RoundedImageView
import per.wsj.library.AndRatingBar

class PopularMovieViewHolder(itemView: View, var onMovieClickListener: OnMovieClickListener) :
    RecyclerView.ViewHolder(itemView), View.OnClickListener {

    private var movieImage: RoundedImageView
    private var movieTitle: TextView
    private var movieOverview: TextView
    private var movieRatingBar: AndRatingBar

    init {
        movieImage = itemView.findViewById(R.id.movie_image)
        movieTitle = itemView.findViewById(R.id.movie_title)
        movieOverview = itemView.findViewById(R.id.movie_overview)
        movieRatingBar = itemView.findViewById(R.id.movie_rating_bar)

        itemView.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        onMovieClickListener.onMovieClick(adapterPosition)
    }
}