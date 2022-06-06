package com.lukakordzaia.streamflowphone.ui.videoplayer

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.lukakordzaia.core.domain.domainmodels.VideoPlayerData
import com.lukakordzaia.core.network.LoadingState
import com.lukakordzaia.core.utils.AppConstants
import com.lukakordzaia.core.videoplayer.VideoPlayerViewModel
import com.lukakordzaia.streamflowphone.R
import com.lukakordzaia.streamflowphone.databinding.ActivityPhoneVideoPlayerBinding
import com.lukakordzaia.streamflowphone.ui.baseclasses.BaseActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class VideoPlayerActivity : BaseActivity<ActivityPhoneVideoPlayerBinding>() {
    override fun getViewBinding() = ActivityPhoneVideoPlayerBinding.inflate(layoutInflater)
    private val videoPlayerViewModel: VideoPlayerViewModel by viewModel()

    private var currentFragment = VIDEO_PLAYER
    private lateinit var parentFragment: VideoPlayerFragment

    private lateinit var videoPlayerData: VideoPlayerData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initViews()
        initObservers()
    }

    private fun initViews() {
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)

        videoPlayerData = this.intent.getParcelableExtra<VideoPlayerData>(AppConstants.VIDEO_PLAYER_DATA) as VideoPlayerData
    }

    private fun initObservers() {
        videoPlayerViewModel.saveLoader.observe(this) {
            when (it) {
                LoadingState.LOADING -> {}
                LoadingState.LOADED, LoadingState.ERROR -> {
                    finish()
                }
            }
        }
    }

    override fun onBackPressed() {
        when (currentFragment) {
            VIDEO_PLAYER -> goBack()
            AUDIO_SIDEBAR -> parentFragment.hideAudioSidebar()
            BACK_BUTTON -> goBack()
        }
    }

    fun setCurrentFragment(fragment: Int) {
        currentFragment = fragment
    }

    fun setParentFragment() {
        parentFragment = supportFragmentManager.findFragmentById(R.id.tv_video_player_fragment) as VideoPlayerFragment
    }

    private fun goBack() {
        if (videoPlayerData.trailerUrl != null || sharedPreferences.getLoginToken().isNullOrEmpty()) {
            finish()
        } else {
            parentFragment.saveCurrentProgress(true)
        }
    }

    companion object {
        const val VIDEO_PLAYER = 0
        const val AUDIO_SIDEBAR = 1
        const val BACK_BUTTON = 2

        fun startFromHomeScreen(context: Context, videoPlayerData: VideoPlayerData): Intent {
            return Intent(context, VideoPlayerActivity::class.java).apply {
                putExtra(AppConstants.VIDEO_PLAYER_DATA, videoPlayerData)
            }
        }

        fun startFromTrailers(context: Context, videoPlayerData: VideoPlayerData): Intent {
            return Intent(context, VideoPlayerActivity::class.java).apply {
                putExtra(AppConstants.VIDEO_PLAYER_DATA, videoPlayerData)
            }
        }

        fun startFromSingleTitle(context: Context, videoPlayerData: VideoPlayerData): Intent {
            return Intent(context, VideoPlayerActivity::class.java).apply {
                putExtra(AppConstants.VIDEO_PLAYER_DATA, videoPlayerData)
            }
        }
    }
}