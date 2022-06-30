package com.application.moviex.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.application.moviex.models.MovieModel
import com.application.moviex.repositories.MovieRepository

class MovieListViewModel : ViewModel() {

    private var movieRepository: MovieRepository = MovieRepository.getInstance()

    fun getMovies(): LiveData<List<MovieModel>> {
        return movieRepository.getMovies()
    }

    fun getPopularMovies(): LiveData<List<MovieModel>> {
        return movieRepository.getPopularMovies()
    }

    fun searchMoviesApi(query: String, pageNumber: Int) {
        movieRepository.searchMoviesApi(query, pageNumber)
    }

    fun popularMoviesApi(pageNumber: Int) {
        movieRepository.popularMoviesApi(pageNumber)
    }

    fun searchNextpage() {
        movieRepository.searchNextPage()
    }

    fun popularNextpage() {
        movieRepository.popularNextPage()
    }
}