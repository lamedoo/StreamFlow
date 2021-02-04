package com.lukakordzaia.imoviesapp.ui.tv.tvvideoplayer

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.KeyEvent
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import com.lukakordzaia.imoviesapp.R
import com.lukakordzaia.imoviesapp.ui.tv.details.TvDetailsActivity
import com.lukakordzaia.imoviesapp.utils.setInvisible
import com.lukakordzaia.imoviesapp.utils.setVisible
import kotlinx.android.synthetic.main.exoplayer_controller_layout.view.*
import kotlinx.android.synthetic.main.fragment_video_player.*
import java.util.concurrent.TimeUnit

class TvVideoPlayerActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tv_video_player)

    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_DPAD_CENTER -> {
                if (!title_player.isControllerVisible) {
                    title_player.showController()
                    title_player.player!!.pause()
                } else if (!title_player.player!!.isPlaying && !title_player.isControllerVisible) {
                    title_player.player!!.play()
                }
                return true
            }
            KeyEvent.KEYCODE_DPAD_DOWN -> {
                if (!title_player.isControllerVisible) {
                    title_player.showController()
                    title_player.exo_progress.requestFocus()
                }
            }
            KeyEvent.KEYCODE_DPAD_LEFT -> {
                if (!title_player.isControllerVisible) {
                    title_player.exo_rew.callOnClick()
                    displayCurrentPosition()
                }
            }
            KeyEvent.KEYCODE_DPAD_RIGHT -> {
                if (!title_player.isControllerVisible) {
                    title_player.exo_ffwd.callOnClick()
                    displayCurrentPosition()
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
                title_player.dispatchMediaKeyEvent(event!!)
                displayCurrentPosition()
                return true
            }
            KeyEvent.KEYCODE_MEDIA_NEXT -> {
                title_player.dispatchMediaKeyEvent(event!!)
                return true
            }
            KeyEvent.KEYCODE_MEDIA_REWIND -> {
                title_player.dispatchMediaKeyEvent(event!!)
                displayCurrentPosition()
                return true
            }
            KeyEvent.KEYCODE_MEDIA_PREVIOUS -> {
                title_player.dispatchMediaKeyEvent(event!!)
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onBackPressed() {
        val titleId = this.intent?.getSerializableExtra("titleId") as Int
        val isTvShow = this.intent?.getSerializableExtra("isTvShow") as Boolean
        if (title_player.isControllerVisible) {
            title_player.hideController()
        } else {
            val intent = Intent(this, TvDetailsActivity::class.java)
            intent.putExtra("titleId", titleId)
            intent.putExtra("isTvShow", isTvShow)
            startActivity(intent)
            finish()
        }
    }

    private fun displayCurrentPosition() {
        position_on_skip.setVisible()
        current_position.text = String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(title_player.player!!.currentPosition),
                TimeUnit.MILLISECONDS.toMinutes(title_player.player!!.currentPosition) -
                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(title_player.player!!.currentPosition)),
                TimeUnit.MILLISECONDS.toSeconds(title_player.player!!.currentPosition) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(title_player.player!!.currentPosition))
        )

        media_duration.text = String.format(" - %02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(title_player.player!!.duration),
                TimeUnit.MILLISECONDS.toMinutes(title_player.player!!.duration) -
                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(title_player.player!!.duration)),
                TimeUnit.MILLISECONDS.toSeconds(title_player.player!!.duration) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(title_player.player!!.duration))
        )
        hidePosition()
    }

    private fun hidePosition() {
        Handler(Looper.getMainLooper()).postDelayed({
            position_on_skip.setInvisible()
        }, 2000)
    }
}