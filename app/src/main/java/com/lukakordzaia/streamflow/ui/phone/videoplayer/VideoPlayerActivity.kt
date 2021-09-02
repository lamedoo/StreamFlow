package com.lukakordzaia.streamflow.ui.phone.videoplayer

import android.content.Context
import android.content.Intent
import android.os.Bundle
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
    }

    override fun onBackPressed() {
        val videoPlayerData = this.intent.getParcelableExtra<VideoPlayerData>(AppConstants.VIDEO_PLAYER_DATA) as VideoPlayerData

        val parentFragment = supportFragmentManager.findFragmentById(R.id.tv_video_player_fragment) as VideoPlayerFragment
        parentFragment.releasePlayer()

        if (videoPlayerData.trailerUrl == null) {
            if (sharedPreferences.getLoginToken() != "") {
                videoPlayerViewModel.saveLoader.observe(this, {
                    when (it.status) {
                        LoadingState.Status.RUNNING -> {}
                        LoadingState.Status.SUCCESS -> {
                            super.onBackPressed()
                        }
                    }
                })
            } else {
                super.onBackPressed()
            }
        } else {
            super.onBackPressed()
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