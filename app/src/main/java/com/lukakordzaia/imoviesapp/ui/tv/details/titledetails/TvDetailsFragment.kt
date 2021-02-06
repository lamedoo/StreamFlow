package com.lukakordzaia.imoviesapp.ui.tv.details.titledetails

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.lukakordzaia.imoviesapp.R
import com.lukakordzaia.imoviesapp.ui.tv.details.TvDetailsViewModel
import com.lukakordzaia.imoviesapp.ui.tv.tvvideoplayer.TvVideoPlayerActivity
import com.lukakordzaia.imoviesapp.utils.createToast
import com.lukakordzaia.imoviesapp.utils.setGone
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.tv_details_fragment.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class TvDetailsFragment : Fragment(R.layout.tv_details_fragment_new) {
    private val tvDetailsViewModel by viewModel<TvDetailsViewModel>()
    private var trailerUrl: String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val titleId = activity?.intent?.getSerializableExtra("titleId") as Int
        val isTvShow = activity?.intent?.getSerializableExtra("isTvShow") as Boolean

        tvDetailsViewModel.getSingleTitleData(titleId)

        // LEFT SIDE
        tvDetailsViewModel.singleTitleData.observe(viewLifecycleOwner, {
            tv_files_trailer.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
                if (hasFocus) {
                    v.background = ResourcesCompat.getDrawable(requireContext().resources, R.drawable.tv_remove_title_from_db_focused, null)
                } else {
                    v.background = ResourcesCompat.getDrawable(requireContext().resources, R.drawable.tv_remove_title_from_db, null)
                }
            }

            tv_files_trailer.setOnClickListener { _ ->
                if (it.trailers != null) {
                    if (!it.trailers.data.isNullOrEmpty()) {
                        it.trailers.data.forEach { trailer ->
                            if (trailer.language == "ENG") {
                                trailerUrl = trailer.fileUrl
                                val intent = Intent(context, TvVideoPlayerActivity::class.java)
                                intent.putExtra("titleId", titleId)
                                intent.putExtra("isTvShow", isTvShow)
                                intent.putExtra("chosenLanguage", "ENG")
                                intent.putExtra("chosenSeason", 0)
                                intent.putExtra("chosenEpisode", 0)
                                intent.putExtra("watchedTime", 0L)
                                intent.putExtra("trailerUrl", trailerUrl)
                                activity?.startActivity(intent)
                            } else {
                                requireContext().createToast("other trailer")
                            }
                        }
                    } else {
                        requireContext().createToast("no trailer")
                    }
                } else {
                    requireContext().createToast("no trailer")
                }
            }

            if (it.covers != null) {
                if (it.covers.data != null) {
                    if (!it.covers.data.x1050.isNullOrEmpty()) {
                        Picasso.get().load(it.covers.data.x1050).into(tv_files_title_poster)
                    } else {
                        Picasso.get().load(R.drawable.movie_image_placeholder).into(tv_files_title_poster)
                    }
                }
            }

            tv_files_title_year.text = it.year.toString()
            if (it.rating.imdb?.score != null) {
                tv_files_title_imdb_score.text = it.rating.imdb.score.toString()
            }
            if (it.countries.data.isEmpty()) {
                tv_files_title_country.text = "N/A"
            } else {
                tv_files_title_country.text = it.countries.data[0].secondaryName
            }
            tv_files_title_name_eng.text = it.secondaryName
            tv_files_title_name_geo.text = it.primaryName
            if (it.isTvShow == true) {
                tv_files_title_duration.text = "${it.seasons.data.size} სეზონი"
            } else {
                tv_files_title_duration.text = "${it.duration.toString()} წთ."
            }
            if (it.plot.data != null) {
                if (!it.plot.data.description.isNullOrEmpty()) {
                    tv_files_title_desc.text = it.plot.data.description
                } else {
                    tv_files_title_desc.text = "აღწერა არ მოიძებნა"
                }
            } else {
                tv_files_title_desc.text = "აღწერა არ მოიძებნა"
            }
        })

        tvDetailsViewModel.checkTitleInDb(requireContext(), titleId).observe(viewLifecycleOwner, {
            tvDetailsViewModel.titleIsInDb(it)
            if (it) {
                tv_files_title_delete.setOnClickListener {
                    tvDetailsViewModel.deleteTitleFromDb(requireContext(), titleId)
                }
                tv_files_title_delete.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
                    if (hasFocus) {
                        v.background = ResourcesCompat.getDrawable(requireContext().resources, R.drawable.tv_remove_title_from_db_focused, null)
                    } else {
                        v.background = ResourcesCompat.getDrawable(requireContext().resources, R.drawable.tv_remove_title_from_db, null)
                    }
                }
            } else {
                tv_files_title_delete.setGone()
            }
        })
    }
}