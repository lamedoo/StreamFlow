package com.lukakordzaia.streamflow.ui.tv

import android.os.Bundle
import android.view.KeyEvent
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import com.lukakordzaia.streamflow.R
import com.lukakordzaia.streamflow.utils.setGone
import com.lukakordzaia.streamflow.utils.setVisible
import kotlinx.android.synthetic.main.activity_tv.*
import kotlinx.android.synthetic.main.tv_sidebar.*

class TvActivity : FragmentActivity(), CheckFirstItem {

    private var isFirstItem = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tv)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_DPAD_LEFT -> {
                if (isFirstItem) {
                    tv_sidebar_sidebar.setVisible()
                    tv_sidebar_home.requestFocus()
                }
            }
            KeyEvent.KEYCODE_DPAD_RIGHT -> {
                if (tv_sidebar_sidebar.isVisible) {
                    tv_sidebar_sidebar.setGone()
                }
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun isFirstItem(boolean: Boolean) {
        isFirstItem = boolean
    }
}

interface CheckFirstItem {
    fun isFirstItem(boolean: Boolean)
}