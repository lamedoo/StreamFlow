package com.lukakordzaia.streamflow.ui.tv.genres

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.FragmentActivity
import com.lukakordzaia.streamflow.R
import com.lukakordzaia.streamflow.ui.tv.details.titledetails.TvDetailsViewModel
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_tv_single_genre.*
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

        tvDetailsViewModel.singleTitleData.observe(this, {
            single_genre_top_name.text = it.secondaryName
            Picasso.get().load(it.covers?.data?.x1050).error(R.drawable.movie_image_placeholder_landscape).into(single_genre_top_poster)
            if (it.plot.data.description.isNotEmpty()) {
                single_genre_top_plot.text = it.plot.data.description
            } else {
                single_genre_top_plot.text = "აღწერა არ მოიძებნა"
            }
        })
    }

    override fun getTitleId(titleId: Int) {
        Log.d("singletitleid", titleId.toString())
        tvDetailsViewModel.getSingleTitleData(titleId)
    }
}