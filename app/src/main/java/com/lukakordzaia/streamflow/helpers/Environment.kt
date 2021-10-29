package com.lukakordzaia.streamflow.helpers

import com.lukakordzaia.streamflow.repository.cataloguerepository.CatalogueRepository
import com.lukakordzaia.streamflow.repository.databaserepository.DatabaseRepository
import com.lukakordzaia.streamflow.repository.homerepistory.HomeRepository
import com.lukakordzaia.streamflow.repository.searchrepository.SearchRepository
import com.lukakordzaia.streamflow.repository.singletitlerepository.SingleTitleRepository
import com.lukakordzaia.streamflow.repository.userrepository.UserRepository
import com.lukakordzaia.streamflow.repository.watchlistrepository.WatchlistRepository

class Environment(
    val homeRepository: HomeRepository,
    val singleTitleRepository: SingleTitleRepository,
    val catalogueRepository: CatalogueRepository,
    val watchlistRepository: WatchlistRepository,
    val searchRepository: SearchRepository,
    val databaseRepository: DatabaseRepository,
    val userRepository: UserRepository
)