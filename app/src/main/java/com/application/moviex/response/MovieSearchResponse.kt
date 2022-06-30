package com.application.moviex.response

import com.application.moviex.models.MovieModel
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

//This class is for getting multiple movies (movies list) - popular movies
class MovieSearchResponse {
    @SerializedName("total_results")
    @Expose
    private var total_count: Int = 0

    @SerializedName("results")
    @Expose
    private lateinit var movies: List<MovieModel>

    fun getTotal_Count(): Int {
        return total_count
    }

    fun getMovies(): List<MovieModel> {
        return movies
    }

    override fun toString(): String {
        return "MovieSearchResponse(total_count=$total_count, movies=$movies)"
    }


}