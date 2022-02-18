package com.lukakordzaia.streamflowtv.interfaces

import com.lukakordzaia.core.datamodels.ContinueWatchingModel
import com.lukakordzaia.core.datamodels.TvInfoModel

interface TvTitleSelected {
    fun getTitleId(info: TvInfoModel, continueWatchingDetails: ContinueWatchingModel? = null)
}