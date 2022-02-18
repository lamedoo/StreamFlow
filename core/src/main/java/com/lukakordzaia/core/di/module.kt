package com.lukakordzaia.core.di

import com.lukakordzaia.core.database.StreamFlowDatabase
import com.lukakordzaia.core.network.Environment
import com.lukakordzaia.core.network.RetrofitBuilder
import com.lukakordzaia.core.network.imovies.ImoviesNetwork
import com.lukakordzaia.core.network.interceptors.DefaultHeaderInterceptor
import com.lukakordzaia.core.network.interceptors.NetworkConnectionInterceptor
import com.lukakordzaia.core.domain.repository.cataloguerepository.CatalogueRepository
import com.lukakordzaia.core.domain.repository.cataloguerepository.DefaultCatalogueRepository
import com.lukakordzaia.core.domain.repository.databaserepository.DatabaseRepository
import com.lukakordzaia.core.domain.repository.databaserepository.DefaultDatabaseRepository
import com.lukakordzaia.core.domain.repository.homerepistory.DefaultHomeRepository
import com.lukakordzaia.core.domain.repository.homerepistory.HomeRepository
import com.lukakordzaia.core.domain.repository.searchrepository.DefaultSearchRepository
import com.lukakordzaia.core.domain.repository.searchrepository.SearchRepository
import com.lukakordzaia.core.domain.repository.singletitlerepository.DefaultSingleTitleRepository
import com.lukakordzaia.core.domain.repository.singletitlerepository.SingleTitleRepository
import com.lukakordzaia.core.domain.repository.userrepository.DefaultUserRepository
import com.lukakordzaia.core.domain.repository.userrepository.UserRepository
import com.lukakordzaia.core.domain.repository.watchlistrepository.DefaultWatchlistRepository
import com.lukakordzaia.core.domain.repository.watchlistrepository.WatchlistRepository
import com.lukakordzaia.core.domain.usecases.*
import com.lukakordzaia.core.sharedpreferences.SharedPreferences
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

val useCaseModule = module {
    single { MovieDayUseCaseBase(get()) }
    single { NewMoviesUseCase(get()) }
    single { TopMoviesUseCase(get()) }
    single { TopTvShowsUseCase(get()) }
    single { NewSeriesUseCase(get()) }
    single { UserSuggestionsUseCase(get()) }
    single { ContinueWatchingUseCase(get()) }
    single { SingleTitleUseCase(get()) }
    single { AllGenresUseCase(get()) }
    single { TopStudiosUseCase(get()) }
    single { TopTrailersUseCase(get()) }
    single { SingleGenreUseCase(get()) }
    single { SingleStudioUseCase(get()) }
    single { WatchlistUseCase(get()) }
    single { DeleteWatchlistUseCase(get()) }
    single { AddWatchlistUseCase(get()) }
    single { UserLoginUseCase(get()) }
    single { UserLogOutUseCase(get()) }
    single { UserDataUseCase(get()) }
    single { SearchTitleUseCase(get()) }
    single { TopFranchisesUseCase(get()) }
    single { SingleTitleCastUseCase(get()) }
    single { SingleTitleRelatedUseCase(get()) }
    single { DbSingleContinueWatchingUseCase(get()) }
    single { SingleTitleFilesUseCase(get()) }
}