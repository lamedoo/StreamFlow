package com.lukakordzaia.streamflow.ui.tv.tvvideoplayer

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import com.lukakordzaia.streamflow.R
import com.lukakordzaia.streamflow.databinding.ActivityTvVideoPlayerBinding
import com.lukakordzaia.streamflow.ui.tv.details.TvDetailsActivity
import kotlinx.android.synthetic.main.continue_watching_dialog.*
import kotlinx.android.synthetic.main.tv_exoplayer_controller_layout.*
import kotlinx.android.synthetic.main.tv_exoplayer_controller_layout.view.*
import kotlinx.android.synthetic.main.tv_video_player_fragment.*

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
                when {
                    tv_next_button.isVisible -> {
                        exo_pause.nextFocusUpId = R.id.tv_next_button
                        exo_play.nextFocusUpId = R.id.tv_next_button
                    }
                    tv_title_player.player?.mediaItemCount != 1 -> {
                        exo_pause.nextFocusUpId = R.id.tv_next_button
                        exo_play.nextFocusUpId = R.id.tv_next_button
                    }
                    else -> {
                        exo_pause.nextFocusUpId = R.id.tv_subtitle_toggle
                        exo_play.nextFocusUpId = R.id.tv_subtitle_toggle
                    }
                }

                if (!tv_title_player.isControllerVisible) {
                    tv_title_player.showController()
                    tv_title_player.exo_pause.requestFocus()
                }

                if (continue_watching_dialog_root.isVisible) {
                    continue_watching_dialog_yes.requestFocus()
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
                if (!tv_title_player.isControllerVisible) {
                    tv_title_player.showController()
                    tv_title_player.exo_pause.requestFocus()
                }

                if (tv_title_player.player!!.isPlaying) {
                    tv_subtitle_toggle.nextFocusDownId = R.id.exo_pause
                    tv_next_button.nextFocusDownId = R.id.exo_pause
                    tv_next_button?.nextFocusDownId = R.id.exo_pause
                } else {
                    tv_subtitle_toggle.nextFocusDownId = R.id.exo_play
                    tv_next_button.nextFocusDownId = R.id.exo_play
                    tv_next_button?.nextFocusDownId = R.id.exo_play
                }

                if (continue_watching_dialog_root.isVisible) {
                    continue_watching_dialog_home.requestFocus()
                }
            }
            KeyEvent.KEYCODE_DPAD_LEFT -> {
                if (!tv_title_player.isControllerVisible) {
                    tv_title_player.showController()
                }

                if (tv_next_button.isFocused || tv_next_button.isFocused) {
                    tv_subtitle_toggle.requestFocus()
                } else if (tv_subtitle_toggle.isFocused) {
                    tv_exo_back.requestFocus()
                } else {
                    tv_title_player.exo_rew.callOnClick()
                }

                if (tv_next_button.isFocused || tv_next_button.isFocused && tv_subtitle_toggle.isGone) {
                    tv_exo_back.requestFocus()
                }

                if (!continue_watching_dialog_root.isVisible) {
                    tv_title_player.setRewindIncrementMs(rewIncrement)
                }
                return true
            }
            KeyEvent.KEYCODE_DPAD_RIGHT -> {
                if (!tv_title_player.isControllerVisible) {
                    tv_title_player.showController()
                }

                if (tv_title_player.player?.mediaItemCount == 1) {
                    if (tv_exo_back.isFocused && tv_subtitle_toggle.isVisible) {
                        tv_subtitle_toggle.requestFocus()
                    } else  if (tv_exo_back.isFocused && tv_subtitle_toggle.isGone){
                        tv_next_button.requestFocus()
                    } else {
                        tv_title_player.exo_ffwd.callOnClick()
                    }
                } else {
                    if (tv_exo_back.isFocused && tv_subtitle_toggle.isVisible) {
                        tv_subtitle_toggle.requestFocus()
                    } else if (tv_subtitle_toggle.isFocused) {
                        if (tv_next_button.isVisible) {
                            tv_next_button.requestFocus()
                        } else {
                            tv_next_button.requestFocus()
                        }
                    } else if (tv_exo_back.isFocused && tv_subtitle_toggle.isGone) {
                        if (tv_next_button.isVisible) {
                            tv_next_button.requestFocus()
                        } else {
                            tv_next_button.requestFocus()
                        }
                    } else {
                        tv_title_player.exo_ffwd.callOnClick()
                    }
                }

                if (!continue_watching_dialog_root.isVisible) {
                    tv_title_player.setFastForwardIncrementMs(ffIncrement)
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
                if (!continue_watching_dialog_root.isVisible) {
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
                if (!continue_watching_dialog_root.isVisible) {
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
        val titleId = this.intent?.getSerializableExtra("titleId") as Int
        val isTvShow = this.intent?.getSerializableExtra("isTvShow") as Boolean
        if (tv_title_player.isControllerVisible) {
            tv_title_player.hideController()
        } else {
            val intent = Intent(this, TvDetailsActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra("titleId", titleId)
            intent.putExtra("isTvShow", isTvShow)
            startActivity(intent)
            finish()
        }
    }
}