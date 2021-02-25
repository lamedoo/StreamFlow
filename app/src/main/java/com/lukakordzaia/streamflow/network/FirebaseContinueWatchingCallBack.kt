package com.lukakordzaia.streamflow.network

import com.lukakordzaia.streamflow.database.DbDetails

interface FirebaseContinueWatchingCallBack {
    fun continueWatchingTitle(title: DbDetails)
}

interface FirebaseContinueWatchingListCallBack {
    fun continueWatchingList(titleList: MutableList<DbDetails>)
}