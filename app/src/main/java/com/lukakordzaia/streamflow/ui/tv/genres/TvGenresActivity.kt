package com.lukakordzaia.streamflow.ui.tv.genres

import android.os.Bundle
import androidx.core.content.ContextCompat
import com.lukakordzaia.streamflow.R
import com.lukakordzaia.streamflow.ui.baseclasses.activities.BaseInfoFragmentActivity

class TvGenresActivity: BaseInfoFragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setFragment(TvGenresFragment())
        binding.tvSidebarCollapsed.collapsedGenresIcon.setColorFilter(ContextCompat.getColor(this, R.color.accent_color))
    }
}