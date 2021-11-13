package com.lukakrodzaia.streamflowtv.ui.genres

import android.os.Bundle
import androidx.core.content.ContextCompat
import com.lukakrodzaia.streamflowtv.R
import com.lukakrodzaia.streamflowtv.baseclasses.activities.BaseInfoFragmentActivity

class TvGenresActivity: BaseInfoFragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setFragment(TvGenresFragment())
        binding.tvSidebarCollapsed.collapsedGenresIcon.setColorFilter(ContextCompat.getColor(this, R.color.accent_color))
    }
}