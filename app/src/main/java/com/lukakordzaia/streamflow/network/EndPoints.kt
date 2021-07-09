package com.lukakordzaia.streamflow.network

class EndPoints {
    companion object {

        // IMOVIES
        const val BASE_URL = "https://api.imovies.cc/api/v1/"

        const val MOVIE_OF_THE_DAY = "movies/movie-day?page=1&per_page=1"
        const val NEW_MOVIES = "movies?filters%5Bwith_files%5D=yes&filters%5Btype%5D=movie&sort=-upload_date&per_page=55"
        const val TOP_MOVIES = "movies/top?type=movie&period=day&page=1&per_page=55"
        const val TOP_TV_SHOWS = "movies/top?type=series&period=day&per_page=55"

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

        //TRAKT TV
        const val DEVICE_CODE = "https://api.trakt.tv/oauth/device/code"

        const val USER_TOKEN = "https://api.trakt.tv/oauth/device/token"
        const val USER_PROFILE = "https://api.trakt.tv/users/me"
        const val USER_LIST = "https://api.trakt.tv/users/me/lists/streamflow-list"
        const val USER_LIST_BY_TYPE = "https://api.trakt.tv/users/me/lists/streamflow-list/items/{type}"

        const val ADD_LIST = "https://api.trakt.tv/users/me/lists"
        const val ADD_MOVIE_TO_LIST = "https://api.trakt.tv/users/me/lists/streamflow-list/items"
        const val ADD_TV_SHOW_TO_LIST = "https://api.trakt.tv/users/me/lists/streamflow-list/items"
        const val REMOVE_MOVIE_FROM_LIST = "https://api.trakt.tv/users/me/lists/streamflow-list/items/remove"
        const val REMOVE_TV_SHOW_FROM_LIST = "https://api.trakt.tv/users/me/lists/streamflow-list/items/remove"
    }
}