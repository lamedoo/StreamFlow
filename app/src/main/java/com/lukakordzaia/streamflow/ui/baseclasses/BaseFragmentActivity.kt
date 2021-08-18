package com.lukakordzaia.streamflow.ui.baseclasses

import android.app.Dialog
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import androidx.leanback.app.RowsSupportFragment
import androidx.leanback.widget.ListRowPresenter
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.lukakordzaia.streamflow.R
import com.lukakordzaia.streamflow.animations.TvSidebarAnimations
import com.lukakordzaia.streamflow.databinding.DialogSyncDatabaseBinding
import com.lukakordzaia.streamflow.databinding.TvSidebarBinding
import com.lukakordzaia.streamflow.interfaces.TvCheckFirstItem
import com.lukakordzaia.streamflow.ui.phone.profile.ProfileFragment
import com.lukakordzaia.streamflow.ui.phone.profile.ProfileViewModel
import com.lukakordzaia.streamflow.ui.tv.favorites.TvFavoritesActivity
import com.lukakordzaia.streamflow.ui.tv.genres.TvSingleGenreActivity
import com.lukakordzaia.streamflow.ui.tv.main.TvActivity
import com.lukakordzaia.streamflow.ui.tv.search.TvSearchActivity
import com.lukakordzaia.streamflow.ui.tv.settings.TvSettingsActivity
import com.lukakordzaia.streamflow.ui.tv.tvcatalogue.TvCatalogueActivity
import com.lukakordzaia.streamflow.utils.AppConstants
import com.lukakordzaia.streamflow.utils.createToast
import com.lukakordzaia.streamflow.utils.setVisibleOrGone
import kotlinx.android.synthetic.main.tv_sidebar.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

abstract class BaseFragmentActivity<VB : ViewBinding> : FragmentActivity(), TvCheckFirstItem {
    private val profileViewModel: ProfileViewModel by viewModel()
    private val sidebarAnimations: TvSidebarAnimations by inject()
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

    lateinit var binding: VB
    abstract fun getViewBinding(): VB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = getViewBinding()
        setContentView(binding.root)

        googleAccount = GoogleSignIn.getLastSignedInAccount(this)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

    }

    fun setSidebarClickListeners(view: TvSidebarBinding) {
        view.searchButton.setOnClickListener {
            startActivity(Intent(this, TvSearchActivity::class.java))
            sidebarAnimations.hideSideBar(view.tvSidebar)
        }
        view.homeButton.setOnClickListener {
            val intent = Intent(this, TvActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            startActivity(intent)
            sidebarAnimations.hideSideBar(view.tvSidebar)
        }
        view.favoritesButton.setOnClickListener {
            if (auth.currentUser != null) {
                startActivity(Intent(this, TvFavoritesActivity::class.java))
            } else {
                this.createToast("ფავორიტების სანახავად, გაიარეთ ავტორიზაცია")
            }
            sidebarAnimations.hideSideBar(view.tvSidebar)
        }
        view.moviesButton.setOnClickListener {
            val intent = Intent(this, TvCatalogueActivity::class.java).apply {
                putExtra("type", AppConstants.TV_CATEGORY_NEW_MOVIES)
            }
            startActivity(intent)
            sidebarAnimations.hideSideBar(view.tvSidebar)
        }
        view.genresButton.setOnClickListener {
            val intent = Intent(this, TvSingleGenreActivity::class.java)
            startActivity(intent)
            sidebarAnimations.hideSideBar(view.tvSidebar)
        }
        view.settingsButton.setOnClickListener {
            val intent = Intent(this, TvSettingsActivity::class.java)
            startActivity(intent)
            sidebarAnimations.hideSideBar(view.tvSidebar)
        }
    }

    fun setCurrentButton(currentButton: View) {
        this.currentButton = currentButton
    }

    fun googleViews(view: TvSidebarBinding) {
        signInButton = view.signIn
        signOutButton = view.signOut
        profilePhoto = view.profilePhoto
        profileUsername = view.profileUsername

        googleListeners()
    }

    private fun googleListeners() {
        updateGoogleUI(auth.currentUser != null)

        signInButton.setOnClickListener {
            val signInIntent: Intent = googleSignInClient!!.signInIntent
            startActivityForResult(signInIntent, ProfileFragment.RC_SIGN_IN)
        }

        signOutButton.setOnClickListener {
            auth.signOut()
            googleSignInClient!!.signOut()
            updateGoogleUI(false)

            val intent = Intent(this, TvActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            startActivity(intent)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == ProfileFragment.RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Log.w(ContentValues.TAG, "Google sign in failed", e)
                this.createToast("დაფიქსირა შეცდომა ავტორიზაციის დროს")
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
                        this.createToast("დაფიქსირა შეცდომა ავტორიზაციის დროს")
                        updateGoogleUI(false)
                    }
                }
    }

    private fun showSyncDialog() {
        profileViewModel.getContinueWatchingFromRoom().observe(this, {
            if (!it.isNullOrEmpty()) {
                val binding = DialogSyncDatabaseBinding.inflate(LayoutInflater.from(this))
                val syncDialog = Dialog(this)
                syncDialog.setContentView(binding.root)

                binding.confirmButton.setOnClickListener { _ ->
                    profileViewModel.addContinueWatchingToFirestore(it)
                    val intent = Intent(this, TvActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    this.startActivity(intent)

                }
                binding.cancelButton.setOnClickListener {
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

    private fun updateGoogleUI(isLoggedIn: Boolean) {
        profilePhoto.setVisibleOrGone(isLoggedIn)
        signInButton.setVisibleOrGone(!isLoggedIn)
        signOutButton.setVisibleOrGone(isLoggedIn)

        profileUsername.text = if (isLoggedIn && googleAccount != null) {
            "გამარჯობა, ${googleAccount!!.givenName!!.toUpperCase()}"
        } else {
            ""
        }

        if (isLoggedIn) {
            Glide.with(this).load(googleAccount?.photoUrl).into(profilePhoto)
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_DPAD_LEFT -> {
                if (isFirstItem) {
                    sidebarAnimations.showSideBar(tv_sidebar)
                    currentButton.requestFocus()
                }
            }
            KeyEvent.KEYCODE_DPAD_RIGHT -> {
                if (tv_sidebar.isVisible) {
                    sidebarAnimations.hideSideBar(tv_sidebar)
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
            sidebarAnimations.hideSideBar(tv_sidebar)
        } else {
            sidebarAnimations.showSideBar(tv_sidebar)
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