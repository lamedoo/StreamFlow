package com.lukakordzaia.streamflowtv.customviews

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.lukakordzaia.core.utils.setImage
import com.lukakordzaia.core.utils.setVisibleOrGone
import com.lukakordzaia.streamflowtv.databinding.TvDetailsEpisodesItemBinding

class TvEpisodesCardView(context: Context, attrs: AttributeSet?) : ConstraintLayout(context, attrs) {
    val binding = TvDetailsEpisodesItemBinding.inflate(LayoutInflater.from(context), this, true)

    fun setPoster(poster: String?) {
        binding.cardPoster.setImage(poster, false)
    }

    fun setName(title: String?) {
        binding.episodeName.text = title
    }

    fun setNumber(number: String) {
        binding.episodeNumber.text = number
    }

    fun setSeekbarVisibility(visibility: Boolean) {
        binding.itemSeekBar.setVisibleOrGone(visibility)
    }
}