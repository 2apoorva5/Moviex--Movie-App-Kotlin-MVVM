package com.application.moviex

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.application.moviex.databinding.ActivityMovieDetailsBinding
import com.application.moviex.helper.LoadingDialog
import com.application.moviex.models.MovieModel
import com.application.moviex.notification.NotificationHelper
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.tapadoo.alerter.Alerter
import kotlin.math.roundToInt

class MovieDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMovieDetailsBinding
    private lateinit var loadingDialog: LoadingDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMovieDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false)
        window.statusBarColor = Color.TRANSPARENT

        loadingDialog = LoadingDialog(this@MovieDetailsActivity)

        getDataFromIntent()

        binding.back.setOnClickListener {
            onBackPressed()
        }
    }

    private fun getDataFromIntent() {
        if (intent.hasExtra("movie")) {
            var movieModel: MovieModel = intent.getParcelableExtra("movie")!!

            Glide.with(binding.movieImage.context)
                .load("https://image.tmdb.org/t/p/w500/" + movieModel.poster_path)
                .into(binding.movieImage)

            binding.movieTitle.text = movieModel.title
            binding.movieOverview.text = "Overview\n\n" + movieModel.overview
            binding.movieReleaseDate.text = "Release Date : " + movieModel.release_date
            binding.movieRatingBar.progress = ((movieModel.vote_average*10).roundToInt())
            binding.movieRatings.text = ":  Movie Ratings (${movieModel.vote_average})"

            binding.playBtn.setOnClickListener {
                NotificationHelper(this, movieModel.title).notification()
                val hideKeyboard =
                    getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                hideKeyboard.hideSoftInputFromWindow(currentFocus?.windowToken, 0)

                Alerter.create(this@MovieDetailsActivity)
                    .setTitle("Movie is Playing")
                    .setText(movieModel.title)
                    .setTextAppearance(R.style.AlertText)
                    .setBackgroundColorRes(R.color.successColor)
                    .setIcon(R.drawable.ic_notification)
                    .setDuration(2500)
                    .disableOutsideTouch()
                    .show()
            }

            binding.favoriteBtn.setOnClickListener {
                loadingDialog.startDialog()
                Toast.makeText(
                    this,
                    "Adding to favorites",
                    Toast.LENGTH_SHORT
                ).show()

                val favMovie = MovieModel(
                    movieModel.title,
                    movieModel.poster_path,
                    movieModel.release_date,
                    movieModel.id,
                    movieModel.vote_average,
                    movieModel.overview,
                    movieModel.runtime,
                    movieModel.original_language
                )

                Firebase.firestore.collection("Users")
                    .document(FirebaseAuth.getInstance().currentUser?.uid.toString())
                    .collection("Favorites")
                    .document(movieModel.id.toString())
                    .get()
                    .addOnSuccessListener { document ->
                        if (!document.exists()) {
                            Firebase.firestore.collection("Users")
                                .document(FirebaseAuth.getInstance().currentUser?.uid.toString())
                                .collection("Favorites")
                                .document(movieModel.id.toString())
                                .set(favMovie)
                                .addOnSuccessListener {
                                    loadingDialog.dismissDialog()
                                    Toast.makeText(
                                        this,
                                        "Added to favorites",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                                .addOnFailureListener {
                                    loadingDialog.dismissDialog()
                                    Toast.makeText(
                                        this,
                                        "ERROR occurred!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                        } else {
                            loadingDialog.dismissDialog()
                            Toast.makeText(
                                this,
                                "Already added",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    .addOnFailureListener {
                        loadingDialog.dismissDialog()
                        Toast.makeText(
                            this,
                            "ERROR occurred!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }

            binding.watchlistBtn.setOnClickListener {
                loadingDialog.startDialog()
                Toast.makeText(
                    this,
                    "Adding to watchlist",
                    Toast.LENGTH_SHORT
                ).show()

                val watchMovie = MovieModel(
                    movieModel.title,
                    movieModel.poster_path,
                    movieModel.release_date,
                    movieModel.id,
                    movieModel.vote_average,
                    movieModel.overview,
                    movieModel.runtime,
                    movieModel.original_language
                )

                Firebase.firestore.collection("Users")
                    .document(FirebaseAuth.getInstance().currentUser?.uid.toString())
                    .collection("Watchlist")
                    .document(movieModel.id.toString())
                    .get()
                    .addOnSuccessListener { document ->
                        if (!document.exists()) {
                            Firebase.firestore.collection("Users")
                                .document(FirebaseAuth.getInstance().currentUser?.uid.toString())
                                .collection("Watchlist")
                                .document(movieModel.id.toString())
                                .set(watchMovie)
                                .addOnSuccessListener {
                                    loadingDialog.dismissDialog()
                                    Toast.makeText(
                                        this,
                                        "Added to watchlist",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                                .addOnFailureListener {
                                    loadingDialog.dismissDialog()
                                    Toast.makeText(
                                        this,
                                        "ERROR occurred!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                        } else {
                            loadingDialog.dismissDialog()
                            Toast.makeText(
                                this,
                                "Already added",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    .addOnFailureListener {
                        loadingDialog.dismissDialog()
                        Toast.makeText(
                            this,
                            "ERROR occurred!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }
        }
    }

    private fun setWindowFlag(movieDetailsActivity: MovieDetailsActivity, bits: Int, on: Boolean) {
        val window: Window = movieDetailsActivity.window
        val layoutParams = window.attributes
        if (on) {
            layoutParams.flags = layoutParams.flags or bits
        } else {
            layoutParams.flags = layoutParams.flags and bits.inv()
        }
        window.attributes = layoutParams
    }
}