package com.lukakrodzaia.streamflowtv.customviews

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.lukakordzaia.core.utils.setImage
import com.lukakordzaia.core.utils.setVisibleOrGone
import com.lukakrodzaia.streamflowtv.databinding.TvDetailsEpisodesItemBinding

class TvEpisodesCardView(context: Context, attrs: AttributeSet?) : ConstraintLayout(context, attrs) {
    val binding = TvDetailsEpisodesItemBinding.inflate(LayoutInflater.from(context), this, true)
    var progress = 0
    var max = 0

    fun setPoster(poster: String?) {
        binding.cardPoster.setImage(poster, false)
    }

    fun setName(title: String?) {
        binding.episodeName.text = title
    }

    fun setNumber(number: String) {
        binding.episodeNumber.text = number
    }

    fun currentIndicatorVisibility(visibility: Boolean) {
        binding.currentIndicator.setVisibleOrGone(visibility)
    }

    fun posterDimVisibility(visibility: Boolean) {
        binding.posterDim.setVisibleOrGone(visibility)
    }

    fun nameVisibility(visibility: Boolean) {
        binding.episodeName.setVisibleOrGone(visibility)
    }

    fun setIndicatorDrawable(drawable: Drawable?) {
        binding.currentIndicator.setImageDrawable(drawable)
    }

    fun setSeekbarVisibility(visibility: Boolean) {
        binding.itemSeekBar.setVisibleOrGone(visibility)
    }

    fun setProgress(progress: Int, max: Int) {
        this.progress = progress
        this.max = max

        binding.itemSeekBar.progress = this.progress
        binding.itemSeekBar.max = this.max

        invalidate()
    }
}