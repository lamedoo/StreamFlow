package com.lukakordzaia.streamflowtv.ui.tvcatalogue

import android.os.Bundle
import androidx.core.content.ContextCompat
import com.lukakordzaia.streamflowtv.R
import com.lukakordzaia.streamflowtv.baseclasses.activities.BaseInfoFragmentActivity

class TvCatalogueActivity : BaseInfoFragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setFragment(TvCatalogueFragment())
        binding.tvSidebarCollapsed.collapsedMoviesIcon.setColorFilter(ContextCompat.getColor(this, R.color.accent_color))
    }
}