package com.lukakordzaia.streamflow.helpers

import com.lukakordzaia.streamflow.repository.cataloguerepository.CatalogueRepository
import com.lukakordzaia.streamflow.repository.databaserepository.DatabaseRepository
import com.lukakordzaia.streamflow.repository.favoritesrepository.FavoritesRepository
import com.lukakordzaia.streamflow.repository.homerepistory.HomeRepository
import com.lukakordzaia.streamflow.repository.searchrepository.SearchRepository
import com.lukakordzaia.streamflow.repository.singletitlerepository.SingleTitleRepository
import com.lukakordzaia.streamflow.repository.traktrepository.TraktRepository

class Environment(
    val homeRepository: HomeRepository,
    val singleTitleRepository: SingleTitleRepository,
    val catalogueRepository: CatalogueRepository,
    val favoritesRepository: FavoritesRepository,
    val searchRepository: SearchRepository,
    val traktRepository: TraktRepository,
    val databaseRepository: DatabaseRepository
)