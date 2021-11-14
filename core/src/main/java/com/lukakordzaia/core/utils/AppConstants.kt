package com.lukakordzaia.core.utils

class AppConstants {
    companion object {
        const val NAV_HOME_TO_SINGLE = 0
        const val NAV_CONTINUE_WATCHING_TO_SINGLE = 3

        const val LIST_NEW_MOVIES = 0
        const val LIST_TOP_MOVIES = 1
        const val LIST_TOP_TV_SHOWS = 2
        const val LIST_SINGLE_STUDIO = 3
        const val LIST_SINGLE_GENRE = 4

        const val NOT_FOUND_ERROR = "404 ვერ მოიძებნა"
        const val SERVER_ERROR = "500 სერვერთან დაკავშირება ვერ მოხერხდა"
        const val UNKNOWN_ERROR = "410 გაურკვეველი პრობლემა"

        const val VIDEO_PLAYER_DATA = "video player data"

        const val WATCHLIST_MOVIES = "movie"
        const val WATCHLIST_TV_SHOWS = "series"

        const val TITLE_ID = "title id"
        const val IS_TV_SHOW = "is tv Show"
        const val CONTINUE_WATCHING_NOW = "continue watching now"
        const val CATALOGUE_TYPE = "catalogue type"
        const val FROM_WATCHLIST = "from watchlist"
    }
}