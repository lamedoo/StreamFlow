package com.lukakordzaia.streamflowphone.di

import com.lukakordzaia.core.videoplayer.VideoPlayerViewModel
import com.lukakordzaia.streamflowphone.helpers.NavigationHelper
import com.lukakordzaia.streamflowphone.ui.catalogue.CatalogueViewModel
import com.lukakordzaia.streamflowphone.ui.catalogue.cataloguedetails.SingleCatalogueViewModel
import com.lukakordzaia.streamflowphone.ui.home.HomeViewModel
import com.lukakordzaia.streamflowphone.ui.home.toplistfragment.TopListViewModel
import com.lukakordzaia.streamflowphone.ui.phonesingletitle.PhoneSingleTitleViewModel
import com.lukakordzaia.streamflowphone.ui.phonesingletitle.tvepisodesbottomsheet.TvEpisodeBottomSheetViewModel
import com.lukakordzaia.streamflowphone.ui.phonewatchlist.WatchlistViewModel
import com.lukakordzaia.streamflowphone.ui.profile.ProfileViewModel
import com.lukakordzaia.streamflowphone.ui.searchtitles.SearchTitlesViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val phoneViewModelModule = module {
    viewModel { HomeViewModel(get(), get()) }
    viewModel { PhoneSingleTitleViewModel() }
    viewModel { CatalogueViewModel() }
    viewModel { SingleCatalogueViewModel() }
    viewModel { SearchTitlesViewModel() }
    viewModel { TvEpisodeBottomSheetViewModel() }
    viewModel { ProfileViewModel() }
    viewModel { WatchlistViewModel() }
    viewModel { TopListViewModel() }
    viewModel { VideoPlayerViewModel() }
}

val phoneGeneralModule = module {
    single { NavigationHelper() }
}