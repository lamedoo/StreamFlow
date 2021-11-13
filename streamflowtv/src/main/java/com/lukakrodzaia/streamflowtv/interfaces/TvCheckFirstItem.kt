package com.lukakrodzaia.streamflowtv.interfaces

import androidx.leanback.app.RowsSupportFragment

interface TvCheckFirstItem {
    fun isFirstItem(boolean: Boolean, rowsSupportFragment: RowsSupportFragment?, rowsPosition: Int?)
}