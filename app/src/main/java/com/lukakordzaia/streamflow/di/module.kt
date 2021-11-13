package com.lukakordzaia.streamflow.di

import com.lukakordzaia.core.viewModels.VideoPlayerViewModel
import com.lukakordzaia.streamflow.ui.phone.catalogue.CatalogueViewModel
import com.lukakordzaia.streamflow.ui.phone.catalogue.cataloguedetails.SingleCatalogueViewModel
import com.lukakordzaia.streamflow.ui.phone.home.HomeViewModel
import com.lukakordzaia.streamflow.ui.phone.home.toplistfragment.TopListViewModel
import com.lukakordzaia.streamflow.ui.phone.phonesingletitle.PhoneSingleTitleViewModel
import com.lukakordzaia.streamflow.ui.phone.phonesingletitle.tvshowdetailsbottomsheet.TvShowBottomSheetViewModel
import com.lukakordzaia.streamflow.ui.phone.phonewatchlist.WatchlistViewModel
import com.lukakordzaia.streamflow.ui.phone.profile.ProfileViewModel
import com.lukakordzaia.streamflow.ui.phone.searchtitles.SearchTitlesViewModel
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