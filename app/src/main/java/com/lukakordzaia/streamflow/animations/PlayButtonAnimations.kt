package com.lukakordzaia.streamflow.animations

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.View
import android.view.animation.AccelerateInterpolator

class PlayButtonAnimations {
    fun rotatePlayButton(view: View, duration: Long) {
        val collapsedPlayRotate = ObjectAnimator.ofFloat(view, "rotation", 0f, 360f)
        val setPlayButton = AnimatorSet().apply {
            this.interpolator = AccelerateInterpolator()
            this.duration = duration
            play(collapsedPlayRotate)
        }
        setPlayButton.start()
    }
}