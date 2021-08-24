package com.lukakordzaia.streamflow.customviews

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.lukakordzaia.streamflow.databinding.TvDetailsEpisodesItemBinding
import com.lukakordzaia.streamflow.utils.setImage
import com.lukakordzaia.streamflow.utils.setVisible
import com.lukakordzaia.streamflow.utils.setVisibleOrGone

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
        binding.itemSeekBar.setVisible()
    }

    fun setProgress(progress: Int, max: Int) {
        binding.itemSeekBar.progress = progress
        binding.itemSeekBar.max = max
    }
}