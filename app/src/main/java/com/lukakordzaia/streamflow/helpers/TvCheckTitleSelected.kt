package com.lukakordzaia.streamflow.helpers

import com.lukakordzaia.streamflow.datamodels.DbTitleData

interface TvCheckTitleSelected {
    fun getTitleId(titleId: Int, continueWatchingDetails: DbTitleData?)
}