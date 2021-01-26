package com.lukakordzaia.imoviesapp.utils

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController

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

fun infiniteScroll(view: NestedScrollView, f: () -> Unit) {
    view.setOnScrollChangeListener {
            v: NestedScrollView, _: Int, scrollY: Int, _: Int, oldScrollY: Int ->

        if (scrollY == v.getChildAt(v.childCount - 1).measuredHeight - v.measuredHeight &&
            scrollY > oldScrollY) {

            f()

        }
    }
}
