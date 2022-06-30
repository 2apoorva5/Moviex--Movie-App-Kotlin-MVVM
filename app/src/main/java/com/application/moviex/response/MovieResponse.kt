package com.application.moviex.response

import com.application.moviex.models.MovieModel
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

//This class is for single movie request
class MovieResponse {
    @SerializedName("results")
    @Expose
    private lateinit var movie: MovieModel

    fun getMovie(): MovieModel {
        return movie
    }

    override fun toString(): String {
        return "MovieResponse(movie=$movie)"
    }


}