package com.lukakordzaia.streamflow.ui.phone.videoplayer

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.lukakordzaia.streamflow.R
import com.lukakordzaia.streamflow.datamodels.VideoPlayerData
import com.lukakordzaia.streamflow.network.LoadingState
import com.lukakordzaia.streamflow.sharedpreferences.SharedPreferences
import com.lukakordzaia.streamflow.ui.shared.VideoPlayerViewModel
import com.lukakordzaia.streamflow.utils.AppConstants
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class VideoPlayerActivity : AppCompatActivity() {
    private val videoPlayerViewModel: VideoPlayerViewModel by viewModel()
    private val sharedPreferences: SharedPreferences by inject()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_phone_video_player)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
    }

    override fun onBackPressed() {
        val videoPlayerData = this.intent.getParcelableExtra<VideoPlayerData>(AppConstants.VIDEO_PLAYER_DATA) as VideoPlayerData

        val parentFragment = supportFragmentManager.findFragmentById(R.id.tv_video_player_fragment) as VideoPlayerFragment
        parentFragment.releasePlayer()

        if (videoPlayerData.trailerUrl != null || sharedPreferences.getLoginToken().isNullOrEmpty()) {
            super.onBackPressed()
        } else {
            videoPlayerViewModel.saveLoader.observe(this, {
                when (it) {
                    LoadingState.LOADING -> {}
                    LoadingState.LOADED, LoadingState.ERROR -> {
                        super.onBackPressed()
                    }
                }
            })
        }
    }

    companion object {
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