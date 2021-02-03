package com.lukakordzaia.imoviesapp.ui.phone.singletitle

import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.google.android.material.appbar.AppBarLayout
import com.lukakordzaia.imoviesapp.R
import com.lukakordzaia.imoviesapp.animations.PlayButtonAnimations
import com.lukakordzaia.imoviesapp.datamodels.TitleDetails
import com.lukakordzaia.imoviesapp.utils.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.clear_db_alert_dialog.*
import kotlinx.android.synthetic.main.phone_single_title_fragment.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.math.abs

class SingleTitleFragment : Fragment(R.layout.phone_single_title_fragment) {
    private val viewModel by viewModel<SingleTitleViewModel>()
    private val args: SingleTitleFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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

            single_title_trailer_container.setOnClickListener { _ ->
                if (it.trailers != null) {
                    if (!it.trailers.data.isNullOrEmpty()) {
                        it.trailers.data.forEach { trailer ->
                            if (trailer.language == "ENG") {
                                viewModel.onTrailerPressed(args.titleId, it.isTvShow!!, trailer.fileUrl)
                            } else {
                                requireContext().createToast("other trailer")
                            }
                        }
                    } else {
                        requireContext().createToast("ტრეილერი არ არის")
                    }
                } else {
                    requireContext().createToast("ტრეილერი არ არის")
                }
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
                    val clearDbDialog = Dialog(requireContext())
                    clearDbDialog.setContentView(layoutInflater.inflate(R.layout.clear_db_alert_dialog, null))
                    clearDbDialog.clear_db_alert_yes.setOnClickListener {
                        viewModel.deleteTitleFromDb(requireContext(), args.titleId)
                        clearDbDialog.dismiss()
                    }
                    clearDbDialog.clear_db_alert_no.setOnClickListener {
                        clearDbDialog.dismiss()
                    }
                    clearDbDialog.show()
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