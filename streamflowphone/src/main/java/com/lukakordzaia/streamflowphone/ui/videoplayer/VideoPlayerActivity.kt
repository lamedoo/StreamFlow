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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
    }

    override fun onBackPressed() {
        val parentFragment = supportFragmentManager.findFragmentById(R.id.tv_video_player_fragment) as VideoPlayerFragment

        when (currentFragment) {
            VIDEO_PLAYER -> {
                val videoPlayerData = this.intent.getParcelableExtra<VideoPlayerData>(AppConstants.VIDEO_PLAYER_DATA) as VideoPlayerData

                parentFragment.releasePlayer()

                if (videoPlayerData.trailerUrl != null || sharedPreferences.getLoginToken().isNullOrEmpty()) {
                    finish()
                } else {
                    videoPlayerViewModel.saveLoader.observe(this) {
                        when (it) {
                            LoadingState.LOADING -> {}
                            LoadingState.LOADED, LoadingState.ERROR -> {
                                finish()
                            }
                        }
                    }
                }
            }
            AUDIO_SIDEBAR -> parentFragment.hideAudioSidebar()
            VIDEO_PLAYER_PAUSE -> {}
        }
    }

    fun setCurrentFragment(fragment: Int) {
        currentFragment = fragment
    }

    companion object {
        const val VIDEO_PLAYER = 0
        const val AUDIO_SIDEBAR = 1
        const val VIDEO_PLAYER_PAUSE = 2

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