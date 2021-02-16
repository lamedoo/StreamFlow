package com.lukakordzaia.streamflow.ui.tv.genres

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import com.lukakordzaia.streamflow.R
import com.lukakordzaia.streamflow.ui.tv.details.titledetails.TvDetailsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class TvSingleGenreActivity: FragmentActivity(), TvSingleGenreFragment.OnTitleSelected {
    private val tvDetailsViewModel: TvDetailsViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tv_single_genre)

        supportFragmentManager.beginTransaction()
                .add(R.id.tv_single_genre_fragment, TvSingleGenreFragment())
                .show(TvSingleGenreFragment())
                .commit()

        tvDetailsViewModel.singleTitleData.observe(lifecycleScope, )
    }

    override fun getTitleId(titleId: Int) {
        Log.d("singletitleid", titleId.toString())
        tvDetailsViewModel.getSingleTitleData(titleId)
    }
}