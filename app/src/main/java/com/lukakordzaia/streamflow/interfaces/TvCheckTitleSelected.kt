package com.lukakordzaia.streamflow.interfaces

import com.lukakordzaia.streamflow.datamodels.DbTitleData

interface TvCheckTitleSelected {
    fun getTitleId(titleId: Int, continueWatchingDetails: DbTitleData?)
}