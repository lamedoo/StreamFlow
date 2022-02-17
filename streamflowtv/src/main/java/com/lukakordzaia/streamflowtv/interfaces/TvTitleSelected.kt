package com.lukakordzaia.streamflowtv.interfaces

import com.lukakordzaia.core.domain.domainmodels.ContinueWatchingModel

interface TvTitleSelected {
    fun getTitleId(titleId: Int, continueWatchingDetails: ContinueWatchingModel?)
}