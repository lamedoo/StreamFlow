package com.lukakordzaia.streamflow.ui.phone.singletitle

import android.app.Dialog
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.appbar.AppBarLayout
import com.lukakordzaia.streamflow.R
import com.lukakordzaia.streamflow.animations.PlayButtonAnimations
import com.lukakordzaia.streamflow.datamodels.TitleDetails
import com.lukakordzaia.streamflow.utils.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.clear_db_alert_dialog.*
import kotlinx.android.synthetic.main.phone_single_title_details.*
import kotlinx.android.synthetic.main.phone_single_title_fragment.*
import kotlinx.android.synthetic.main.phone_single_title_info.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class SingleTitleFragment : Fragment(R.layout.phone_single_title_fragment) {
    private val viewModel by viewModel<SingleTitleViewModel>()
    private lateinit var singleTitleCastAdapter: SingleTitleCastAdapter
    private lateinit var singleTitleRelatedAdapter: SingleTitleRelatedAdapter
    private val args: SingleTitleFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getSingleTitleData(args.titleId)
        viewModel.getSingleTitleCast(args.titleId)
        viewModel.getSingleTitleRelated(args.titleId)

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

        viewModel.titleGenres.observe(viewLifecycleOwner, {
            single_title_genre_names.text = TextUtils.join(", ", it)
        })

        viewModel.titleDetails.observe(viewLifecycleOwner, Observer {
            iv_post_video_icon.setOnClickListener { _ ->
                viewModel.onPlayPressed(args.titleId, TitleDetails(it.numOfSeasons, it.isTvShow))

                PlayButtonAnimations().rotatePlayButton(iv_post_video_icon, 1000)
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
            if (kotlin.math.abs(verticalOffset) == single_title_appbar.totalScrollRange) {
                single_title_details_scroll.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.primaryColor))
            } else {
                single_title_details_scroll.background = ResourcesCompat.getDrawable(requireContext().resources, R.drawable.single_title_tabs_background, null)
            }
        })

        viewModel.titleDirector.observe(viewLifecycleOwner, {
            single_title_director_name.text = it.originalName
        })


        // Cast
        val castLayout = GridLayoutManager(requireActivity(), 1, GridLayoutManager.HORIZONTAL, false)
        singleTitleCastAdapter = SingleTitleCastAdapter(requireContext()) {
            requireContext().createToast(it)
        }
        rv_single_title_cast.layoutManager = castLayout
        rv_single_title_cast.adapter = singleTitleCastAdapter

        viewModel.castData.observe(viewLifecycleOwner, {
            singleTitleCastAdapter.setCastList(it)
        })


        viewModel.navigateScreen.observe(viewLifecycleOwner, EventObserver {
            navController(it)
        })

        //Related
        val relatedLayout = GridLayoutManager(requireActivity(), 1, GridLayoutManager.HORIZONTAL, false)
        singleTitleRelatedAdapter = SingleTitleRelatedAdapter(requireContext()) {
            viewModel.onRelatedTitlePressed(it)
        }
        rv_single_title_related.layoutManager = relatedLayout
        rv_single_title_related.adapter = singleTitleRelatedAdapter

        viewModel.singleTitleRelated.observe(viewLifecycleOwner, {
            singleTitleRelatedAdapter.setRelatedList(it)
        })

    }
}