package com.lukakordzaia.core.network

import com.lukakordzaia.core.repository.cataloguerepository.CatalogueRepository
import com.lukakordzaia.core.repository.databaserepository.DatabaseRepository
import com.lukakordzaia.core.repository.homerepistory.HomeRepository
import com.lukakordzaia.core.repository.searchrepository.SearchRepository
import com.lukakordzaia.core.repository.singletitlerepository.SingleTitleRepository
import com.lukakordzaia.core.repository.userrepository.UserRepository
import com.lukakordzaia.core.repository.watchlistrepository.WatchlistRepository

class Environment(
    val homeRepository: HomeRepository,
    val singleTitleRepository: SingleTitleRepository,
    val catalogueRepository: CatalogueRepository,
    val watchlistRepository: WatchlistRepository,
    val searchRepository: SearchRepository,
    val databaseRepository: DatabaseRepository,
    val userRepository: UserRepository
)