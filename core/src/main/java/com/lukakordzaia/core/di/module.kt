package com.lukakordzaia.core.di

import com.lukakordzaia.core.database.StreamFlowDatabase
import com.lukakordzaia.core.network.imovies.RetrofitImoviesBuilder
import com.lukakordzaia.core.network.imovies.ImoviesNetwork
import com.lukakordzaia.core.network.interceptors.DefaultImoviesHeaderInterceptor
import com.lukakordzaia.core.network.interceptors.NetworkConnectionInterceptor
import com.lukakordzaia.core.domain.repository.cataloguerepository.CatalogueRepository
import com.lukakordzaia.core.domain.repository.cataloguerepository.DefaultCatalogueRepository
import com.lukakordzaia.core.domain.repository.databaserepository.DatabaseRepository
import com.lukakordzaia.core.domain.repository.databaserepository.DefaultDatabaseRepository
import com.lukakordzaia.core.domain.repository.homerepistory.DefaultHomeRepository
import com.lukakordzaia.core.domain.repository.homerepistory.HomeRepository
import com.lukakordzaia.core.domain.repository.releaserepository.DefaultReleaseRepository
import com.lukakordzaia.core.domain.repository.releaserepository.ReleaseRepository
import com.lukakordzaia.core.domain.repository.searchrepository.DefaultSearchRepository
import com.lukakordzaia.core.domain.repository.searchrepository.SearchRepository
import com.lukakordzaia.core.domain.repository.singletitlerepository.DefaultSingleTitleRepository
import com.lukakordzaia.core.domain.repository.singletitlerepository.SingleTitleRepository
import com.lukakordzaia.core.domain.repository.userrepository.DefaultUserRepository
import com.lukakordzaia.core.domain.repository.userrepository.UserRepository
import com.lukakordzaia.core.domain.repository.watchlistrepository.DefaultWatchlistRepository
import com.lukakordzaia.core.domain.repository.watchlistrepository.WatchlistRepository
import com.lukakordzaia.core.domain.usecases.*
import com.lukakordzaia.core.network.github.GithubNetwork
import com.lukakordzaia.core.network.github.RetrofitGithubBuilder
import com.lukakordzaia.core.network.interceptors.DefaultGithubHeaderInterceptor
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
    single<ReleaseRepository> { DefaultReleaseRepository(get()) }
}

val generalModule = module {
    single { NetworkConnectionInterceptor(get()) }
    single { DefaultImoviesHeaderInterceptor(get()) }
    single { DefaultGithubHeaderInterceptor() }
    single { RetrofitImoviesBuilder(get(), get()) }
    single { RetrofitGithubBuilder(get(), get()) }
    single { get<RetrofitImoviesBuilder>().getRetrofitInstance().create(ImoviesNetwork::class.java) }
    single { get<RetrofitGithubBuilder>().getRetrofitInstance().create(GithubNetwork::class.java) }
    single { BuildMediaSource() }
    single { SharedPreferences(get()) }
    single { StreamFlowDatabase.getDatabase(get()) }
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
    single { DbDeleteSingleContinueWatchingUseCase(get()) }
    single { HideContinueWatchingUseCase(get()) }
    single { SingleTitleFilesVideoUseCase(get()) }
    single { DbInsertContinueWatchingUseCase(get()) }
    single { TitleWatchTimeUseCase(get()) }
    single { DbAllContinueWatchingUseCase(get(), get()) }
    single { DbDeleteAllContinueWatchingUseCase(get()) }
    single { GithubReleasesUseCase(get()) }
}