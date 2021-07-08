package com.lukakordzaia.streamflow.interfaces

import androidx.leanback.app.RowsSupportFragment

interface TvCheckFirstItem {
    fun isFirstItem(boolean: Boolean, rowsSupportFragment: RowsSupportFragment?, rowsPosition: Int?)
}