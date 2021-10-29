package com.lukakordzaia.streamflow.network

class EndPoints {
    companion object {

        // IMOVIES
        const val BASE_URL = "https://api.imovies.cc/api/v1/"

        const val MOVIE_OF_THE_DAY = "movies/movie-day?page=1&per_page=1"
        const val NEW_MOVIES = "movies?filters%5Bwith_files%5D=yes&filters%5Btype%5D=movie&sort=-upload_date&per_page=55"
        const val TOP_MOVIES = "movies/top?type=movie&period=day&page=1&per_page=55"
        const val TOP_TV_SHOWS = "movies/top?type=series&period=day&per_page=55"
        const val NEW_SERIES = "movies/latest-episodes"

        const val TOP_TRAILERS = "trailers/trailer-day?page=1&per_page=5"
        const val GENRES = "genres?page=1&per_page=100"
        const val SINGLE_GENRE = "movies?filters%5Bwith_files%5D=yes&per_page=55&sort=-year"
        const val TOP_STUDIOS = "studios?page=1&per_page=15&sort=-rating"
        const val SINGLE_STUDIOS = "movies?filters%5Bwith_files%5D=yes&per_page=55&sort=-year"

        const val SEARCH_TITLES = "search-advanced?filters%5Btype%5D=movie&per_page=25"
        const val TOP_FRANCHISES = "franchises?page=1&per_page=12&sort=rand&filters%5Bfeatured%5D=yes"

        const val SINGLE_TITLE = "movies/{id}/"
        const val SINGLE_TITLE_CAST = "movies/{id}/persons"
        const val SINGLE_TITLE_RELATED = "movies/{id}/related?page=1&per_page=10"
        const val SINGLE_TITLE_FILES = "movies/{id}/season-files/{season_number}"

        const val USER_LOGIN = "auth/token"
        const val USER_DATA = "auth/user"
        const val USER_LOG_OUT = "auth/revoke-token"
        const val USER_WATCHLIST = "user/wantstowatch?per_page=50&filters%5Bgenres_related%5D=no&filters%5Bcountries_related%5D=no&filters%5Bwithout_watched_movies%5D=no&sort=last_added"
        const val USER_WATCHLIST_STATUS = "movies/{id}/wantstowatch"
        const val USER_IS_WATCHING = "user/watches/{id}/{season}/{episode}"
        const val USER_CONTINUE_WATCHING = "user/watches-continue?page=1&per_page=75&filters%5Bwithout_watched_movies%5D=yes"
        const val HIDE_CONTINUE_WATCHING = "user/watches/{id}/hide"
        const val USER_SUGGESTIONS = "user/movies/suggestions?per_page=55"
    }
}