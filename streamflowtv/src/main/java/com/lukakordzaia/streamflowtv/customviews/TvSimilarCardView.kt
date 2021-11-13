package com.lukakordzaia.streamflowtv.customviews

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.lukakordzaia.core.utils.setImage
import com.lukakordzaia.streamflowtv.databinding.TvSimilarCardViewBinding

open class TvSimilarCardView(context: Context, attrs: AttributeSet?) : ConstraintLayout(context, attrs) {
    val binding = TvSimilarCardViewBinding.inflate(LayoutInflater.from(context), this, true)

    fun setPoster(poster: String?) {
        binding.cardPoster.setImage(poster, false)
    }

    fun setTitle(title: String?) {
        binding.cardTitle.text = title
    }
}