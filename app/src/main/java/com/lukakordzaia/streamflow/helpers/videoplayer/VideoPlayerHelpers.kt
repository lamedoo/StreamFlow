package com.lukakordzaia.streamflow.helpers.videoplayer

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.widget.ImageButton
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.CaptionStyleCompat
import com.google.android.exoplayer2.ui.SubtitleView
import com.lukakordzaia.streamflow.R
import com.lukakordzaia.streamflow.animations.VideoPlayerAnimations
import com.lukakordzaia.streamflow.utils.setGone
import com.lukakordzaia.streamflow.utils.setInvisible
import com.lukakordzaia.streamflow.utils.setVisible

class VideoPlayerHelpers(private val context: Context) {
    fun subtitleFunctions(view: SubtitleView, toggle: ImageButton, player: SimpleExoPlayer, hasSubs: Boolean) {
        player.addTextOutput {
            view.onCues(it)

            view.setStyle(
                CaptionStyleCompat(
                    ContextCompat.getColor(context, R.color.white),
                    ContextCompat.getColor(context, R.color.transparent),
                    ContextCompat.getColor(context, R.color.transparent),
                    CaptionStyleCompat.EDGE_TYPE_DROP_SHADOW,
                    ContextCompat.getColor(context, R.color.black),
                    Typeface.DEFAULT_BOLD,
                )
            )
            view.setFixedTextSize(2, 25F)
        }

        if (hasSubs) {
            toggle.setVisible()
            toggle.imageTintList = ColorStateList.valueOf(
                ContextCompat.getColor(
                    context,
                    R.color.accent_color
                )
            )
        } else {
            toggle.setGone()
        }

        toggle.setOnClickListener {
            if (view.isVisible) {
                view.setInvisible()
                VideoPlayerAnimations().setSubtitleOff(toggle, 200, context)
            } else {
                view.setVisible()
                VideoPlayerAnimations().setSubtitleOn(toggle, 200, context)
            }
        }
    }
}