package com.lukakordzaia.streamflow.ui.baseclasses

import android.app.Dialog
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import androidx.leanback.app.RowsSupportFragment
import androidx.leanback.widget.ListRowPresenter
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.lukakordzaia.streamflow.R
import com.lukakordzaia.streamflow.helpers.TvCheckFirstItem
import com.lukakordzaia.streamflow.ui.phone.profile.ProfileFragment
import com.lukakordzaia.streamflow.ui.phone.profile.ProfileViewModel
import com.lukakordzaia.streamflow.ui.tv.TvActivity
import com.lukakordzaia.streamflow.ui.tv.categories.TvCategoriesActivity
import com.lukakordzaia.streamflow.ui.tv.favorites.TvFavoritesActivity
import com.lukakordzaia.streamflow.ui.tv.genres.TvSingleGenreActivity
import com.lukakordzaia.streamflow.ui.tv.search.TvSearchActivity
import com.lukakordzaia.streamflow.ui.tv.settings.TvSettingsActivity
import com.lukakordzaia.streamflow.utils.AppConstants
import com.lukakordzaia.streamflow.utils.createToast
import com.lukakordzaia.streamflow.utils.setGone
import com.lukakordzaia.streamflow.utils.setVisible
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.sync_continue_watching_alert_dialog.*
import kotlinx.android.synthetic.main.tv_sidebar.*
import org.koin.androidx.viewmodel.ext.android.viewModel

open class BaseFragmentActivity : FragmentActivity(), TvCheckFirstItem {
    private val profileViewModel: ProfileViewModel by viewModel()
    private var googleAccount: GoogleSignInAccount? = null
    private var googleSignInClient: GoogleSignInClient? = null
    protected val auth = Firebase.auth
    private lateinit var signInButton: View
    private lateinit var signOutButton: View
    private lateinit var profilePhoto: ImageView
    private lateinit var profileUsername: TextView

    private lateinit var currentButton: View

    private var doubleBackToExitPressedOnce = false
    private var isFirstItem = false
    private var rowsSupportFragment: RowsSupportFragment? = null
    private var rowsPosition: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        googleAccount = GoogleSignIn.getLastSignedInAccount(this)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

    }

    fun setSidebarClickListeners(search: View, home: View, favorites: View, movies: View, genres: View, settings: View) {
        search.setOnClickListener {
            startActivity(Intent(this, TvSearchActivity::class.java))
            tv_sidebar.setGone()
        }
        home.setOnClickListener {
            startActivity(Intent(this, TvActivity::class.java))
            tv_sidebar.setGone()
        }
        favorites.setOnClickListener {
            if (auth.currentUser != null) {
                startActivity(Intent(this, TvFavoritesActivity::class.java))
            } else {
                this.createToast("ფავორიტების სანახავად, გაიარეთ ავტორიზაცია")
            }
            tv_sidebar.setGone()
        }
        movies.setOnClickListener {
            val intent = Intent(this, TvCategoriesActivity::class.java)
            intent.putExtra("type", AppConstants.TV_CATEGORY_NEW_MOVIES)
            this.startActivity(intent)
            tv_sidebar.setGone()
        }
        genres.setOnClickListener {
            val intent = Intent(this, TvSingleGenreActivity::class.java)
            this.startActivity(intent)
            tv_sidebar.setGone()
        }
        settings.setOnClickListener {
            val intent = Intent(this, TvSettingsActivity::class.java)
            this.startActivity(intent)
            tv_sidebar.setGone()
        }
    }

    fun setCurrentButton(currentButton: View) {
        this.currentButton = currentButton
    }

    fun googleSignIn(view: View) {
        signInButton = view
        view.setOnClickListener {
            this.createToast("ავტორიზაცია")
            val signInIntent: Intent = googleSignInClient!!.signInIntent
            startActivityForResult(signInIntent, ProfileFragment.RC_SIGN_IN)
        }
    }

    fun googleSignOut(view: View) {
        signOutButton = view
        view.setOnClickListener {
            auth.signOut()
            googleSignInClient!!.signOut()
            updateGoogleUI(null)
            val intent = Intent(this, TvActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            this.startActivity(intent)
        }
    }

    fun googleProfileDetails(photo: ImageView, username: TextView) {
        profilePhoto = photo
        profileUsername = username

        if (auth.currentUser == null) {
            updateGoogleUI(null)
        } else {
            updateGoogleUI(auth.currentUser)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == ProfileFragment.RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                Log.d(ContentValues.TAG, "firebaseAuthWithGoogle:" + account.idToken)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Log.w(ContentValues.TAG, "Google sign in failed", e)
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Log.d(ContentValues.TAG, "signInWithCredential:success")
                        this.createToast("წარმატებით გაიარეთ ავტორიზაცია")
                        profileViewModel.createUserFirestore()

                        showSyncDialog()
                    } else {
                        Log.w(ContentValues.TAG, "signInWithCredential:failure", task.exception)
                        updateGoogleUI(null)
                    }
                }
    }

    private fun showSyncDialog() {
        profileViewModel.getContinueWatchingFromRoom(this).observe(this, {
            if (!it.isNullOrEmpty()) {
                val syncDialog = Dialog(this)
                syncDialog.setContentView(layoutInflater.inflate(R.layout.sync_continue_watching_alert_dialog, null))
                syncDialog.sync_continue_watching_alert_yes.setOnClickListener { _ ->
                    profileViewModel.addContinueWatchingToFirestore(this, it)
                    val intent = Intent(this, TvActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    this.startActivity(intent)

                }
                syncDialog.sync_continue_watching_alert_no.setOnClickListener {
                    syncDialog.dismiss()
                }
                syncDialog.show()
            } else {
                val intent = Intent(this, TvActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                this.startActivity(intent)
            }
        })
    }

    private fun updateGoogleUI(user: FirebaseUser?) {
        if (user != null) {
            profilePhoto.setVisible()

            if (googleAccount != null) {
                profileUsername.text = "გამარჯობა, ${googleAccount!!.givenName!!.toUpperCase()}"
                Picasso.get().load(googleAccount!!.photoUrl).into(profilePhoto)
            }

            signInButton.setGone()
            signOutButton.setVisible()

//            signOutButton.requestFocus()


        } else {
            profileUsername.text = ""
            profilePhoto.setGone()

            signInButton.setVisible()
            signOutButton.setGone()

//            signInButton.requestFocus()
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_DPAD_LEFT -> {
                if (isFirstItem) {
                    tv_sidebar.setVisible()
                    currentButton.requestFocus()
                }
            }
            KeyEvent.KEYCODE_DPAD_RIGHT -> {
                if (tv_sidebar.isVisible) {
                    tv_sidebar.setGone()
                    if (rowsSupportFragment != null && rowsPosition != null) {
                        rowsSupportFragment!!.setSelectedPosition(rowsPosition!!, true, ListRowPresenter.SelectItemViewHolderTask(0))
                    }
                }
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onBackPressed() {
        if (tv_sidebar.isVisible) {
            tv_sidebar.setGone()
        } else {
            tv_sidebar.setVisible()
            currentButton.requestFocus()
        }

        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }

        this.doubleBackToExitPressedOnce = true
        Handler(Looper.getMainLooper()).postDelayed({ doubleBackToExitPressedOnce = false }, 2000)

        if (this is TvActivity) {
            this.createToast("გასავლელად, კიდევ ერთხელ დააჭირეთ")
        }
    }

    override fun isFirstItem(boolean: Boolean, rowsSupportFragment: RowsSupportFragment?, rowsPosition: Int?) {
        isFirstItem = boolean
        this.rowsSupportFragment = rowsSupportFragment
        this.rowsPosition = rowsPosition
    }
}