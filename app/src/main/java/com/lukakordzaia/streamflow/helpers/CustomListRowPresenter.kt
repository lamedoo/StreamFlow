package com.lukakordzaia.streamflow.helpers

import android.text.TextUtils
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.leanback.widget.ListRowPresenter
import androidx.leanback.widget.Presenter
import androidx.leanback.widget.Row
import androidx.leanback.widget.RowHeaderPresenter
import com.lukakordzaia.streamflow.R
import com.lukakordzaia.streamflow.utils.setMargin

class CustomListRowPresenter : ListRowPresenter() {
    init {
        headerPresenter = CustomRowHeaderPresenter()
    }

    internal class CustomRowHeaderPresenter : RowHeaderPresenter() {
        override fun onBindViewHolder(viewHolder: Presenter.ViewHolder, item: Any) {
            val headerItem = (item as Row).headerItem
            val vh = viewHolder as ViewHolder
            val title = vh.view.findViewById<TextView>(R.id.row_header)
            if (!TextUtils.isEmpty(headerItem!!.name)) {
                title.text = headerItem.name
                title.typeface = ResourcesCompat.getFont(title.context, R.font.georgian_uppercase)
                title.setPadding(0, 12, 0, 0)
                title.setMargin(0, 0, 0, -300)
                title.textSize = 16F
            }
        }
    }
}