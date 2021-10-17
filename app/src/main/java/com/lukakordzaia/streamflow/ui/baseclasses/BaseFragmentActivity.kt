package com.lukakordzaia.streamflow.ui.baseclasses

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import com.lukakordzaia.streamflow.animations.TvSidebarAnimations
import com.lukakordzaia.streamflow.databinding.DialogSyncDatabaseBinding
import com.lukakordzaia.streamflow.databinding.TvSidebarBinding
import com.lukakordzaia.streamflow.interfaces.TvCheckFirstItem
import com.lukakordzaia.streamflow.network.LoadingState
import com.lukakordzaia.streamflow.sharedpreferences.SharedPreferences
import com.lukakordzaia.streamflow.ui.phone.profile.ProfileViewModel
import com.lukakordzaia.streamflow.ui.tv.genres.TvSingleGenreActivity
import com.lukakordzaia.streamflow.ui.tv.login.TvLoginActivity
import com.lukakordzaia.streamflow.ui.tv.main.TvActivity
import com.lukakordzaia.streamflow.ui.tv.search.TvSearchActivity
import com.lukakordzaia.streamflow.ui.tv.settings.TvSettingsActivity
import com.lukakordzaia.streamflow.ui.tv.tvcatalogue.TvCatalogueActivity
import com.lukakordzaia.streamflow.ui.tv.tvwatchlist.TvWatchlistActivity
import com.lukakordzaia.streamflow.utils.AppConstants
import com.lukakordzaia.streamflow.utils.createToast
import com.lukakordzaia.streamflow.utils.setVisibleOrGone
import kotlinx.android.synthetic.main.tv_sidebar.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

abstract class BaseFragmentActivity<VB : ViewBinding> : FragmentActivity(), TvCheckFirstItem {
    private val profileViewModel: ProfileViewModel by viewModel()
    protected val sidebarAnimations: TvSidebarAnimations by inject()
    protected val sharedPreferences: SharedPreferences by inject()

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

    override fun onStart() {
        super.onStart()
        profileViewModel.getUserData()
        updateProfileUI(sharedPreferences.getLoginToken() != "")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = getViewBinding()
        setContentView(binding.root)
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
            if (sharedPreferences.getLoginToken() != "") {
                startActivity(Intent(this, TvWatchlistActivity::class.java))
            } else {
                this.createToast("ფავორიტების სანახავად, გაიარეთ ავტორიზაცია")
            }
            sidebarAnimations.hideSideBar(view.tvSidebar)
        }
        view.moviesButton.setOnClickListener {
            val intent = Intent(this, TvCatalogueActivity::class.java).apply {
                putExtra(AppConstants.CATALOGUE_TYPE, AppConstants.LIST_NEW_MOVIES)
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
        view.signIn.setOnClickListener {
            val intent = Intent(this, TvLoginActivity::class.java)
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

        fragmentListeners()
    }

    private fun fragmentListeners() {
        signOutButton.setOnClickListener {
            profileViewModel.userLogout()
        }

        profileViewModel.loginLoader.observe(this, {
            when (it.status) {
                LoadingState.Status.RUNNING -> {}
                LoadingState.Status.SUCCESS -> {
                    val intent = Intent(this, TvActivity::class.java).apply {
                        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    startActivity(intent)
                }
            }
        })
    }

    private fun showSyncDialog() {
        profileViewModel.getContinueWatchingFromRoom().observe(this, {
            if (!it.isNullOrEmpty()) {
                val binding = DialogSyncDatabaseBinding.inflate(LayoutInflater.from(this))
                val syncDialog = Dialog(this)
                syncDialog.setContentView(binding.root)

                binding.confirmButton.setOnClickListener { _ ->
                    profileViewModel.addContinueWatchingToApi(it)
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

    private fun updateProfileUI(isLoggedIn: Boolean) {
        profilePhoto.setVisibleOrGone(isLoggedIn)
        signInButton.setVisibleOrGone(!isLoggedIn)
        signOutButton.setVisibleOrGone(isLoggedIn)

        if (isLoggedIn) {
            profileViewModel.userData.observe(this, {
                profileUsername.text = "გამარჯობა, ${it.displayName.toUpperCase()}"
                Glide.with(this).load(it.avatar.large).into(profilePhoto)
            })
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