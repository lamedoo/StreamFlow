package com.lukakordzaia.streamflow.animations

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.util.Log
import android.view.View
import android.view.animation.AccelerateInterpolator
import com.lukakordzaia.streamflow.utils.setInvisible
import com.lukakordzaia.streamflow.utils.setVisible

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

    fun showPlayButton(view: View, duration: Long) {
        val collapsedPlayAlphaVisible = ObjectAnimator.ofFloat(view, View.ALPHA, 0f, 1f)
        val setPlayButton = AnimatorSet().apply {
            interpolator = AccelerateInterpolator()
            this.duration = duration
            play(collapsedPlayAlphaVisible)
        }
        setPlayButton.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator?) {
                view.setVisible()
                Log.d("animationshow", "started")
            }

            override fun onAnimationEnd(animation: Animator?) {
                Log.d("animationshow", "ended")
            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationRepeat(animation: Animator?) {
            }
        })
        setPlayButton.start()
    }

    fun hidePlayButton(view: View, duration: Long) {
        val collapsedPlayAlphaGone = ObjectAnimator.ofFloat(view, View.ALPHA, 1f, 0f)
        val removePlayButton = AnimatorSet().apply {
            interpolator = AccelerateInterpolator()
            this.duration = duration
            play(collapsedPlayAlphaGone)
        }
        removePlayButton.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator?) {
                Log.d("animationthide", "started")
            }

            override fun onAnimationEnd(animation: Animator?) {
                view.setInvisible()
                Log.d("animationhide", "started")
            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationRepeat(animation: Animator?) {
            }
        })
        removePlayButton.start()
    }
}