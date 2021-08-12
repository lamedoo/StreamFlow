package com.lukakordzaia.streamflow.interfaces

import com.lukakordzaia.streamflow.datamodels.ContinueWatchingModel

interface TvCheckTitleSelected {
    fun getTitleId(titleId: Int, continueWatchingDetails: ContinueWatchingModel?)
}