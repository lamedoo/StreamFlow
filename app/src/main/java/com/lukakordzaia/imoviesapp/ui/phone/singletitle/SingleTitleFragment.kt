package com.lukakordzaia.imoviesapp.ui.phone.singletitle

import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.lukakordzaia.imoviesapp.R
import com.lukakordzaia.imoviesapp.animations.PlayButtonAnimations
import com.lukakordzaia.imoviesapp.datamodels.TitleDetails
import com.lukakordzaia.imoviesapp.ui.phone.singletitle.tabs.TabsPagerAdapter
import com.lukakordzaia.imoviesapp.utils.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.clear_db_alert_dialog.*
import kotlinx.android.synthetic.main.phone_single_title_fragment_new.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class SingleTitleFragment : Fragment(R.layout.phone_single_title_fragment_new) {
    private val viewModel by viewModel<SingleTitleViewModel>()
    private val args: SingleTitleFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getSingleTitleData(args.titleId)

        single_title_back_button.setOnClickListener {
            requireActivity().onBackPressed()
        }

        viewModel.isLoading.observe(viewLifecycleOwner, EventObserver {
            if (!it) {
                single_title_progressBar.setGone()
                single_title_main_container.setVisible()
            }
        })

        viewModel.singleTitleData.observe(viewLifecycleOwner, {
            if (it.primaryName.isNotBlank()) {
                tv_single_title_name_geo.text = it.primaryName
            }
            tv_single_title_name_eng.text = it.secondaryName
            if (!it.covers?.data?.x1050.isNullOrEmpty()) {
                Picasso.get().load(it.covers?.data?.x1050).into(iv_single_title_play)
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

            if (it.plot.data != null) {
                if (!it.plot.data.description.isNullOrEmpty()) {
                    single_title_desc.text = it.plot.data.description
                } else {
                    single_title_desc.text = "აღწერა არ მოიძებნა"
                }
            } else {
                single_title_desc.text = "აღწერა არ მოიძებნა"
            }

            if (it.rating.imdb != null) {
                tv_single_movie_imdb_score.text = it.rating.imdb.score.toString()
            }

            tv_single_title_year.text = it.year.toString()
            tv_single_title_duration.text = "${it.duration} წ."
            if (it.countries.data.isEmpty()) {
                tv_single_title_country.text = "N/A"
            } else {
                tv_single_title_country.text = it.countries.data[0].secondaryName
            }

        })

        viewModel.titleDetails.observe(viewLifecycleOwner, Observer {
            iv_post_video_icon.setOnClickListener { _ ->
                viewModel.onPlayPressed(args.titleId, TitleDetails(it.numOfSeasons, it.isTvShow))

                PlayButtonAnimations().rotatePlayButton(iv_post_video_icon, 1000)
            }
//            single_title_play_collapsed.setOnClickListener { _ ->
//                viewModel.onPlayPressed(args.titleId, TitleDetails(it.numOfSeasons, it.isTvShow))
//
//                PlayButtonAnimations().rotatePlayButton(single_title_play_collapsed, 1000)
//            }
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
//            if (abs(verticalOffset) == single_title_appbar.totalScrollRange) {
//                PlayButtonAnimations().showPlayButton(single_title_play_collapsed, 1000)
//            } else if (abs(verticalOffset) == 0) {
//                PlayButtonAnimations().hidePlayButton(single_title_play_collapsed, 1000)
//            }
        })


        // Tabs Customization
        tab_layout.setSelectedTabIndicatorColor(ContextCompat.getColor(requireContext(), R.color.accent_color))
        tab_layout.tabTextColors = ContextCompat.getColorStateList(requireContext(), R.color.general_text_color)
        val numberOfTabs = 2

        tab_layout.tabMode = TabLayout.MODE_FIXED
        tab_layout.isInlineLabel = true

        val adapter = TabsPagerAdapter(childFragmentManager, lifecycle, numberOfTabs, args.titleId)
        tabs_viewpager.adapter = adapter
        tabs_viewpager.isUserInputEnabled = false



        TabLayoutMediator(tab_layout, tabs_viewpager) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = "ინფო"
                }
                1 -> {
                    tab.text = "მსგავსი"
                }
            }
        }.attach()


        viewModel.navigateScreen.observe(viewLifecycleOwner, EventObserver {
            navController(it)
        })

    }
}