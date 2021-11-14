package com.lukakordzaia.streamflowtv.customviews

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.lukakordzaia.core.utils.setImage
import com.lukakordzaia.streamflowtv.databinding.TvDefaultCardViewBinding

class TvDefaultCardView(context: Context, attrs: AttributeSet?) : ConstraintLayout(context, attrs) {
    val binding = TvDefaultCardViewBinding.inflate(LayoutInflater.from(context), this, true)

    fun setPoster(poster: String?) {
        binding.cardPoster.setImage(poster, true)
    }
}