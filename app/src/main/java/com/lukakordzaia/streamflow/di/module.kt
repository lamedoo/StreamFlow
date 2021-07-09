package com.lukakordzaia.streamflow.di

import com.lukakordzaia.streamflow.helpers.SpinnerClass
import com.lukakordzaia.streamflow.helpers.videoplayer.BuildMediaSource
import com.lukakordzaia.streamflow.network.RetrofitBuilder
import com.lukakordzaia.streamflow.network.imovies.ImoviesNetwork
import com.lukakordzaia.streamflow.network.interceptors.DefaultHeaderInterceptor
import com.lukakordzaia.streamflow.network.interceptors.NetworkConnectionInterceptor
import com.lukakordzaia.streamflow.network.trakttv.TraktTvNetwork
import com.lukakordzaia.streamflow.repository.*
import com.lukakordzaia.streamflow.sharedpreferences.AuthSharedPreferences
import com.lukakordzaia.streamflow.ui.phone.categories.CategoriesViewModel
import com.lukakordzaia.streamflow.ui.phone.categories.SingleCategoryViewModel
import com.lukakordzaia.streamflow.ui.phone.favorites.FavoritesViewModel
import com.lukakordzaia.streamflow.ui.phone.home.HomeViewModel
import com.lukakordzaia.streamflow.ui.phone.home.toplistfragments.SingleTopListViewModel
import com.lukakordzaia.streamflow.ui.phone.profile.ProfileViewModel
import com.lukakordzaia.streamflow.ui.phone.searchtitles.SearchTitlesViewModel
import com.lukakordzaia.streamflow.ui.phone.singletitle.SingleTitleViewModel
import com.lukakordzaia.streamflow.ui.phone.singletitle.choosetitledetails.ChooseTitleDetailsViewModel
import com.lukakordzaia.streamflow.ui.shared.VideoPlayerViewModel
import com.lukakordzaia.streamflow.ui.tv.categories.TvCategoriesViewModel
import com.lukakordzaia.streamflow.ui.tv.details.titledetails.TvDetailsViewModel
import com.lukakordzaia.streamflow.ui.tv.details.titlefiles.TvTitleFilesViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { HomeViewModel(get()) }
    viewModel { SingleTitleViewModel(get(), get()) }
    viewModel { CategoriesViewModel(get()) }
    viewModel { SingleCategoryViewModel(get()) }
    viewModel { SearchTitlesViewModel(get()) }
    viewModel { TvDetailsViewModel(get()) }
    viewModel { TvCategoriesViewModel(get()) }
    viewModel { ChooseTitleDetailsViewModel(get()) }
    viewModel { ProfileViewModel(get(), get()) }
    viewModel { TvTitleFilesViewModel(get()) }
    viewModel { FavoritesViewModel(get(), get()) }
    viewModel { SingleTopListViewModel(get()) }
    viewModel { VideoPlayerViewModel(get()) }
}

val repositoryModule = module {
    single { HomeRepository(get()) }
    single { SingleTitleRepository(get()) }
    single { CategoriesRepository(get()) }
    single { SearchTitleRepository(get()) }
    single { TvDetailsRepository(get()) }
    single { TraktRepository(get()) }
    single { FavoritesRepository(get()) }
    single { ProfileRepository() }
}

val generalModule = module {
    single { NetworkConnectionInterceptor(get()) }
    single { DefaultHeaderInterceptor() }
    single { RetrofitBuilder(get(), get()) }
    single { get<RetrofitBuilder>().getRetrofitInstance().create(ImoviesNetwork::class.java) }
    single { get<RetrofitBuilder>().getRetrofitInstance().create(TraktTvNetwork::class.java) }
    single { SpinnerClass(get()) }
    single { BuildMediaSource(get()) }
    single { AuthSharedPreferences(get()) }
}