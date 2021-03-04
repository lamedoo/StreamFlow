package com.lukakordzaia.streamflow.ui.tv.tvvideoplayer

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.KeyEvent
import androidx.fragment.app.FragmentActivity
import com.lukakordzaia.streamflow.R
import com.lukakordzaia.streamflow.ui.tv.details.TvDetailsActivity
import kotlinx.android.synthetic.main.tv_exoplayer_controller_layout.view.*
import kotlinx.android.synthetic.main.tv_video_player_fragment.*

class TvVideoPlayerActivity : FragmentActivity() {
    private var ffIncrement = 10000
    private var rewIncrement = 10000

    private val timerHandler = Handler()
    private val ffTimerRunnable = Runnable {
        ffIncrement = 50000
    }

    private val rewTimerRunnable = Runnable {
        rewIncrement = 50000
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tv_video_player)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode) {
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
            }
            KeyEvent.KEYCODE_DPAD_LEFT -> {
                if (!tv_title_player.isControllerVisible) {
                    tv_title_player.showController()
                }

                tv_title_player.exo_rew.callOnClick()

                tv_title_player.setRewindIncrementMs(rewIncrement)
                timerHandler.postDelayed(rewTimerRunnable, 2000)
                return true
            }
            KeyEvent.KEYCODE_DPAD_RIGHT -> {
                if (!tv_title_player.isControllerVisible) {
                    tv_title_player.showController()
                }

                tv_title_player.exo_ffwd.callOnClick()

                tv_title_player.setFastForwardIncrementMs(ffIncrement)
                timerHandler.postDelayed(ffTimerRunnable, 2000)
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
                tv_title_player.dispatchMediaKeyEvent(event!!)
                return true
            }
//            KeyEvent.KEYCODE_MEDIA_NEXT -> {
//                tv_title_player.dispatchMediaKeyEvent(event!!)
//                return true
//            }
            KeyEvent.KEYCODE_MEDIA_REWIND -> {
                tv_title_player.dispatchMediaKeyEvent(event!!)
                return true
            }
//            KeyEvent.KEYCODE_MEDIA_PREVIOUS -> {
//                tv_title_player.dispatchMediaKeyEvent(event!!)
//                return true
//            }
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_DPAD_LEFT -> {
                timerHandler.removeCallbacks(rewTimerRunnable)
                return true
            }
            KeyEvent.KEYCODE_DPAD_RIGHT -> {
                timerHandler.removeCallbacks(ffTimerRunnable)
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
            intent.putExtra("titleId", titleId)
            intent.putExtra("isTvShow", isTvShow)
            startActivity(intent)
            finish()
        }
    }
}