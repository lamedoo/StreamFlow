package com.lukakordzaia.streamflow.animations

import android.animation.AnimatorSet
import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.content.Context
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.lukakordzaia.streamflow.R

class VideoPlayerAnimations {
    fun setSubtitleOn(view: ImageView, duration: Long, context: Context) {
            val subtitleOn = ObjectAnimator.ofObject(
                view,
                "colorFilter",
                ArgbEvaluator(),
                ContextCompat.getColor(context, R.color.white),
                ContextCompat.getColor(context, R.color.accent_color)
            )
        val setPlayButton = AnimatorSet().apply {
            this.interpolator = AccelerateInterpolator()
            this.duration = duration
            play(subtitleOn)
        }
        setPlayButton.start()
    }

    fun setSubtitleOff(view: ImageView, duration: Long, context: Context) {
        val subtitleOff = ObjectAnimator.ofObject(
                view,
                "colorFilter",
                ArgbEvaluator(),
                ContextCompat.getColor(context, R.color.accent_color),
                ContextCompat.getColor(context, R.color.white)
        )
        val setPlayButton = AnimatorSet().apply {
            this.interpolator = AccelerateInterpolator()
            this.duration = duration
            play(subtitleOff)
        }
        setPlayButton.start()
    }

    fun moveHeaderUp(view: View, container: View, duration: Long) {
        val collapsedPlayRotate = ObjectAnimator.ofFloat(view, View.Y, view.y, container.y)
        val setPlayButton = AnimatorSet().apply {
            this.interpolator = AccelerateInterpolator()
            this.duration = duration
            play(collapsedPlayRotate)
        }
        setPlayButton.start()
    }
}