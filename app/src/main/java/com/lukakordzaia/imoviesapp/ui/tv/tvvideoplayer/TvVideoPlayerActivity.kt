package com.lukakordzaia.imoviesapp.ui.tv.tvvideoplayer

import android.os.Bundle
import android.view.KeyEvent
import androidx.fragment.app.FragmentActivity
import com.lukakordzaia.imoviesapp.R
import kotlinx.android.synthetic.main.fragment_tv_video_player.*

class TvVideoPlayerActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tv_video_player)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_DPAD_CENTER -> {
                if (!tv_title_player.isControllerVisible) {
                    tv_title_player.showController()
                }
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }
}