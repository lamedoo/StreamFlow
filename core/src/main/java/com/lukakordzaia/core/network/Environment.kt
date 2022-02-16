package com.lukakordzaia.core.network

import com.lukakordzaia.core.domain.repository.cataloguerepository.CatalogueRepository
import com.lukakordzaia.core.domain.repository.databaserepository.DatabaseRepository
import com.lukakordzaia.core.domain.repository.homerepistory.HomeRepository
import com.lukakordzaia.core.domain.repository.searchrepository.SearchRepository
import com.lukakordzaia.core.domain.repository.singletitlerepository.SingleTitleRepository
import com.lukakordzaia.core.domain.repository.userrepository.UserRepository
import com.lukakordzaia.core.domain.repository.watchlistrepository.WatchlistRepository

class Environment(
    val homeRepository: HomeRepository,
    val singleTitleRepository: SingleTitleRepository,
    val catalogueRepository: CatalogueRepository,
    val watchlistRepository: WatchlistRepository,
    val searchRepository: SearchRepository,
    val databaseRepository: DatabaseRepository,
    val userRepository: UserRepository
)