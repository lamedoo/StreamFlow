package com.lukakordzaia.streamflowtv.customviews

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.lukakordzaia.streamflowtv.R
import com.lukakordzaia.streamflowtv.databinding.TvSingleTitleButtonViewBinding

class TvSingleTitleButtonView(context: Context, attrs: AttributeSet?) : LinearLayout(context, attrs) {
    val binding = TvSingleTitleButtonViewBinding.inflate(LayoutInflater.from(context), this, true)

    var title: String? = null
    var color: Int? = null

    init {
        context.theme.obtainStyledAttributes(attrs, R.styleable.TvSingleTitleButtonView, 0, 0).apply {
            try {
                color = getColor(R.styleable.TvSingleTitleButtonView_icon_tint, 0xFFFFFF)
                title = getString(R.styleable.TvSingleTitleButtonView_title)

                binding.buttonTitle.text = title
                binding.buttonIcon.setImageDrawable(getDrawable(R.styleable.TvSingleTitleButtonView_icon))

                if (color != null) {
                    binding.buttonIcon.setColorFilter(color!!)
                }
            } finally {
                recycle()
            }
        }
    }

    fun setNewTitle(newTitle: String) {
        title = newTitle
        binding.buttonTitle.text = title
    }
}