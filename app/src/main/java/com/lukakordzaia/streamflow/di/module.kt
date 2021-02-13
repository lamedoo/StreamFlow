package com.lukakordzaia.streamflow.di

import com.lukakordzaia.streamflow.helpers.SpinnerClass
import com.lukakordzaia.streamflow.network.NetworkConnectionInterceptor
import com.lukakordzaia.streamflow.network.RetrofitBuilder
import com.lukakordzaia.streamflow.repository.*
import com.lukakordzaia.streamflow.ui.phone.categories.CategoriesViewModel
import com.lukakordzaia.streamflow.ui.phone.categories.singlegenre.SingleCategoryViewModel
import com.lukakordzaia.streamflow.ui.phone.home.HomeViewModel
import com.lukakordzaia.streamflow.ui.phone.searchtitles.SearchTitlesViewModel
import com.lukakordzaia.streamflow.ui.phone.settings.SettingsViewModel
import com.lukakordzaia.streamflow.ui.phone.singletitle.SingleTitleViewModel
import com.lukakordzaia.streamflow.ui.phone.singletitle.choosetitledetails.ChooseTitleDetailsViewModel
import com.lukakordzaia.streamflow.ui.phone.videoplayer.VideoPlayerViewModel
import com.lukakordzaia.streamflow.ui.tv.categories.TvCategoriesViewModel
import com.lukakordzaia.streamflow.ui.tv.details.titledetails.TvDetailsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { HomeViewModel(get()) }
    viewModel { SingleTitleViewModel(get()) }
    viewModel { VideoPlayerViewModel(get()) }
    viewModel { CategoriesViewModel(get()) }
    viewModel { SingleCategoryViewModel(get()) }
    viewModel { SearchTitlesViewModel(get()) }
    viewModel { TvDetailsViewModel(get()) }
    viewModel { TvCategoriesViewModel(get()) }
    viewModel { ChooseTitleDetailsViewModel(get()) }
    viewModel { SettingsViewModel() }
}

val repositoryModule = module {
    single { HomeRepository(get()) }
    single { SingleTitleRepository(get()) }
    single { CategoriesRepository(get()) }
    single { SearchTitleRepository(get()) }
    single { TvDetailsRepository(get()) }
}

val generalModule = module {
    single { RetrofitBuilder(get()) }
    single { NetworkConnectionInterceptor(get()) }
    single { SpinnerClass(get()) }
}