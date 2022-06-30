package com.application.moviex

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.application.moviex.databinding.ActivitySplashScreenBinding
import com.application.moviex.helper.LoadingDialog
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SplashScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashScreenBinding
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var firebaseAuth: FirebaseAuth
    private val db = Firebase.firestore
    private lateinit var loadingDialog: LoadingDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false)
        window.statusBarColor = Color.TRANSPARENT

        loadingDialog = LoadingDialog(this@SplashScreenActivity)

        //configure Google Sign In
        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)

        //init Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance()
        checkUser()

        //Google Login Button
        binding.googleLoginBtn.setOnClickListener {
            val intent = googleSignInClient.signInIntent
            signInLauncher.launch(intent)
        }
    }

    private fun checkUser() {
        //check if user is already logged in
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser != null) {
            //user already logged in
            //start MainActivity
            startActivity(
                Intent(this@SplashScreenActivity, MainActivity::class.java)
            )
            finish()
        }
    }

    private val signInLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account)
            } catch (e: ApiException) {
                Toast.makeText(
                    this@SplashScreenActivity,
                    "Some ERROR occurred!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount?) {
        loadingDialog.startDialog()
        val credential = GoogleAuthProvider.getCredential(account!!.idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnSuccessListener {
                //check if user is new or existing
                if (it.additionalUserInfo!!.isNewUser) {
                    val user = hashMapOf(
                        "name" to firebaseAuth.currentUser?.displayName.toString(),
                        "image" to firebaseAuth.currentUser?.photoUrl.toString(),
                        "email" to firebaseAuth.currentUser?.email.toString(),
                        "uid" to firebaseAuth.currentUser?.uid.toString()
                    )

                    db.collection("Users")
                        .document(firebaseAuth.currentUser?.uid.toString())
                        .set(user)
                        .addOnSuccessListener {
                            Toast.makeText(
                                this@SplashScreenActivity,
                                "Created account",
                                Toast.LENGTH_SHORT
                            ).show()

                            //start MainActivity
                            loadingDialog.dismissDialog()
                            startActivity(
                                Intent(this@SplashScreenActivity, MainActivity::class.java)
                            )
                            finish()
                        }
                        .addOnFailureListener {
                            loadingDialog.dismissDialog()
                            Toast.makeText(
                                this@SplashScreenActivity,
                                "Failed creating account",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                } else {
                    Toast.makeText(
                        this@SplashScreenActivity,
                        "Logging In..",
                        Toast.LENGTH_SHORT
                    ).show()

                    //start MainActivity
                    loadingDialog.dismissDialog()
                    startActivity(
                        Intent(this@SplashScreenActivity, MainActivity::class.java)
                    )
                    finish()
                }
            }
            .addOnFailureListener {
                loadingDialog.dismissDialog()
                Toast.makeText(
                    this@SplashScreenActivity,
                    "Login failed due to ${it.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun setWindowFlag(splashScreenActivity: SplashScreenActivity, bits: Int, on: Boolean) {
        val window: Window = splashScreenActivity.window
        val layoutParams = window.attributes
        if (on) {
            layoutParams.flags = layoutParams.flags or bits
        } else {
            layoutParams.flags = layoutParams.flags and bits.inv()
        }
        window.attributes = layoutParams
    }
}