package com.lukakordzaia.streamflowtv.ui.main.presenters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.leanback.widget.ListRow
import androidx.leanback.widget.Presenter
import androidx.leanback.widget.RowHeaderPresenter
import com.lukakordzaia.streamflowtv.R

class TvHeaderItemPresenter : RowHeaderPresenter() {
    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).run { inflate(R.layout.tv_header_item, null) })
    }

    override fun onBindViewHolder(viewHolder: Presenter.ViewHolder?, item: Any?) {
        super.onBindViewHolder(viewHolder, item)
        val headerItem = (item as ListRow).headerItem
        val rootView = viewHolder!!.view

        rootView.findViewById<TextView>(R.id.header_name).apply {
            text = headerItem.name
        }
    }

}