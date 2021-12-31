package com.lukakordzaia.streamflowtv.ui.tvvideoplayer

import android.os.Bundle
import android.view.KeyEvent
import com.lukakordzaia.core.utils.AppConstants
import com.lukakordzaia.core.datamodels.VideoPlayerData
import com.lukakordzaia.core.network.LoadingState
import com.lukakordzaia.core.videoplayer.VideoPlayerViewModel
import com.lukakordzaia.streamflowtv.R
import com.lukakordzaia.streamflowtv.baseclasses.activities.BaseFragmentActivity
import com.lukakordzaia.streamflowtv.databinding.ActivityTvVideoPlayerBinding
import com.lukakordzaia.streamflowtv.ui.tvsingletitle.tvepisodes.TvEpisodesFragment
import com.lukakordzaia.streamflowtv.ui.tvsingletitle.tvepisodes.TvEpisodesViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class TvVideoPlayerActivity : BaseFragmentActivity<ActivityTvVideoPlayerBinding>() {
    private val videoPlayerViewModel: VideoPlayerViewModel by viewModel()

    private lateinit var videoPlayerFragment: TvVideoPlayerFragment

    override fun getViewBinding() = ActivityTvVideoPlayerBinding.inflate(layoutInflater)

    private var currentFragmentState = VIDEO_PLAYER

    private lateinit var videoPlayerData: VideoPlayerData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        videoPlayerData = intent.getParcelableExtra<VideoPlayerData>(AppConstants.VIDEO_PLAYER_DATA) as VideoPlayerData
        supportFragmentManager.beginTransaction()
            .replace(R.id.tv_video_player_nav_host, TvVideoPlayerFragment())
            .commit()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_DPAD_UP -> {
                if (currentFragmentState == VIDEO_PLAYER) {
                    videoPlayerFragment.onKeyUp()
                } else {
                    return super.onKeyUp(keyCode, event)
                }
            }

            KeyEvent.KEYCODE_DPAD_CENTER -> {
                return if (currentFragmentState == VIDEO_PLAYER) {
                    videoPlayerFragment.onKeyCenter()
                    true
                } else {
                    super.onKeyUp(keyCode, event)
                }
            }

            KeyEvent.KEYCODE_DPAD_DOWN -> {
                if (currentFragmentState == VIDEO_PLAYER) {
                    videoPlayerFragment.onKeyDown()
                } else {
                    return super.onKeyUp(keyCode, event)
                }
            }

            KeyEvent.KEYCODE_DPAD_LEFT -> {
                return if (currentFragmentState == VIDEO_PLAYER) {
                    videoPlayerFragment.onKeyLeft()
                    true
                } else {
                    super.onKeyUp(keyCode, event)
                }
            }

            KeyEvent.KEYCODE_DPAD_RIGHT -> {
                return if (currentFragmentState == VIDEO_PLAYER) {
                    videoPlayerFragment.onKeyRight()
                    true
                } else {
                    super.onKeyUp(keyCode, event)
                }
            }

            KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE -> {
                dispatchEvent(event)
                return true
            }

            KeyEvent.KEYCODE_MEDIA_PLAY -> {
                dispatchEvent(event)
                return true
            }

            KeyEvent.KEYCODE_MEDIA_FAST_FORWARD -> {
                if (currentFragmentState == VIDEO_PLAYER) {
                    if (!videoPlayerFragment.checkContinueWatchingVisibility()) {
                        dispatchEvent(event)
                    }
                }
                return true
            }

            KeyEvent.KEYCODE_MEDIA_REWIND -> {
                if (currentFragmentState == VIDEO_PLAYER) {
                    if (!videoPlayerFragment.checkContinueWatchingVisibility()) {
                        dispatchEvent(event)
                    }
                }
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onBackPressed() {
        when (currentFragmentState) {
            VIDEO_DETAILS -> {
                super.onBackPressed()
                setCurrentFragmentState(VIDEO_PLAYER)
            }
            VIDEO_PLAYER -> {
                if (videoPlayerFragment.getPlayer().isControllerVisible) {
                    videoPlayerFragment.getPlayer().hideController()
                } else {
                    goBack()
                }
            }
            AUDIO_SIDEBAR -> {
                videoPlayerFragment.hideAudioSidebar()
            }
            BACK_BUTTON -> goBack()
            NEW_EPISODE -> {}
        }
    }

    fun showDetails() {
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.slide_from_down, R.anim.slide_out_top)
            .add(R.id.tv_video_player_nav_host, TvEpisodesFragment())
            .addToBackStack(null)
            .commit()
    }

    fun setCurrentFragmentState(state: Int) {
        currentFragmentState = state

        if (state == VIDEO_PLAYER) {
            videoPlayerFragment.getPlayer().showController()
            videoPlayerFragment.focusOnPause()
        }
    }

    private fun goBack() {
        videoPlayerFragment.releasePlayer()

        if (videoPlayerData.trailerUrl != null || sharedPreferences.getLoginToken().isNullOrEmpty()) {
            super.onBackPressed()
        } else {
            videoPlayerViewModel.saveLoader.observe(this, {
                when (it) {
                    LoadingState.LOADING -> {
                    }
                    LoadingState.LOADED, LoadingState.ERROR -> {
                        super.onBackPressed()
                    }
                }
            })
        }
    }

    private fun dispatchEvent(event: KeyEvent?) {
        if (currentFragmentState == VIDEO_PLAYER) {
            videoPlayerFragment.getPlayer().dispatchMediaKeyEvent(event!!)
        }
    }

    fun setVideoPlayerFragment() {
        videoPlayerFragment = supportFragmentManager.findFragmentById(R.id.tv_video_player_nav_host) as TvVideoPlayerFragment
    }

    companion object {
        const val VIDEO_PLAYER = 0
        const val VIDEO_DETAILS = 1
        const val NEW_EPISODE = 2
        const val BACK_BUTTON = 3
        const val AUDIO_SIDEBAR = 4
    }
}