package com.lukakordzaia.streamflow.network

import com.lukakordzaia.streamflow.datamodels.AddTitleToFirestore

interface FirebaseCallBack {
    fun moviesList(movies: MutableList<Int>)
    fun tvShowsList(tvShows: MutableList<Int>)
}