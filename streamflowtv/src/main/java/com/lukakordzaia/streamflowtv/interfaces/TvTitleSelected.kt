package com.lukakordzaia.streamflowtv.interfaces

import com.lukakordzaia.core.datamodels.ContinueWatchingModel

interface TvTitleSelected {
    fun getTitleId(titleId: Int, continueWatchingDetails: ContinueWatchingModel?)
}