package com.lukakordzaia.streamflowphone.animations

import android.animation.AnimatorSet
import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.content.Context
import android.view.animation.AccelerateInterpolator
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.lukakordzaia.streamflowphone.R

class VideoPlayerAnimations {
    fun setSubtitleOn(view: ImageView, context: Context) {
            val subtitleOn = ObjectAnimator.ofObject(
                view,
                "colorFilter",
                ArgbEvaluator(),
                ContextCompat.getColor(context, R.color.white),
                ContextCompat.getColor(context, R.color.accent_color)
            )
        playAnimation(subtitleOn)
    }

    fun setSubtitleOff(view: ImageView, context: Context) {
        val subtitleOff = ObjectAnimator.ofObject(
                view,
                "colorFilter",
                ArgbEvaluator(),
                ContextCompat.getColor(context, R.color.accent_color),
                ContextCompat.getColor(context, R.color.white)
        )
        playAnimation(subtitleOff)
    }

    private fun playAnimation(animation: ObjectAnimator) {
        val setPlayButton = AnimatorSet().apply {
            this.interpolator = AccelerateInterpolator()
            this.duration = 200
            play(animation)
        }
        setPlayButton.start()
    }
}