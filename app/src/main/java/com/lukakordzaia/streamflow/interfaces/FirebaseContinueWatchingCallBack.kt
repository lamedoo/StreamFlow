package com.lukakordzaia.streamflow.network

import com.lukakordzaia.streamflow.database.continuewatchingdb.ContinueWatchingRoom

interface FirebaseContinueWatchingCallBack {
    fun continueWatchingTitle(title: ContinueWatchingRoom?)
}

interface FirebaseContinueWatchingListCallBack {
    fun continueWatchingList(titleList: MutableList<ContinueWatchingRoom>)
}