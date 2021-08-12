package com.lukakordzaia.streamflow.ui.phone.videoplayer

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.lukakordzaia.streamflow.R
import com.lukakordzaia.streamflow.datamodels.VideoPlayerData
import com.lukakordzaia.streamflow.utils.AppConstants
import kotlinx.android.synthetic.main.activity_phone_video_player.*

class VideoPlayerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_phone_video_player)

        supportFragmentManager.beginTransaction()
            .add(R.id.tv_video_player_fragment, VideoPlayerFragment())
            .commit()
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