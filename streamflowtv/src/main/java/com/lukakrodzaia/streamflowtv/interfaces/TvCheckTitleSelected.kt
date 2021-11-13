package com.lukakrodzaia.streamflowtv.interfaces

import com.lukakordzaia.core.datamodels.ContinueWatchingModel

interface TvCheckTitleSelected {
    fun getTitleId(titleId: Int, continueWatchingDetails: ContinueWatchingModel?)
}