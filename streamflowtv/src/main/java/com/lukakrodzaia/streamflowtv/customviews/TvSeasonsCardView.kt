package com.lukakrodzaia.streamflowtv.customviews

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.lukakrodzaia.streamflowtv.databinding.TvDetailsSeasonItemBinding

open class TvSeasonsCardView(context: Context, attrs: AttributeSet?) : ConstraintLayout(context, attrs) {
    val binding = TvDetailsSeasonItemBinding.inflate(LayoutInflater.from(context), this, true)

    fun setSeason(season: String) {
        binding.seasonNumber.text = season
    }
}