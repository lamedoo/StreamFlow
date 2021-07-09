package com.lukakordzaia.streamflow.interfaces

interface FavoritesCallBack {
    fun moviesList(movies: MutableList<Int>)
    fun tvShowsList(tvShows: MutableList<Int>)
}