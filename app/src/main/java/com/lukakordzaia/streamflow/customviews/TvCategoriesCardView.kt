package com.lukakordzaia.streamflow.customviews

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.lukakordzaia.streamflow.R
import com.lukakordzaia.streamflow.databinding.TvCategoriesCardViewBinding
open class TvCategoriesCardView(context: Context, attrs: AttributeSet?) : ConstraintLayout(context, attrs) {
    val binding = TvCategoriesCardViewBinding.inflate(LayoutInflater.from(context), this, true)

    fun setPoster(poster: Int?) {
        Glide.with(context)
            .load(poster ?: R.drawable.placeholder_movie_landscape)
            .placeholder( R.drawable.placeholder_movie_landscape)
            .into(binding.cardPoster)
    }

    fun setTitle(title: String) {
        binding.cardTitle.text = title
    }
}