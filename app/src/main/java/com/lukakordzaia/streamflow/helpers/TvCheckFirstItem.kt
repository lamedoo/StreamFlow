package com.lukakordzaia.streamflow.helpers

import androidx.leanback.app.RowsSupportFragment

interface TvCheckFirstItem {
    fun isFirstItem(boolean: Boolean, rowsSupportFragment: RowsSupportFragment?, rowsPosition: Int?)
}