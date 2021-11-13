package com.lukakrodzaia.streamflowtv.animations

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.util.Log
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.lukakordzaia.core.utils.setGone
import com.lukakordzaia.core.utils.setVisible

class TvSidebarAnimations {
    fun showSideBar(view: ConstraintLayout, duration: Long = 100) {

        val sideBarAlpha = ObjectAnimator.ofFloat(view, View.ALPHA, 0F, 1F)
        val sidebarX = ObjectAnimator.ofFloat(view, View.X, -100F, 0F)
        val animatorSet = AnimatorSet()

        animatorSet.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(p0: Animator?) {
                Log.d("sadasdsadas", "dadasdada'")
                view.setVisible()
            }
            override fun onAnimationEnd(p0: Animator?) {}
            override fun onAnimationCancel(p0: Animator?) {}
            override fun onAnimationRepeat(p0: Animator?) {}
        })

        animatorSet.playTogether(
            sideBarAlpha,
            sidebarX
        )
        animatorSet.duration = duration
        animatorSet.start()
    }

    fun hideSideBar(view: ConstraintLayout, duration: Long = 100) {
        val sideBarAlpha = ObjectAnimator.ofFloat(view, View.ALPHA, 1F, 0F)
        val sidebarX = ObjectAnimator.ofFloat(view, View.X, 0F, -100F)
        val animatorSet = AnimatorSet()

        animatorSet.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(p0: Animator?) {}
            override fun onAnimationEnd(p0: Animator?) {
                view.setGone()
            }
            override fun onAnimationCancel(p0: Animator?) {}
            override fun onAnimationRepeat(p0: Animator?) {}
        })

        animatorSet.playTogether(
            sideBarAlpha,
            sidebarX
        )
        animatorSet.duration = duration
        animatorSet.start()
    }
}