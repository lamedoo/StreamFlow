package com.lukakordzaia.streamflowtv.baseclasses.activities

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import androidx.core.view.isVisible
import androidx.leanback.app.RowsSupportFragment
import androidx.leanback.widget.ListRowPresenter
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import com.lukakordzaia.core.AppConstants
import com.lukakordzaia.core.databinding.DialogSyncDatabaseBinding
import com.lukakordzaia.core.network.LoadingState
import com.lukakordzaia.core.utils.createToast
import com.lukakordzaia.core.utils.setGone
import com.lukakordzaia.core.utils.setVisibleOrGone
import com.lukakordzaia.streamflowtv.R
import com.lukakordzaia.streamflowtv.animations.TvSidebarAnimations
import com.lukakordzaia.streamflowtv.databinding.TvSidebarBinding
import com.lukakordzaia.streamflowtv.interfaces.TvCheckFirstItem
import com.lukakordzaia.streamflowtv.ui.genres.TvGenresActivity
import com.lukakordzaia.streamflowtv.ui.login.TvLoginActivity
import com.lukakordzaia.streamflowtv.ui.login.TvProfileViewModel
import com.lukakordzaia.streamflowtv.ui.main.TvActivity
import com.lukakordzaia.streamflowtv.ui.search.TvSearchActivity
import com.lukakordzaia.streamflowtv.ui.settings.TvSettingsActivity
import com.lukakordzaia.streamflowtv.ui.tvcatalogue.TvCatalogueActivity
import com.lukakordzaia.streamflowtv.ui.tvwatchlist.TvWatchlistActivity
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

abstract class BaseSidebarFragmentActivity<VB : ViewBinding> : BaseFragmentActivity<VB>(), TvCheckFirstItem {
    private val profileViewModel: TvProfileViewModel by viewModel()
    val sidebarAnimations: TvSidebarAnimations by inject()

    private lateinit var sidebar: TvSidebarBinding
    private lateinit var currentButton: View

    private var doubleBackToExitPressedOnce = false
    private var isFirstItem = false
    private var rowsSupportFragment: RowsSupportFragment? = null
    private var rowsPosition: Int? = null

    override fun onStart() {
        super.onStart()
        profileViewModel.getUserData()
        updateProfileUI(sharedPreferences.getLoginToken() != "")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initObservers()
    }

    private fun initObservers() {
        profileViewModel.generalLoader.observe(this, {
            when (it) {
                LoadingState.LOADING -> {}
                LoadingState.LOADED -> {
                    val intent = Intent(this, TvActivity::class.java).apply {
                        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    startActivity(intent)
                }
            }
        })
    }

    fun setSidebar(tvSidebar: TvSidebarBinding) {
        sidebar = tvSidebar
        sidebarClickListeners(sidebar)
    }

    private fun sidebarClickListeners(view: TvSidebarBinding) {
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
            val intent = Intent(this, TvGenresActivity::class.java)
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
        view.signOut.setOnClickListener {
            profileViewModel.userLogout()
        }
    }

    fun setCurrentButton(currentButton: View) {
        this.currentButton = currentButton

        currentButton.setOnClickListener {
            sidebar.root.setGone()
        }
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
        sidebar.profilePhoto.setVisibleOrGone(isLoggedIn)
        sidebar.signIn.setVisibleOrGone(!isLoggedIn)
        sidebar.signOut.setVisibleOrGone(isLoggedIn)

        if (isLoggedIn) {
            profileViewModel.userData.observe(this, {
                sidebar.profileUsername.text = getString(R.string.hello_user, it.displayName).uppercase()
                Glide.with(this).load(it.avatar.large).into(sidebar.profilePhoto)
            })
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_DPAD_LEFT -> {
                if (isFirstItem) {
                    sidebarAnimations.showSideBar(sidebar.root)
                    currentButton.requestFocus()
                }
            }
            KeyEvent.KEYCODE_DPAD_RIGHT -> {
                if (sidebar.root.isVisible) {
                    sidebarAnimations.hideSideBar(sidebar.root)
                    if (rowsSupportFragment != null && rowsPosition != null) {
                        rowsSupportFragment!!.setSelectedPosition(rowsPosition!!, true, ListRowPresenter.SelectItemViewHolderTask(0))
                    }
                }
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onBackPressed() {
        if (sidebar.root.isVisible) {
            sidebarAnimations.hideSideBar(sidebar.root)
        } else {
            sidebarAnimations.showSideBar(sidebar.root)
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