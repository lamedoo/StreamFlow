package com.lukakrodzaia.streamflowtv.ui.main

import android.os.Bundle
import androidx.core.content.ContextCompat
import com.lukakrodzaia.streamflowtv.R
import com.lukakrodzaia.streamflowtv.baseclasses.activities.BaseInfoFragmentActivity
import com.lukakrodzaia.streamflowtv.interfaces.TvCheckTitleSelected

class TvActivity : BaseInfoFragmentActivity(), TvCheckTitleSelected {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences.saveTvVideoPlayerOn(false)
        setFragment(TvMainFragment())
        binding.tvSidebarCollapsed.collapsedHomeIcon.setColorFilter(ContextCompat.getColor(this, R.color.accent_color))
    }
}