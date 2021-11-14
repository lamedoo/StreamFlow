package com.lukakordzaia.streamflowphone.di

import com.lukakordzaia.core.viewModels.VideoPlayerViewModel
import com.lukakordzaia.streamflowphone.ui.phone.catalogue.CatalogueViewModel
import com.lukakordzaia.streamflowphone.ui.phone.catalogue.cataloguedetails.SingleCatalogueViewModel
import com.lukakordzaia.streamflowphone.ui.phone.home.HomeViewModel
import com.lukakordzaia.streamflowphone.ui.phone.home.toplistfragment.TopListViewModel
import com.lukakordzaia.streamflowphone.ui.phone.phonesingletitle.PhoneSingleTitleViewModel
import com.lukakordzaia.streamflowphone.ui.phone.phonesingletitle.tvshowdetailsbottomsheet.TvShowBottomSheetViewModel
import com.lukakordzaia.streamflowphone.ui.phone.phonewatchlist.WatchlistViewModel
import com.lukakordzaia.streamflowphone.ui.phone.profile.ProfileViewModel
import com.lukakordzaia.streamflowphone.ui.phone.searchtitles.SearchTitlesViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val phoneViewModelModule = module {
    viewModel { HomeViewModel() }
    viewModel { PhoneSingleTitleViewModel() }
    viewModel { CatalogueViewModel() }
    viewModel { SingleCatalogueViewModel() }
    viewModel { SearchTitlesViewModel() }
    viewModel { TvShowBottomSheetViewModel() }
    viewModel { ProfileViewModel() }
    viewModel { WatchlistViewModel() }
    viewModel { TopListViewModel() }
    viewModel { VideoPlayerViewModel() }
}