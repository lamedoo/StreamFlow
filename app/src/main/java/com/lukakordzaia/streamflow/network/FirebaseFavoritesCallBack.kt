package com.lukakordzaia.streamflow.network

interface FavoritesCallBack {
    fun moviesList(movies: MutableList<Int>)
    fun tvShowsList(tvShows: MutableList<Int>)
}