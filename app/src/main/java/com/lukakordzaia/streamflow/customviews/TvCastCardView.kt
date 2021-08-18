package com.lukakordzaia.streamflow.customviews

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.lukakordzaia.streamflow.databinding.TvCastCardItemBinding
import com.lukakordzaia.streamflow.utils.setImage

class TvCastCardView(context: Context, attrs: AttributeSet?) : ConstraintLayout(context, attrs) {
    val binding = TvCastCardItemBinding.inflate(LayoutInflater.from(context), this, true)

    fun setPoster(poster: String?) {
        binding.castItemPoster.setImage(poster, false)
    }

    fun setTitle(title: String) {
        binding.castItemName.text = title
    }
}