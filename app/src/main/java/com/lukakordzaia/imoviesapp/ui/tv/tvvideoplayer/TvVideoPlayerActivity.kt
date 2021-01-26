package com.lukakordzaia.imoviesapp.ui.tv.tvvideoplayer

import android.os.Bundle
import android.view.KeyEvent
import androidx.fragment.app.FragmentActivity
import com.lukakordzaia.imoviesapp.R
import kotlinx.android.synthetic.main.fragment_video_player.*

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
                }
                return true
            }
            KeyEvent.KEYCODE_MEDIA_PAUSE -> {
                title_player.showController()
                title_player.player!!.pause()
                return true
            }
            KeyEvent.KEYCODE_MEDIA_PLAY -> {
                title_player.player!!.play()
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }
}