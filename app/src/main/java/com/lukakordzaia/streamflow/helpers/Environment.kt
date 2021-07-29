package com.lukakordzaia.streamflow.helpers

import com.lukakordzaia.streamflow.repository.*

class Environment(
    val homeRepository: HomeRepository,
    val singleTitleRepository: SingleTitleRepository,
    val categoriesRepository: CategoriesRepository,
    val favoritesRepository: FavoritesRepository,
    val searchTitleRepository: SearchTitleRepository,
    val traktRepository: TraktRepository,
    val databaseRepository: DatabaseRepository
)