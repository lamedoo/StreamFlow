package com.lukakordzaia.streamflow.animations

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.View
import android.view.animation.AccelerateInterpolator
import androidx.core.util.toHalf
import kotlinx.android.synthetic.main.phone_search_titles_framgent_new.*

class SearchAnimations {
    fun textTopTop(view: View, container: View, duration: Long, Unit: () -> Unit) {
        val height = container.height.toFloat()
        val width = container.width.toFloat()
        val animateTextTop = ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, 0F, -height)
        val animateTextFullWidth = ObjectAnimator.ofFloat(view, View.TRANSLATION_X, -100F)
        val moveTextToTop = AnimatorSet().apply {
            interpolator = AccelerateInterpolator()
            this.duration = duration
            play(animateTextTop)
        }
        moveTextToTop.start()
        moveTextToTop.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator?) {
            }

            override fun onAnimationEnd(animation: Animator?) {
                Unit()
                val animateTextBottom = ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, -height, 0F)
                val moveTextToBottom = AnimatorSet().apply {
                    interpolator = AccelerateInterpolator()
                    this.duration = duration
                    play(animateTextBottom)
                }
                moveTextToBottom.start()
            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationRepeat(animation: Animator?) {
            }

        })
    }
}