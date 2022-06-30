package com.application.moviex

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.speech.RecognizerIntent
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.application.moviex.adapters.MovieRecyclerAdapter
import com.application.moviex.adapters.OnMovieClickListener
import com.application.moviex.databinding.ActivityMainBinding
import com.application.moviex.helper.LoadingDialog
import com.application.moviex.viewmodels.MovieListViewModel
import com.blogspot.atifsoftwares.animatoolib.Animatoo
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.tapadoo.alerter.Alerter
import dev.shreyaspatil.MaterialDialog.MaterialDialog
import dev.shreyaspatil.MaterialDialog.interfaces.DialogInterface
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent.setEventListener
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener
import java.util.*

class MainActivity : AppCompatActivity(), OnMovieClickListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var movieListViewModel: MovieListViewModel
    private var movieRecyclerAdapter: MovieRecyclerAdapter = MovieRecyclerAdapter(this)
    private var isPopular: Boolean = true
    private lateinit var loadingDialog: LoadingDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = getColor(R.color.colorBackground)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        loadingDialog = LoadingDialog(this@MainActivity)

        //init Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance()
        checkUser()

        setupSearchView()

        movieListViewModel = ViewModelProvider(this)[MovieListViewModel::class.java]
        //getting popular movies
        movieListViewModel.popularMoviesApi(1)

        observeAnyChange()
        observePopularChange()
        configureRecyclerView()

        binding.btnLogout.setOnClickListener {
            val materialDialog = MaterialDialog.Builder(this@MainActivity)
                .setTitle("Log out of Moviex?")
                .setMessage("Are you sure of logging out of Moviex?")
                .setCancelable(false)
                .setPositiveButton(
                    "Yes", R.drawable.ic_dialog_okay
                ) { dialogInterface: DialogInterface, which: Int ->
                    dialogInterface.dismiss()
                    Toast.makeText(this@MainActivity, "Signing out", Toast.LENGTH_SHORT).show()
                    firebaseAuth.signOut()
                    checkUser()
                }
                .setNegativeButton(
                    "Cancel", R.drawable.ic_dialog_cancel
                ) { dialogInterface: DialogInterface, which: Int -> dialogInterface.dismiss() }
                .build()
            materialDialog.show()
        }

        binding.bottomNavigation.selectedItemId = R.id.menu_home
        binding.bottomNavigation.itemIconTintList = null
        binding.bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_home -> {}
                R.id.menu_favorite -> {
                    startActivity(Intent(this@MainActivity, FavoritesActivity::class.java))
                    overridePendingTransition(0, 0)
                    finish()
                }
                R.id.menu_watchlist -> {
                    startActivity(Intent(this@MainActivity, WatchlistActivity::class.java))
                    overridePendingTransition(0, 0)
                    finish()
                }
            }
            false
        }
    }

    private fun checkUser() {
        //check if user is logged in
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser == null) {
            //user not logged in
            //start SplashScreenActivity
            startActivity(
                Intent(this@MainActivity, SplashScreenActivity::class.java)
            )
            finish()
        } else {
            //Set user name
            val name: String = firebaseUser.displayName.toString()
            val splitName = name.split(" ").toTypedArray()
            val emoji = 0x1F44B
            binding.greeting.text =
                String.format("Hi, %s %s", splitName[0], String(Character.toChars(emoji)))

            //Set user image
            Glide.with(this@MainActivity)
                .load(Uri.parse(firebaseUser.photoUrl.toString()))
                .into(binding.userImage)
        }
    }

    private fun setupSearchView() {
        setEventListener(this@MainActivity,
            KeyboardVisibilityEventListener { isOpen: Boolean ->
                if (!isOpen) {
                    binding.searchInput.clearFocus()
                }
            })

        binding.searchInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                movieListViewModel.searchMoviesApi(
                    s.toString(),
                    1
                )
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                movieListViewModel.searchMoviesApi(
                    s.toString(),
                    1
                )
            }
        })

        binding.micBtn.setOnClickListener {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            intent.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, String.format("Name a doctor.."))
            try {
                activityResultLauncher.launch(intent)
            } catch (e: ActivityNotFoundException) {
                Alerter.create(this@MainActivity)
                    .setText("Uh oh! Something broke. Try again!")
                    .setTextAppearance(R.style.AlertText)
                    .setBackgroundColorRes(R.color.errorColor)
                    .setDuration(2500)
                    .disableOutsideTouch()
                    .show()
            }
        }

        binding.searchInput.setOnClickListener {
            isPopular = false
        }
    }

    var activityResultLauncher = registerForActivityResult(
        StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK && result.data != null) {
            val input = result.data!!.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            binding.searchInput.setText(input!![0])
            binding.searchInput.clearFocus()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun configureRecyclerView() {
        movieRecyclerAdapter.notifyDataSetChanged()
        binding.recyclerMovies.layoutManager = WrapContentLinearLayoutManager(
            this@MainActivity,
            LinearLayoutManager.VERTICAL,
            false
        )
        binding.recyclerMovies.adapter = movieRecyclerAdapter

        binding.recyclerMovies.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (!binding.recyclerMovies.canScrollVertically(1)) {
                    if (isPopular) {
                        movieListViewModel.popularNextpage()
                    } else {
                        movieListViewModel.searchNextpage()
                    }
                }
            }
        })
    }

    //observing any data change
    @SuppressLint("NotifyDataSetChanged")
    private fun observeAnyChange() {
        movieListViewModel.getMovies().observe(this) {
            if (it != null) {
                for (movieModel in it) {
                    Log.v("Tag", "onChanged : ${movieModel.title}")

                    movieRecyclerAdapter.setmMovies(it)
                    movieRecyclerAdapter.notifyDataSetChanged()
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun observePopularChange() {
        movieListViewModel.getPopularMovies().observe(this) {
            if (it != null) {
                for (movieModel in it) {
                    Log.v("Tag", "onChanged : ${movieModel.title}")

                    movieRecyclerAdapter.setmMovies(it)
                    movieRecyclerAdapter.notifyDataSetChanged()
                }
            }
        }
    }

    override fun onMovieClick(position: Int) {
        startActivity(
            Intent(this@MainActivity, MovieDetailsActivity::class.java)
                .putExtra("movie", movieRecyclerAdapter.getSelectedMovie(position))
        )
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