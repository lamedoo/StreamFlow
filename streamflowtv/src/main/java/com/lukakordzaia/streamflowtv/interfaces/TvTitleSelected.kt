package com.lukakordzaia.streamflowtv.interfaces

import com.lukakordzaia.core.domain.domainmodels.ContinueWatchingModel
import com.lukakordzaia.core.datamodels.TvInfoModel

interface TvTitleSelected {
    fun getTitleId(info: TvInfoModel, continueWatchingDetails: ContinueWatchingModel? = null)
}