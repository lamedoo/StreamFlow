package com.lukakordzaia.imoviesapp.ui.phone.singletitle

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.google.android.material.appbar.AppBarLayout
import com.lukakordzaia.imoviesapp.R
import com.lukakordzaia.imoviesapp.animations.PlayButtonAnimations
import com.lukakordzaia.imoviesapp.datamodels.TitleDetails
import com.lukakordzaia.imoviesapp.utils.EventObserver
import com.lukakordzaia.imoviesapp.utils.navController
import com.lukakordzaia.imoviesapp.utils.setGone
import com.lukakordzaia.imoviesapp.utils.setVisible
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_single_title_new.*
import kotlin.math.abs

class SingleTitleFragment : Fragment(R.layout.fragment_single_title_new) {
    private lateinit var viewModel: SingleTitleViewModel
    private val args: SingleTitleFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(SingleTitleViewModel::class.java)
        viewModel.getSingleTitleData(args.titleId)

        viewModel.isLoading.observe(viewLifecycleOwner, EventObserver {
            if (!it) {
                single_title_progressBar.setGone()
                single_title_main_container.setVisible()
            }
        })

        viewModel.singleTitleData.observe(viewLifecycleOwner, Observer {
            tv_single_title_name_geo.text = it.primaryName
            tv_single_title_name_eng.text = it.secondaryName
            if (it.rating?.imdb?.score != null) {
                tv_single_movie_imdb_score.text = it.rating.imdb.score.toString()
            }
            if (!it.covers?.data?.x1050.isNullOrEmpty()) {
                Picasso.get().load(it.covers?.data?.x1050).into(iv_single_title_play)
            }

            if (it.plot.data != null) {
                if (!it.plot.data.description.isNullOrEmpty()) {
                    single_title_desc.text = it.plot.data.description
                } else {
                    single_title_desc.text = "აღწერა არ მოიძებნა"
                }
            } else {
                single_title_desc.text = "აღწერა არ მოიძებნა"
            }

            tv_single_title_year.text = it.year.toString()
            tv_single_title_duration.text = "${it.duration} წ."
            if (!it.countries.data.isNullOrEmpty()) {
                tv_single_title_country.text = it.countries.data[0].secondaryName
            }

        })

        viewModel.titleDetails.observe(viewLifecycleOwner, Observer {
            iv_post_video_icon.setOnClickListener { _ ->
                viewModel.onPlayPressed(args.titleId, TitleDetails(it.numOfSeasons, it.isTvShow))

                PlayButtonAnimations().rotatePlayButton(iv_post_video_icon, 1000)
            }
            single_title_play_collapsed.setOnClickListener { _ ->
                viewModel.onPlayPressed(args.titleId, TitleDetails(it.numOfSeasons, it.isTvShow))

                PlayButtonAnimations().rotatePlayButton(single_title_play_collapsed, 1000)
            }
        })

        viewModel.checkTitleInDb(requireContext(), args.titleId).observe(viewLifecycleOwner, {
            if (it) {
                single_title_delete_container.setOnClickListener {
                    val alertDialog = AlertDialog.Builder(requireContext())
                    alertDialog.setMessage("ნამდვილად გსურთ ისტორიიდან წაშლა?")
                            .setCancelable(false)
                            .setPositiveButton("დიახ") { _, _ ->
                                viewModel.deleteTitleFromDb(requireContext(), args.titleId)
                            }
                            .setNegativeButton("არა") { dialog, _ ->
                                dialog.dismiss()
                            }
                    val alert = alertDialog.create()
                    alert.show()
                }
            } else {
                single_title_delete_container.setGone()
            }
        })

        single_title_appbar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { _, verticalOffset ->
            if (abs(verticalOffset) == single_title_appbar.totalScrollRange) {
                PlayButtonAnimations().showPlayButton(single_title_play_collapsed, 1000)
            } else if (abs(verticalOffset) == 0) {
                PlayButtonAnimations().hidePlayButton(single_title_play_collapsed, 1000)
            }
        })

        viewModel.navigateScreen.observe(viewLifecycleOwner, EventObserver {
            navController(it)
        })

    }
}