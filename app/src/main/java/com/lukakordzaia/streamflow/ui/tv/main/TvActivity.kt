package com.lukakordzaia.streamflow.ui.tv.main

import android.os.Bundle
import androidx.core.content.ContextCompat
import com.lukakordzaia.streamflow.R
import com.lukakordzaia.streamflow.interfaces.TvCheckTitleSelected
import com.lukakordzaia.streamflow.ui.baseclasses.activities.BaseInfoFragmentActivity

class TvActivity : BaseInfoFragmentActivity(), TvCheckTitleSelected {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences.saveTvVideoPlayerOn(false)
        setFragment(TvMainFragment())
        binding.tvSidebarCollapsed.collapsedHomeIcon.setColorFilter(ContextCompat.getColor(this, R.color.accent_color))
    }
}