package com.lukakordzaia.imoviesapp.ui.singlemovie

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.lukakordzaia.imoviesapp.R
import com.lukakordzaia.imoviesapp.ui.MainActivity
import com.lukakordzaia.imoviesapp.utils.EventObserver
import com.lukakordzaia.imoviesapp.utils.navController
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_single_movie.*

class SingleMovieFragment : Fragment(R.layout.fragment_single_movie) {
    private lateinit var viewModel: SingleMovieViewModel
    private val args: SingleMovieFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(SingleMovieViewModel::class.java)
        viewModel.getSingleMovieFiles(args.movieId)
        Log.d("singlemovie", args.movieId.toString())

        viewModel.singleMovieFiles.observe(viewLifecycleOwner, Observer {
            tv_single_movie_title_geo.text = it.primaryName
            tv_single_movie_title_eng.text = it.secondaryName
            if (it.rating.imdb.score != null) {
                tv_single_movie_imdb_score.text = it.rating.imdb.score.toString()
            } else {
                tv_single_movie_imdb_score.text = "N/A"
            }
            if (!it.covers.data.x1050.isNullOrEmpty()) {
                Picasso.get().load(it.covers.data.x1050).into(iv_single_movie_play)
            }
            tv_single_movie_desc.text = "${it.plot?.data?.description?.substring(0, 100)}..."

            tv_single_movie_year.text = it.year.toString()
            tv_single_movie_duration.text = "${it.duration} წ."
            tv_single_movie_country.text = it.countries?.data?.get(0)?.secondaryName
        })

        iv_post_video_icon.setOnClickListener {
            viewModel.onPlayPressed(args.movieId)
        }

        viewModel.navigateScreen.observe(viewLifecycleOwner, EventObserver {
            navController(it)
        })

    }
}