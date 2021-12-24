package com.lukakordzaia.streamflowtv.ui.tvsingletitle.tvepisodes

import androidx.leanback.widget.VerticalGridPresenter
import androidx.leanback.widget.VerticalGridView

import android.view.LayoutInflater
import android.view.View

import android.view.ViewGroup
import com.lukakordzaia.streamflowtv.R


class StandardGridPresenter(focusZoomFactor: Int, useFocusDimmer: Boolean) : VerticalGridPresenter(focusZoomFactor, useFocusDimmer) {
    private var gridView: VerticalGridView? = null

    override fun createGridViewHolder(parent: ViewGroup): ViewHolder {
        val root: View = LayoutInflater.from(parent.context).inflate(
            R.layout.standard_grid_presenter_view,
            parent, false
        )
        gridView = root.findViewById(R.id.browse_grid)

        val width = gridView!!.context.resources.displayMetrics.widthPixels * 0.3.toInt()

        gridView!!.setColumnWidth(width)
        return ViewHolder(gridView)
    }

    fun getGridView(): VerticalGridView? {
        return gridView
    }
}