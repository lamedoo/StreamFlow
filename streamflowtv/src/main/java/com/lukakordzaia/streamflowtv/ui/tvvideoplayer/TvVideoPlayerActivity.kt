package com.lukakordzaia.streamflowtv.ui.tvvideoplayer

import android.os.Bundle
import android.view.KeyEvent
import androidx.core.view.isVisible
import com.lukakordzaia.core.AppConstants
import com.lukakordzaia.core.datamodels.VideoPlayerData
import com.lukakordzaia.core.network.LoadingState
import com.lukakordzaia.core.viewModels.VideoPlayerViewModel
import com.lukakordzaia.streamflowtv.R
import com.lukakordzaia.streamflowtv.baseclasses.activities.BaseFragmentActivity
import com.lukakordzaia.streamflowtv.databinding.ActivityTvVideoPlayerBinding
import kotlinx.android.synthetic.main.fragment_tv_video_player.*
import kotlinx.android.synthetic.main.tv_exoplayer_controller_layout.*
import kotlinx.android.synthetic.main.tv_exoplayer_controller_layout.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class TvVideoPlayerActivity : BaseFragmentActivity<ActivityTvVideoPlayerBinding>() {
    private val videoPlayerViewModel: VideoPlayerViewModel by viewModel()

    override fun getViewBinding() = ActivityTvVideoPlayerBinding.inflate(layoutInflater)

    private var currentFragmentState = VIDEO_PLAYER

    private lateinit var videoPlayerData: VideoPlayerData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        videoPlayerData = this.intent.getParcelableExtra<VideoPlayerData>(AppConstants.VIDEO_PLAYER_DATA) as VideoPlayerData

        supportFragmentManager.beginTransaction()
            .replace(R.id.tv_video_player_nav_host, TvVideoPlayerFragment())
            .commit()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_DPAD_UP -> {
                if (currentFragmentState == VIDEO_PLAYER) {
                    val parentFragment = supportFragmentManager.findFragmentById(R.id.tv_video_player_nav_host) as TvVideoPlayerFragment
                    parentFragment.onKeyUp()
                } else {
                    return super.onKeyUp(keyCode, event)
                }
            }

            KeyEvent.KEYCODE_DPAD_CENTER -> {
                return if (currentFragmentState == VIDEO_PLAYER) {
                    val parentFragment = supportFragmentManager.findFragmentById(R.id.tv_video_player_nav_host) as TvVideoPlayerFragment
                    parentFragment.onKeyCenter()
                    true
                } else {
                    super.onKeyUp(keyCode, event)
                }
            }

            KeyEvent.KEYCODE_DPAD_DOWN -> {
                if (currentFragmentState == VIDEO_PLAYER) {
//                    if (continue_watching_dialog_root?.isVisible == true) {
//                        go_back_button.requestFocus()
//                    } else {
                        when {
                            !title_player.isControllerVisible -> {
                                title_player.showController()
                                title_player.exo_pause.requestFocus()
                                return true
                            }
                            exo_play.isFocused || exo_pause.isFocused -> {
                                setCurrentFragmentState(VIDEO_DETAILS)
                                detailsFragment()

                                val parentFragment = supportFragmentManager.findFragmentById(R.id.tv_video_player_nav_host) as TvVideoPlayerFragment
                                parentFragment.saveCurrentProgress()

                                if (videoPlayerData.trailerUrl == null) {
                                    showDetails()
                                }
                                return true
                            }
                            else -> {
                                subtitle_toggle.nextFocusDownId = if (title_player.player!!.isPlaying) R.id.exo_pause else R.id.exo_play
                                next_episode.nextFocusDownId = if (title_player.player!!.isPlaying) R.id.exo_pause else R.id.exo_play
                                next_episode.nextFocusDownId = if (title_player.player!!.isPlaying) R.id.exo_pause else R.id.exo_play
                            }
                        }
//                    }
                } else {
                    return super.onKeyUp(keyCode, event)
                }
            }

            KeyEvent.KEYCODE_DPAD_LEFT -> {
                return if (currentFragmentState == VIDEO_PLAYER) {
                    val parentFragment = supportFragmentManager.findFragmentById(R.id.tv_video_player_nav_host) as TvVideoPlayerFragment
                    parentFragment.onKeyLeft()
                    true
                } else {
                    super.onKeyUp(keyCode, event)
                }
            }

            KeyEvent.KEYCODE_DPAD_RIGHT -> {
                return if (currentFragmentState == VIDEO_PLAYER) {
                    val parentFragment = supportFragmentManager.findFragmentById(R.id.tv_video_player_nav_host) as TvVideoPlayerFragment
                    parentFragment.onKeyRight()
                    true
                } else {
                    super.onKeyUp(keyCode, event)
                }
            }

            KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE -> {
                title_player.dispatchMediaKeyEvent(event!!)
                return true
            }

            KeyEvent.KEYCODE_MEDIA_PLAY -> {
                title_player.dispatchMediaKeyEvent(event!!)
                return true
            }

            KeyEvent.KEYCODE_MEDIA_FAST_FORWARD -> {
                if (!continue_watching.isVisible) {
                    title_player.dispatchMediaKeyEvent(event!!)
                }
                return true
            }

            KeyEvent.KEYCODE_MEDIA_NEXT -> {
                if (!title_player.isControllerVisible) {
                    title_player.showController()
                }
                title_player.exo_ffwd.callOnClick()
                return true
            }

            KeyEvent.KEYCODE_MEDIA_REWIND -> {
                if (!continue_watching.isVisible) {
                    title_player.dispatchMediaKeyEvent(event!!)
                }
                return true
            }

            KeyEvent.KEYCODE_MEDIA_PREVIOUS -> {
                if (!title_player.isControllerVisible) {
                    title_player.showController()
                }
                title_player.exo_rew.callOnClick()
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onBackPressed() {
        when (currentFragmentState) {
            VIDEO_DETAILS -> {
                supportFragmentManager.popBackStack()
                setCurrentFragmentState(VIDEO_PLAYER)
            }
            VIDEO_PLAYER -> {
                if (title_player.isControllerVisible) {
                    title_player.hideController()
                } else {
                    goBack()
                }
            }
            AUDIO_SIDEBAR -> {
                val parentFragment = supportFragmentManager.findFragmentById(R.id.tv_video_player_nav_host) as TvVideoPlayerFragment
                parentFragment.hideAudioSidebar()
            }
            BACK_BUTTON -> goBack()
            NEW_EPISODE -> {}
        }
    }

    private fun showDetails() {
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.slide_from_down, R.anim.slide_out_top)
            .add(R.id.tv_video_player_nav_host, TvVideoPlayerDetailsFragment())
            .addToBackStack(null)
            .commit()
    }

    fun setCurrentFragmentState(state: Int) {
        currentFragmentState = state

        if (state == VIDEO_PLAYER) {
            title_player.showController()
            exo_pause.requestFocus()
        }
    }

    private fun detailsFragment() {
        title_player.player!!.pause()
        title_player.hideController()
    }

    private fun goBack() {
        val parentFragment = supportFragmentManager.findFragmentById(R.id.tv_video_player_nav_host) as TvVideoPlayerFragment
        parentFragment.releasePlayer()

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

    companion object {
        const val VIDEO_PLAYER = 0
        const val VIDEO_DETAILS = 1
        const val NEW_EPISODE = 2
        const val BACK_BUTTON = 3
        const val AUDIO_SIDEBAR = 4
    }
}