package com.application.moviex.request

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.application.moviex.AppExecutors
import com.application.moviex.models.MovieModel
import com.application.moviex.response.MovieSearchResponse
import com.application.moviex.utils.MovieCredentials
import retrofit2.Call
import retrofit2.Response
import java.io.IOException
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit

class MovieApiClient {
    //LiveData for Search
    var mMovies: MutableLiveData<List<MovieModel>> = MutableLiveData()

    //LiveData for popular movies
    var mMoviesPopular: MutableLiveData<List<MovieModel>> = MutableLiveData()

    //making search Runnable
    private var retrieveMoviesRunnable: RetrieveMoviesRunnable? = null

    //making popular Runnable
    private var retrieveMoviesRunnablePopular: RetrieveMoviesRunnablePopular? = null

    companion object {
        private var instance: MovieApiClient = MovieApiClient()

        fun getInstance(): MovieApiClient {
            return instance
        }
    }

    fun getMovies(): LiveData<List<MovieModel>> {
        return mMovies
    }

    fun getMoviesPopular(): LiveData<List<MovieModel>> {
        return mMoviesPopular
    }

    fun searchMoviesApi(query: String, pageNumber: Int) {
        if (retrieveMoviesRunnable != null) {
            retrieveMoviesRunnable = null
        }

        retrieveMoviesRunnable = RetrieveMoviesRunnable(query, pageNumber)

        val myHandler: Future<*>? =
            AppExecutors.getInstance().networkIO().submit(retrieveMoviesRunnable)

        AppExecutors.getInstance().networkIO().schedule({
            //Cancelling the retrofit call
            myHandler!!.cancel(true)
        }, 3000, TimeUnit.MILLISECONDS)
    }

    fun popularMoviesApi(pageNumber: Int) {
        if (retrieveMoviesRunnablePopular != null) {
            retrieveMoviesRunnablePopular = null
        }

        retrieveMoviesRunnablePopular = RetrieveMoviesRunnablePopular(pageNumber)

        val myHandler: Future<*>? =
            AppExecutors.getInstance().networkIO().submit(retrieveMoviesRunnablePopular)

        AppExecutors.getInstance().networkIO().schedule({
            //Cancelling the retrofit call
            myHandler!!.cancel(true)
        }, 1000, TimeUnit.MILLISECONDS)
    }

    //Retrieving search data from RESTApi by Runnable class
    private class RetrieveMoviesRunnable(
        var query: String,
        var pageNumber: Int,
        var cancelRequest: Boolean = false
    ) : Runnable {

        override fun run() {
            try {
                var response: Response<MovieSearchResponse> = getMovies(query, pageNumber).execute()

                if (cancelRequest) {
                    return
                }

                if (response.code() == 200) {
                    var list: List<MovieModel> = ArrayList((response.body())!!.getMovies())

                    if (pageNumber == 1) {
                        //sending data to LiveData
                        //PostValue : used for background thread
                        //setValue : not for background thread
                        getInstance().mMovies.postValue(list)
                    } else {
                        var currentMovies: List<MovieModel>? = getInstance().mMovies.value
                        currentMovies!!.toMutableList().add(list)
                        getInstance().mMovies.postValue(currentMovies)
                    }
                } else {
                    var error: String = response.errorBody().toString()
                    Log.v("Tag", "Error : $error")
                    getInstance().mMovies.postValue(null)
                }
            } catch (e: IOException) {
                e.printStackTrace()
                getInstance().mMovies.postValue(null)
            }
        }

        //Search method/query
        private fun getMovies(query: String, pageNumber: Int): Call<MovieSearchResponse> {
            return MovieService.getMovieApi().searchMovie(
                MovieCredentials.API_KEY,
                query,
                pageNumber
            )
        }

        private fun cancelRequest() {
            Log.v("Tag", "Cancelling search request..")
            cancelRequest = true
        }
    }

    //Retrieving poular data from RESTApi by Runnable class
    private class RetrieveMoviesRunnablePopular(
        var pageNumber: Int,
        var cancelRequest: Boolean = false
    ) : Runnable {

        override fun run() {
            try {
                var response: Response<MovieSearchResponse> = getPopularMovies(pageNumber).execute()

                if (cancelRequest) {
                    return
                }

                if (response.code() == 200) {
                    var list: List<MovieModel> = ArrayList((response.body())!!.getMovies())

                    if (pageNumber == 1) {
                        //sending data to LiveData
                        //PostValue : used for background thread
                        //setValue : not for background thread
                        getInstance().mMoviesPopular.postValue(list)
                    } else {
                        var currentMovies: List<MovieModel>? = getInstance().mMoviesPopular.value
                        currentMovies!!.toMutableList().add(list)
                        getInstance().mMoviesPopular.postValue(currentMovies)
                    }
                } else {
                    var error: String = response.errorBody().toString()
                    Log.v("Tag", "Error : $error")
                    getInstance().mMoviesPopular.postValue(null)
                }
            } catch (e: IOException) {
                e.printStackTrace()
                getInstance().mMoviesPopular.postValue(null)
            }
        }

        //Popular method/query
        private fun getPopularMovies(pageNumber: Int): Call<MovieSearchResponse> {
            return MovieService.getMovieApi().getPopularMovies(
                MovieCredentials.API_KEY,
                pageNumber
            )
        }

        private fun cancelRequest() {
            Log.v("Tag", "Cancelling search request..")
            cancelRequest = true
        }
    }
}

private fun <E> MutableList<E>.add(element: List<E>) {

}
