package com.lukakordzaia.streamflow.helpers

import android.text.TextUtils
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.leanback.widget.*
import com.lukakordzaia.streamflow.R
import com.lukakordzaia.streamflow.utils.setMargin

class CustomListRowPresenter : ListRowPresenter(FocusHighlight.ZOOM_FACTOR_NONE, false) {
    init {
        headerPresenter = CustomRowHeaderPresenter()
    }

    override fun createRowViewHolder(parent: ViewGroup?): RowPresenter.ViewHolder {
        val viewHolder = super.createRowViewHolder(parent)

        with((viewHolder.view as ListRowView).gridView) {
            windowAlignment = BaseGridView.WINDOW_ALIGN_LOW_EDGE
            windowAlignmentOffsetPercent = 0f
            windowAlignmentOffset = parent!!.resources.getDimensionPixelSize(R.dimen.lb_browse_padding_start)
            itemAlignmentOffsetPercent = 0f
        }

        return viewHolder
    }

    internal class CustomRowHeaderPresenter : RowHeaderPresenter() {
        override fun onBindViewHolder(viewHolder: Presenter.ViewHolder, item: Any) {
            val headerItem = (item as Row).headerItem
            val vh = viewHolder as ViewHolder
            val title = vh.view.findViewById<TextView>(R.id.row_header)
            if (!TextUtils.isEmpty(headerItem?.name)) {
                title.text = headerItem.name
                title.typeface = ResourcesCompat.getFont(title.context, R.font.georgian_uppercase)
                title.setPadding(0, 12, 0, 0)
                title.setMargin(0, 0, 0, -300)
                title.textSize = 16F
            }
        }
    }
}