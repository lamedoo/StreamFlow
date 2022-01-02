package com.lukakordzaia.core.di

import com.lukakordzaia.core.database.StreamFlowDatabase
import com.lukakordzaia.core.network.Environment
import com.lukakordzaia.core.network.RetrofitBuilder
import com.lukakordzaia.core.network.imovies.ImoviesNetwork
import com.lukakordzaia.core.network.interceptors.DefaultHeaderInterceptor
import com.lukakordzaia.core.network.interceptors.NetworkConnectionInterceptor
import com.lukakordzaia.core.repository.cataloguerepository.CatalogueRepository
import com.lukakordzaia.core.repository.cataloguerepository.DefaultCatalogueRepository
import com.lukakordzaia.core.repository.databaserepository.DatabaseRepository
import com.lukakordzaia.core.repository.databaserepository.DefaultDatabaseRepository
import com.lukakordzaia.core.repository.homerepistory.DefaultHomeRepository
import com.lukakordzaia.core.repository.homerepistory.HomeRepository
import com.lukakordzaia.core.repository.searchrepository.DefaultSearchRepository
import com.lukakordzaia.core.repository.searchrepository.SearchRepository
import com.lukakordzaia.core.repository.singletitlerepository.DefaultSingleTitleRepository
import com.lukakordzaia.core.repository.singletitlerepository.SingleTitleRepository
import com.lukakordzaia.core.repository.userrepository.DefaultUserRepository
import com.lukakordzaia.core.repository.userrepository.UserRepository
import com.lukakordzaia.core.repository.watchlistrepository.DefaultWatchlistRepository
import com.lukakordzaia.core.repository.watchlistrepository.WatchlistRepository
import com.lukakordzaia.core.sharedpreferences.SharedPreferences
import com.lukakordzaia.core.utils.DialogUtils
import com.lukakordzaia.core.videoplayer.BuildMediaSource
import org.koin.dsl.module

val repositoryModule = module {
    single<HomeRepository> { DefaultHomeRepository(get()) }
    single<SingleTitleRepository> { DefaultSingleTitleRepository(get()) }
    single<CatalogueRepository> { DefaultCatalogueRepository(get()) }
    single<SearchRepository> { DefaultSearchRepository(get()) }
    single<WatchlistRepository> { DefaultWatchlistRepository(get()) }
    single<DatabaseRepository> { DefaultDatabaseRepository(get()) }
    single<UserRepository> { DefaultUserRepository(get()) }
}

val generalModule = module {
    single { NetworkConnectionInterceptor(get()) }
    single { DefaultHeaderInterceptor(get()) }
    single { RetrofitBuilder(get(), get()) }
    single { get<RetrofitBuilder>().getRetrofitInstance().create(ImoviesNetwork::class.java) }
    single { BuildMediaSource() }
    single { SharedPreferences(get()) }
    single { StreamFlowDatabase.getDatabase(get()) }
    single { Environment(get(), get(), get(), get(), get(), get(), get()) }
}