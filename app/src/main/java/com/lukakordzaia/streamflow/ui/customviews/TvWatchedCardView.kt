package com.lukakordzaia.streamflow.ui.customviews

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import com.lukakordzaia.streamflow.R
import kotlinx.android.synthetic.main.tv_watched_card_view.view.*

open class TvWatchedCardView(context: Context, attrs: AttributeSet?) : ConstraintLayout(context, attrs) {
    init {
        inflate(context, R.layout.tv_watched_card_view, this)
    }

    fun setPosterDimensions(width: Int, height: Int) {
        val posterLp = tv_watched_card_poster.layoutParams
        posterLp.width = width
        posterLp.height = height
        tv_watched_card_poster.layoutParams = posterLp
    }
}