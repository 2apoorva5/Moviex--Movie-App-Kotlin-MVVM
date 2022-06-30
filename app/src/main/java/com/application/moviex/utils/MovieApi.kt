package com.application.moviex.utils

import com.application.moviex.response.MovieSearchResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface MovieApi {
    //Search for movies
    //https://api.themoviedb.org/3/search/movie?api_key={api_key}&query={query}&page={page}
    @GET("/3/search/movie?")
    fun searchMovie(
        @Query("api_key") key: String,
        @Query("query") query: String,
        @Query("page") page: Int
    ): Call<MovieSearchResponse>

    //Get popular movies
    //https://api.themoviedb.org/3/movie/popular?api_key={api_key}&page={page}
    @GET("/3/movie/popular?")
    fun getPopularMovies(
        @Query("api_key") key: String,
        @Query("page") page: Int
    ): Call<MovieSearchResponse>
}