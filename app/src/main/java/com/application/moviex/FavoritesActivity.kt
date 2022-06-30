package com.application.moviex

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.application.moviex.adapters.FavoriteMovieViewHolder
import com.application.moviex.databinding.ActivityFavoritesBinding
import com.application.moviex.models.MovieModel
import com.bumptech.glide.Glide
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.tapadoo.alerter.Alerter
import kotlinx.android.synthetic.main.search_movie_list_item.view.*

class FavoritesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFavoritesBinding

    private var favMovieAdapter: FirestoreRecyclerAdapter<MovieModel, FavoriteMovieViewHolder>? =
        null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoritesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = getColor(R.color.colorBackground)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        loadFavoriteMovies()

        binding.bottomNavigation.selectedItemId = R.id.menu_favorite
        binding.bottomNavigation.itemIconTintList = null
        binding.bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_home -> {
                    startActivity(Intent(this@FavoritesActivity, MainActivity::class.java))
                    overridePendingTransition(0, 0)
                    finish()
                }
                R.id.menu_favorite -> {}
                R.id.menu_watchlist -> {
                    startActivity(Intent(this@FavoritesActivity, WatchlistActivity::class.java))
                    overridePendingTransition(0, 0)
                    finish()
                }
            }
            false
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun loadFavoriteMovies() {
        val query: Query = FirebaseFirestore.getInstance().collection("Users")
            .document(FirebaseAuth.getInstance().currentUser?.uid.toString())
            .collection("Favorites")

        val options: FirestoreRecyclerOptions<MovieModel> =
            FirestoreRecyclerOptions.Builder<MovieModel>()
                .setLifecycleOwner(this)
                .setQuery(query, MovieModel::class.java)
                .build()

        favMovieAdapter =
            object : FirestoreRecyclerAdapter<MovieModel, FavoriteMovieViewHolder>(options) {

                override fun onCreateViewHolder(
                    parent: ViewGroup,
                    viewType: Int
                ): FavoriteMovieViewHolder {
                    val view: View = LayoutInflater.from(parent.context)
                        .inflate(R.layout.favorite_movie_list_item, parent, false)
                    return FavoriteMovieViewHolder(view)
                }

                override fun onBindViewHolder(
                    holder: FavoriteMovieViewHolder,
                    position: Int,
                    model: MovieModel
                ) {
                    Glide.with(holder.itemView.context)
                        .load(Uri.parse("https://image.tmdb.org/t/p/w500/" + model.poster_path))
                        .into(holder.itemView.movie_image)

                    holder.itemView.movie_title.text = model.title
                    holder.itemView.movie_overview.text = model.overview

                    holder.itemView.movie_rating_bar.rating = (model.vote_average / 2)

                    holder.itemView.setOnClickListener {
                        startActivity(
                            Intent(this@FavoritesActivity, MovieDetailsActivity::class.java)
                                .putExtra("movie", model)
                        )
                    }
                }

                override fun onError(e: FirebaseFirestoreException) {
                    super.onError(e)
                    Alerter.create(this@FavoritesActivity)
                        .setText("Uh oh! Something broke. Try again!")
                        .setTextAppearance(R.style.AlertText)
                        .setBackgroundColorRes(R.color.errorColor)
                        .setDuration(2500)
                        .disableOutsideTouch()
                        .show()
                }
            }

        (favMovieAdapter as FirestoreRecyclerAdapter<MovieModel, FavoriteMovieViewHolder>).notifyDataSetChanged()
        binding.recyclerMovies.setHasFixedSize(true)
        binding.recyclerMovies.layoutManager = WrapContentLinearLayoutManager(
            this@FavoritesActivity,
            LinearLayoutManager.VERTICAL,
            false
        )
        binding.recyclerMovies.adapter = favMovieAdapter
    }

    class WrapContentLinearLayoutManager : LinearLayoutManager {
        constructor(context: Context?) : super(context)
        constructor(context: Context?, orientation: Int, reverseLayout: Boolean) : super(
            context,
            orientation,
            reverseLayout
        )

        constructor(
            context: Context?,
            attrs: AttributeSet?,
            defStyleAttr: Int,
            defStyleRes: Int
        ) : super(context, attrs, defStyleAttr, defStyleRes)

        override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
            try {
                super.onLayoutChildren(recycler, state)
            } catch (e: IndexOutOfBoundsException) {
                Log.e("Error", "IndexOutOfBoundsException in RecyclerView happens")
            }
        }
    }
}