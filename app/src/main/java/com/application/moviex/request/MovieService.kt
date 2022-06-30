package com.application.moviex.request

import com.application.moviex.utils.MovieApi
import com.application.moviex.utils.MovieCredentials
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MovieService {
    companion object {
        private val retrofitBuilder: Retrofit.Builder =
            Retrofit.Builder()
                .baseUrl(MovieCredentials.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())

        private val retrofit: Retrofit = retrofitBuilder.build()

        private val movieApi: MovieApi = retrofit.create(MovieApi::class.java)

        fun getMovieApi(): MovieApi {
            return movieApi
        }
    }
}