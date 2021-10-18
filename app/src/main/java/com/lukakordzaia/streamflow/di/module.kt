package com.lukakordzaia.streamflow.di

import com.lukakordzaia.streamflow.animations.TvSidebarAnimations
import com.lukakordzaia.streamflow.database.StreamFlowDatabase
import com.lukakordzaia.streamflow.helpers.Environment
import com.lukakordzaia.streamflow.helpers.videoplayer.BuildMediaSource
import com.lukakordzaia.streamflow.network.RetrofitBuilder
import com.lukakordzaia.streamflow.network.imovies.ImoviesNetwork
import com.lukakordzaia.streamflow.network.interceptors.DefaultHeaderInterceptor
import com.lukakordzaia.streamflow.network.interceptors.NetworkConnectionInterceptor
import com.lukakordzaia.streamflow.network.trakttv.TraktTvNetwork
import com.lukakordzaia.streamflow.repository.cataloguerepository.CatalogueRepository
import com.lukakordzaia.streamflow.repository.cataloguerepository.DefaultCatalogueRepository
import com.lukakordzaia.streamflow.repository.databaserepository.DatabaseRepository
import com.lukakordzaia.streamflow.repository.databaserepository.DefaultDatabaseRepository
import com.lukakordzaia.streamflow.repository.homerepistory.DefaultHomeRepository
import com.lukakordzaia.streamflow.repository.homerepistory.HomeRepository
import com.lukakordzaia.streamflow.repository.searchrepository.DefaultSearchRepository
import com.lukakordzaia.streamflow.repository.searchrepository.SearchRepository
import com.lukakordzaia.streamflow.repository.singletitlerepository.DefaultSingleTitleRepository
import com.lukakordzaia.streamflow.repository.singletitlerepository.SingleTitleRepository
import com.lukakordzaia.streamflow.repository.traktrepository.DefaultTraktRepository
import com.lukakordzaia.streamflow.repository.traktrepository.TraktRepository
import com.lukakordzaia.streamflow.repository.userrepository.DefaultUserRepository
import com.lukakordzaia.streamflow.repository.userrepository.UserRepository
import com.lukakordzaia.streamflow.repository.watchlistrepository.DefaultWatchlistRepository
import com.lukakordzaia.streamflow.repository.watchlistrepository.WatchlistRepository
import com.lukakordzaia.streamflow.sharedpreferences.SharedPreferences
import com.lukakordzaia.streamflow.ui.phone.catalogue.CatalogueViewModel
import com.lukakordzaia.streamflow.ui.phone.catalogue.cataloguedetails.SingleCatalogueViewModel
import com.lukakordzaia.streamflow.ui.phone.home.HomeViewModel
import com.lukakordzaia.streamflow.ui.phone.home.toplistfragment.TopListViewModel
import com.lukakordzaia.streamflow.ui.phone.phonesingletitle.PhoneSingleTitleViewModel
import com.lukakordzaia.streamflow.ui.phone.phonesingletitle.tvshowdetailsbottomsheet.TvShowBottomSheetViewModel
import com.lukakordzaia.streamflow.ui.phone.profile.ProfileViewModel
import com.lukakordzaia.streamflow.ui.phone.searchtitles.SearchTitlesViewModel
import com.lukakordzaia.streamflow.ui.shared.VideoPlayerViewModel
import com.lukakordzaia.streamflow.ui.shared.WatchlistViewModel
import com.lukakordzaia.streamflow.ui.tv.tvcatalogue.TvCatalogueViewModel
import com.lukakordzaia.streamflow.ui.tv.tvsingletitle.tvtitledetails.TvTitleDetailsViewModel
import com.lukakordzaia.streamflow.ui.tv.tvsingletitle.tvtitlefiles.TvTitleFilesViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { HomeViewModel() }
    viewModel { PhoneSingleTitleViewModel() }
    viewModel { CatalogueViewModel() }
    viewModel { SingleCatalogueViewModel() }
    viewModel { SearchTitlesViewModel() }
    viewModel { TvTitleDetailsViewModel() }
    viewModel { TvCatalogueViewModel() }
    viewModel { TvShowBottomSheetViewModel() }
    viewModel { ProfileViewModel() }
    viewModel { TvTitleFilesViewModel() }
    viewModel { WatchlistViewModel() }
    viewModel { TopListViewModel() }
    viewModel { VideoPlayerViewModel() }
}

val repositoryModule = module {
    single<HomeRepository> { DefaultHomeRepository(get()) }
    single<SingleTitleRepository> { DefaultSingleTitleRepository(get()) }
    single<CatalogueRepository> { DefaultCatalogueRepository(get()) }
    single<SearchRepository> { DefaultSearchRepository(get()) }
    single<TraktRepository> { DefaultTraktRepository(get()) }
    single<WatchlistRepository> { DefaultWatchlistRepository(get()) }
    single<DatabaseRepository> { DefaultDatabaseRepository(get()) }
    single<UserRepository> { DefaultUserRepository(get()) }
}

val generalModule = module {
    single { NetworkConnectionInterceptor(get()) }
    single { DefaultHeaderInterceptor(get()) }
    single { RetrofitBuilder(get(), get()) }
    single { get<RetrofitBuilder>().getRetrofitInstance().create(ImoviesNetwork::class.java) }
    single { get<RetrofitBuilder>().getRetrofitInstance().create(TraktTvNetwork::class.java) }
    single { BuildMediaSource(get()) }
    single { SharedPreferences(get()) }
    single { TvSidebarAnimations() }
    single { StreamFlowDatabase.getDatabase(get()) }
    single { Environment(get(), get(), get(), get(), get(), get(), get(), get()) }
}