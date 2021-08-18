package com.lukakordzaia.streamflow.customviews

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.lukakordzaia.streamflow.databinding.TvDefaultCardViewBinding
import com.lukakordzaia.streamflow.utils.setImage

open class TvDefaultCardView(context: Context, attrs: AttributeSet?) : ConstraintLayout(context, attrs) {
    val binding = TvDefaultCardViewBinding.inflate(LayoutInflater.from(context), this, true)

    fun setPoster(poster: String?) {
        binding.cardPoster.setImage(poster, true)
    }
}