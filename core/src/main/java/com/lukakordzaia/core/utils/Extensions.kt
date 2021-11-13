package com.lukakordzaia.core.utils

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.lukakordzaia.core.R
import java.util.concurrent.TimeUnit

inline fun <T : Fragment> T.applyBundle(block: Bundle.() -> Unit): T {
    val bundle = Bundle()
    bundle.block()
    arguments = bundle
    return this
}

fun <T : Fragment> T.navController(navId: NavDirections) {
    findNavController().navigate(navId)
}

fun Context?.createToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    this?.let { Toast.makeText(it, message, duration).show() }
}

fun View.setVisible() {
    visibility = View.VISIBLE
}

fun View.setGone() {
    visibility = View.GONE
}

fun View.setInvisible() {
    visibility = View.INVISIBLE
}

fun View.setMargin(left: Int? = null, top: Int? = null, right: Int? = null, bottom: Int? = null) {
    val params = (layoutParams as? ViewGroup.MarginLayoutParams)
    params?.setMargins(
        left ?: params.leftMargin,
        top ?: params.topMargin,
        right ?: params.rightMargin,
        bottom ?: params.bottomMargin)
    layoutParams = params
}

fun View.setVisibleOrGone(visibility: Boolean) {
    this.visibility = if (visibility) View.VISIBLE else View.GONE
}

fun View.showKeyboard() {
    this.requestFocus()
    val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
}

fun View.hideKeyboard() {
    val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
}

fun ImageView.setImage(image: String?, isPhone: Boolean) {
    Glide.with(context)
        .load(image ?: if (isPhone) R.drawable.placeholder_movie else R.drawable.placeholder_movie_landscape)
        .placeholder(if (isPhone) R.drawable.placeholder_movie else R.drawable.placeholder_movie_landscape)
        .into(this)
}

fun View.setDrawableBackground(background: Int) {
    this.background = ResourcesCompat.getDrawable(resources, background, null)
}

fun TextView.setColor(color: Int) {
    this.setTextColor(ResourcesCompat.getColor(resources, color, null))
}

fun Long.videoPlayerPosition(): String {
    return if (TimeUnit.MILLISECONDS.toHours(this) == "0".toLong()) {
        String.format("%02d:%02d",
            TimeUnit.MILLISECONDS.toMinutes(this) -
                    TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(this)),
            TimeUnit.MILLISECONDS.toSeconds(this) -
                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(this))
        )
    } else {
        String.format("%02d:%02d:%02d",
            TimeUnit.MILLISECONDS.toHours(this),
            TimeUnit.MILLISECONDS.toMinutes(this) -
                    TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(this)),
            TimeUnit.MILLISECONDS.toSeconds(this) -
                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(this))
        )
    }
}

fun Long.titlePosition(season: Int?, episode: Int?): String {
    return if (TimeUnit.SECONDS.toHours(this) == "0".toLong()) {
        String.format("${if (season != null) "ს:${season} ე:${episode} / " else ""}%02d:%02d",
            TimeUnit.SECONDS.toMinutes(this),
            TimeUnit.SECONDS.toSeconds(this) -
                    TimeUnit.MINUTES.toSeconds(TimeUnit.SECONDS.toMinutes(this))
        )
    } else {
        String.format("${if (season != null) "ს:${season} ე:${episode} / " else ""}%02d:%02d:%02d",
            TimeUnit.SECONDS.toHours(this),
            TimeUnit.SECONDS.toMinutes(this) -
                    TimeUnit.HOURS.toMinutes(TimeUnit.SECONDS.toHours(this)),
            TimeUnit.SECONDS.toSeconds(this) -
                    TimeUnit.MINUTES.toSeconds(TimeUnit.SECONDS.toMinutes(this))
        )
    }
}