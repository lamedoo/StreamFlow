package com.lukakordzaia.streamflow.ui.tv.tvvideoplayer

import android.os.Bundle
import android.view.KeyEvent
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import com.lukakordzaia.streamflow.R
import com.lukakordzaia.streamflow.databinding.ActivityTvVideoPlayerBinding
import com.lukakordzaia.streamflow.datamodels.VideoPlayerData
import com.lukakordzaia.streamflow.network.LoadingState
import com.lukakordzaia.streamflow.sharedpreferences.AuthSharedPreferences
import com.lukakordzaia.streamflow.ui.shared.VideoPlayerViewModel
import kotlinx.android.synthetic.main.continue_watching_dialog.*
import kotlinx.android.synthetic.main.fragment_tv_video_player.*
import kotlinx.android.synthetic.main.tv_exoplayer_controller_layout.*
import kotlinx.android.synthetic.main.tv_exoplayer_controller_layout.view.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class TvVideoPlayerActivity : FragmentActivity() {
    private val videoPlayerViewModel: VideoPlayerViewModel by viewModel()
    private val authSharedPreferences: AuthSharedPreferences by inject()

    private lateinit var binding: ActivityTvVideoPlayerBinding
    private var currentFragment = VIDEO_PLAYER
    private var ffIncrement = 10000
    private var rewIncrement = 10000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTvVideoPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportFragmentManager.beginTransaction()
            .add(R.id.tv_video_player_nav_host, TvVideoPlayerFragment())
            .commit()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_DPAD_UP -> {
                if (currentFragment == VIDEO_PLAYER) {
                    if (continue_watching?.isVisible == true) {
                        confirm_button.requestFocus()
                    } else {
                        when {
                            !tv_title_player.isControllerVisible -> {
                                tv_title_player.showController()
                                tv_title_player.exo_pause.requestFocus()
                            }
                            tv_next_button.isVisible -> {
                                exo_pause.nextFocusUpId = R.id.tv_next_button
                                exo_play.nextFocusUpId = R.id.tv_next_button
                            }
                            tv_subtitle_toggle.isVisible -> {
                                exo_pause.nextFocusUpId = R.id.tv_subtitle_toggle
                                exo_play.nextFocusUpId = R.id.tv_subtitle_toggle
                            }
                            else -> {
                                exo_pause.nextFocusUpId = R.id.tv_exo_back
                                exo_play.nextFocusUpId = R.id.tv_exo_back
                            }
                        }
                    }
//                    return true
                } else {
                    return super.onKeyUp(keyCode, event)
                }
            }

            KeyEvent.KEYCODE_DPAD_CENTER -> {
                return if (currentFragment == VIDEO_PLAYER) {
                    if (!tv_title_player.isControllerVisible) {
                        tv_title_player.showController()
                        tv_title_player.player!!.pause()
                    } else if (!tv_title_player.player!!.isPlaying && !tv_title_player.isControllerVisible) {
                        tv_title_player.player!!.play()
                    }
                    true
                } else {
                    super.onKeyUp(keyCode, event)
                }
            }

            KeyEvent.KEYCODE_DPAD_DOWN -> {
                if (currentFragment == VIDEO_PLAYER) {
                    if (continue_watching_dialog_root?.isVisible == true) {
                        go_back_button.requestFocus()
                    } else {
                        when {
                            !tv_title_player.isControllerVisible -> {
                                tv_title_player.showController()
                                tv_title_player.exo_pause.requestFocus()
                                return true
                            }
                            exo_play.isFocused || exo_pause.isFocused -> {
                                setCurrentFragment(VIDEO_DETAILS)
                                detailsFragment()
                                supportFragmentManager.beginTransaction()
                                    .setCustomAnimations(R.anim.slide_from_down, R.anim.slide_out_top)
                                    .add(R.id.tv_video_player_nav_host, TvVideoPlayerDetailsFragment())
                                    .addToBackStack(null)
                                    .commit()
                                return true
                            }
                            else -> {
                                tv_subtitle_toggle.nextFocusDownId = if (tv_title_player.player!!.isPlaying) R.id.exo_pause else R.id.exo_play
                                tv_next_button.nextFocusDownId = if (tv_title_player.player!!.isPlaying) R.id.exo_pause else R.id.exo_play
                                tv_next_button?.nextFocusDownId = if (tv_title_player.player!!.isPlaying) R.id.exo_pause else R.id.exo_play
                            }
                        }
                    }
                } else {
                    return super.onKeyUp(keyCode, event)
                }
            }

            KeyEvent.KEYCODE_DPAD_LEFT -> {
                if (currentFragment == VIDEO_PLAYER) {
                    if (continue_watching?.isVisible == false) {
                        when {
                            !tv_title_player.isControllerVisible -> {
                                tv_title_player.showController()
                                tv_title_player.exo_rew.callOnClick()
                            }
                            tv_next_button.isFocused -> if (tv_subtitle_toggle.isVisible) tv_subtitle_toggle.requestFocus() else tv_exo_back.requestFocus()
                            tv_subtitle_toggle.isFocused -> tv_exo_back.requestFocus()
                            else -> tv_title_player.exo_rew.callOnClick()
                        }
                    }
                    return true
                } else {
                    return super.onKeyUp(keyCode, event)
                }
            }

            KeyEvent.KEYCODE_DPAD_RIGHT -> {
                if (currentFragment == VIDEO_PLAYER) {
                    if (continue_watching?.isVisible == false) {
                        when {
                            !tv_title_player.isControllerVisible -> {
                                tv_title_player.showController()
                                tv_title_player.exo_ffwd.callOnClick()
                            }
                            tv_exo_back.isFocused ->
                                if (tv_subtitle_toggle.isVisible) tv_subtitle_toggle.requestFocus() else {
                                    if (tv_next_button.isVisible) tv_next_button.requestFocus() else tv_title_player.exo_ffwd.callOnClick()
                                }
                            tv_subtitle_toggle.isFocused -> if (tv_next_button.isVisible) tv_next_button.requestFocus() else tv_title_player.exo_ffwd.callOnClick()
                            else -> tv_title_player.exo_ffwd.callOnClick()
                        }
                    }
                    return true
                } else {
                    return super.onKeyUp(keyCode, event)
                }
            }

            KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE -> {
                tv_title_player.dispatchMediaKeyEvent(event!!)
                return true
            }

            KeyEvent.KEYCODE_MEDIA_PLAY -> {
                tv_title_player.dispatchMediaKeyEvent(event!!)
                return true
            }

            KeyEvent.KEYCODE_MEDIA_FAST_FORWARD -> {
                if (!continue_watching.isVisible) {
                    tv_title_player.dispatchMediaKeyEvent(event!!)
                }
                return true
            }

            KeyEvent.KEYCODE_MEDIA_NEXT -> {
                if (!tv_title_player.isControllerVisible) {
                    tv_title_player.showController()
                }
                tv_title_player.exo_ffwd.callOnClick()
                return true
            }

            KeyEvent.KEYCODE_MEDIA_REWIND -> {
                if (!continue_watching.isVisible) {
                    tv_title_player.dispatchMediaKeyEvent(event!!)
                }
                return true
            }

            KeyEvent.KEYCODE_MEDIA_PREVIOUS -> {
                if (!tv_title_player.isControllerVisible) {
                    tv_title_player.showController()
                }
                tv_title_player.exo_rew.callOnClick()
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_DPAD_LEFT -> {
                rewIncrement = 10000
                return true
            }
            KeyEvent.KEYCODE_DPAD_RIGHT -> {
                ffIncrement = 10000
                return true
            }
        }
        return super.onKeyUp(keyCode, event)
    }

    override fun onBackPressed() {
        if (currentFragment == VIDEO_DETAILS) {
            supportFragmentManager.popBackStack()
            setCurrentFragment(VIDEO_PLAYER)
        } else {
            if (tv_title_player.isControllerVisible) {
                tv_title_player.hideController()
            } else {
                val videoPlayerData = this.intent.getParcelableExtra<VideoPlayerData>("videoPlayerData") as VideoPlayerData

                val parentFragment = supportFragmentManager.findFragmentById(R.id.tv_video_player_nav_host) as TvVideoPlayerFragment
                parentFragment.releasePlayer()

                if (videoPlayerData.trailerUrl == null) {
                    if (authSharedPreferences.getLoginToken() != "") {
                        videoPlayerViewModel.saveLoader.observe(this, {
                            when (it.status) {
                                LoadingState.Status.RUNNING -> {
                                }
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
        }
    }

    fun setCurrentFragment(fragment: Int) {
        currentFragment = fragment

        if (fragment == VIDEO_PLAYER) {
            tv_title_player.showController()
            exo_pause.requestFocus()
        }
    }

    private fun detailsFragment() {
        tv_title_player.player!!.pause()
        tv_title_player.hideController()
    }

    companion object {
        const val VIDEO_PLAYER = 0
        const val VIDEO_DETAILS = 1
    }
}