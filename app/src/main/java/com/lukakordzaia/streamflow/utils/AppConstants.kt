package com.lukakordzaia.streamflow.utils

class AppConstants {
    companion object {
        const val TRAKTV_CLIENT_ID = "3149f4d57d07efed5a51638879923b574d3487e4e8159758835450620aaaed24"
        const val TRAKTV_CLIENT_SECRET = "6bbe62ce5aac07b6c2c00f1b290a0d5407fd444bc03e00b6f00240112179fab6"

        const val TV_CONTINUE_WATCHING = "განაგრძეთ ყურება"
        const val TV_TOP_MOVIES = "ტოპ ფილები"
        const val TV_NEW_MOVIES = "ახალი დამატებული"
        const val TV_TOP_TV_SHOWS = "ტოპ სერიალები"
        const val TV_GENRES = "კატეგორიები"
        const val TV_SETTINGS = "პარამეტრები"

        const val NAV_HOME_TO_SINGLE = 0
        const val NAV_TOP_MOVIES_TO_SINGLE = 1
        const val NAV_TOP_TV_SHOWS_TO_SINGLE = 2
        const val NAV_CONTINUE_WATCHING_TO_SINGLE = 3
        const val NAV_NEW_MOVIES_TO_SINGLE = 4

        const val NAV_GENRE_TO_SINGLE = 0
        const val NAV_STUDIO_TO_SINGLE = 1

        const val GENRE_ANIMATION = "ანიმაცია"
        const val GENRE_COMEDY = "კომედია"
        const val GENRE_MELODRAMA = "მელოდრამა"
        const val GENRE_HORROR = "საშინელებათა"
        const val GENRE_ADVENTURE = "სათავგადასავლო"
        const val GENRE_ACTION = "მძაფრ-სიუჟეტიანი"

        const val TV_CATEGORY_NEW_MOVIES = 0
        const val TV_CATEGORY_TOP_MOVIES = 1
        const val TV_CATEGORY_TOP_TV_SHOWS = 2

        const val NOT_FOUND_ERROR = "404 ვერ მოიძებნა"
        const val SERVER_ERROR = "500 სერვერთან დაკავშირება ვერ მოხერხდა"
        const val UNKNOWN_ERROR = "410 გაურკვეველი პრობლემა"

        const val TRAKT_PENDING_AUTH = "400 ავტორიზაცია არ მომხდარა"
        const val TRAKT_NOT_FOUND = "404 არასწორი კოდი"
        const val TRAKT_CODE_USED = "409 კოდი უკვე გამოყენებულია"
        const val TRAKT_CODE_EXPIRED = "410 კოდის გამოყენების დრო ამოიწურა"
        const val TRAKT_UNKNOWN_ERROR = "გაუკრვეველი პრობლემა"

        const val VIDEO_PLAYER_DATA = "video player data"

        const val NO_INTERNET = "შეამოწმეთ ინტერნეტთან კავშირი"

        const val WATCHLIST_MOVIES = "movie"
        const val WATCHLIST_TV_SHOWS = "series"
    }
}