package com.application.moviex.repositories

import androidx.lifecycle.LiveData
import com.application.moviex.models.MovieModel
import com.application.moviex.request.MovieApiClient
import kotlin.properties.Delegates

class MovieRepository {
    companion object {
        private var instance: MovieRepository = MovieRepository()

        fun getInstance(): MovieRepository {
            return instance
        }
    }

    private var movieApiClient: MovieApiClient = MovieApiClient.getInstance()
    private var mQuery: String? = null
    private var mPageNumber by Delegates.notNull<Int>()

    fun getMovies(): LiveData<List<MovieModel>> {
        return movieApiClient.getMovies()
    }

    fun getPopularMovies(): LiveData<List<MovieModel>> {
        return movieApiClient.getMoviesPopular()
    }

    fun searchMoviesApi(query: String, pageNumber: Int) {
        mQuery = query
        mPageNumber = pageNumber

        movieApiClient.searchMoviesApi(query, pageNumber)
    }

    fun popularMoviesApi(pageNumber: Int) {
        mPageNumber = pageNumber

        movieApiClient.popularMoviesApi(pageNumber)
    }

    fun searchNextPage() {
        searchMoviesApi(mQuery!!, mPageNumber + 1)
    }

    fun popularNextPage() {
        popularMoviesApi(mPageNumber + 1)
    }
}