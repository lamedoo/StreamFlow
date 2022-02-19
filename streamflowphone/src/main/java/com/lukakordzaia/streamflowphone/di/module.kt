package com.lukakordzaia.streamflowphone.di

import com.lukakordzaia.core.videoplayer.VideoPlayerViewModel
import com.lukakordzaia.streamflowphone.helpers.NavigationHelper
import com.lukakordzaia.streamflowphone.ui.catalogue.CatalogueViewModel
import com.lukakordzaia.streamflowphone.ui.catalogue.cataloguedetails.SingleCatalogueViewModel
import com.lukakordzaia.streamflowphone.ui.home.HomeViewModel
import com.lukakordzaia.streamflowphone.ui.home.toplistfragment.TopListViewModel
import com.lukakordzaia.streamflowphone.ui.phonesingletitle.PhoneSingleTitleViewModel
import com.lukakordzaia.streamflowphone.ui.phonesingletitle.episodesbottomsheet.EpisodeBottomSheetViewModel
import com.lukakordzaia.streamflowphone.ui.phonewatchlist.WatchlistViewModel
import com.lukakordzaia.streamflowphone.ui.profile.ProfileViewModel
import com.lukakordzaia.streamflowphone.ui.searchtitles.SearchTitlesViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val phoneViewModelModule = module {
    viewModel { HomeViewModel(get(), get(), get(), get(), get(), get(), get(), get(), get(), get()) }
    viewModel { PhoneSingleTitleViewModel(get(), get(), get(), get(), get(), get(), get()) }
    viewModel { CatalogueViewModel(get(), get(), get()) }
    viewModel { SingleCatalogueViewModel(get(), get()) }
    viewModel { SearchTitlesViewModel(get(), get()) }
    viewModel { EpisodeBottomSheetViewModel(get(), get(), get()) }
    viewModel { ProfileViewModel(get(), get(), get()) }
    viewModel { WatchlistViewModel(get(), get()) }
    viewModel { TopListViewModel(get(), get(), get()) }
    viewModel { VideoPlayerViewModel() }
}

val phoneGeneralModule = module {
    single { NavigationHelper() }
}