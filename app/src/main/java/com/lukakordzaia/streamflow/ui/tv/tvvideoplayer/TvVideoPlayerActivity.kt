package com.lukakordzaia.streamflow.ui.tv.tvvideoplayer

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import com.lukakordzaia.streamflow.R
import com.lukakordzaia.streamflow.databinding.ActivityTvVideoPlayerBinding
import com.lukakordzaia.streamflow.datamodels.VideoPlayerData
import com.lukakordzaia.streamflow.ui.tv.details.TvDetailsActivity
import kotlinx.android.synthetic.main.continue_watching_dialog.*
import kotlinx.android.synthetic.main.fragment_tv_video_player.*
import kotlinx.android.synthetic.main.tv_exoplayer_controller_layout.*
import kotlinx.android.synthetic.main.tv_exoplayer_controller_layout.view.*

class TvVideoPlayerActivity : FragmentActivity() {
    private lateinit var binding: ActivityTvVideoPlayerBinding
    private var ffIncrement = 10000
    private var rewIncrement = 10000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTvVideoPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_DPAD_UP -> {
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
            }

            KeyEvent.KEYCODE_DPAD_CENTER -> {
                if (!tv_title_player.isControllerVisible) {
                    tv_title_player.showController()
                    tv_title_player.player!!.pause()
                } else if (!tv_title_player.player!!.isPlaying && !tv_title_player.isControllerVisible) {
                    tv_title_player.player!!.play()
                }
                return true
            }

            KeyEvent.KEYCODE_DPAD_DOWN -> {
                if (continue_watching_dialog_root?.isVisible == true) {
                    go_back_button.requestFocus()
                } else {
                    when {
                        !tv_title_player.isControllerVisible -> {
                            tv_title_player.showController()
                            tv_title_player.exo_pause.requestFocus()
                        }
                        else -> {
                            tv_subtitle_toggle.nextFocusDownId = if (tv_title_player.player!!.isPlaying) R.id.exo_pause else R.id.exo_play
                            tv_next_button.nextFocusDownId = if (tv_title_player.player!!.isPlaying) R.id.exo_pause else R.id.exo_play
                            tv_next_button?.nextFocusDownId = if (tv_title_player.player!!.isPlaying) R.id.exo_pause else R.id.exo_play
                        }
                    }
                }
            }

            KeyEvent.KEYCODE_DPAD_LEFT -> {
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
            }

            KeyEvent.KEYCODE_DPAD_RIGHT -> {
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
        val videoPlayerData = this.intent!!.getParcelableExtra("videoPlayerData") as VideoPlayerData
        if (tv_title_player.isControllerVisible) {
            tv_title_player.hideController()
        } else {
            val intent = Intent(this, TvDetailsActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra("titleId", videoPlayerData.titleId)
            intent.putExtra("isTvShow", videoPlayerData.isTvShow)
            startActivity(intent)
            finish()
        }
    }
}