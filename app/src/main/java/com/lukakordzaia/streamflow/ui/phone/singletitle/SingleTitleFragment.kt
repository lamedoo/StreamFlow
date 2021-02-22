package com.lukakordzaia.streamflow.ui.phone.singletitle

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.appbar.AppBarLayout
import com.lukakordzaia.streamflow.R
import com.lukakordzaia.streamflow.animations.PlayButtonAnimations
import com.lukakordzaia.streamflow.network.LoadingState
import com.lukakordzaia.streamflow.ui.baseclasses.BaseFragment
import com.lukakordzaia.streamflow.utils.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.phone_single_title_details.*
import kotlinx.android.synthetic.main.phone_single_title_fragment.*
import kotlinx.android.synthetic.main.phone_single_title_info.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class SingleTitleFragment : BaseFragment(R.layout.phone_single_title_fragment) {
    private val singleTitleViewModel by viewModel<SingleTitleViewModel>()
    private lateinit var singleTitleCastAdapter: SingleTitleCastAdapter
    private lateinit var singleTitleRelatedAdapter: SingleTitleRelatedAdapter
    private val args: SingleTitleFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        singleTitleViewModel.checkTitleInFirestore(args.titleId)

        singleTitleViewModel.noInternet.observe(viewLifecycleOwner, EventObserver {
            if (it) {
                requireContext().createToast(AppConstants.NO_INTERNET)
                Handler(Looper.getMainLooper()).postDelayed({
                    singleTitleViewModel.getSingleTitleData(args.titleId, "Bearer ${authSharedPreferences.getAccessToken()}")
                }, 5000)
            }
        })

        singleTitleViewModel.getSingleTitleData(args.titleId, "Bearer ${authSharedPreferences.getAccessToken()}")

        single_title_back_button.setOnClickListener {
            requireActivity().onBackPressed()
        }

        singleTitleViewModel.singleTitleLoader.observe(viewLifecycleOwner, {
            when (it.status) {
                LoadingState.Status.RUNNING -> {
                    single_title_progressBar.setVisible()
                }
                LoadingState.Status.SUCCESS -> {
                    single_title_progressBar.setGone()
                    single_title_main_container.setVisible()
                }
            }
        })

        singleTitleViewModel.traktFavoriteLoader.observe(viewLifecycleOwner, {
            when (it.status) {
                LoadingState.Status.RUNNING -> {
                    single_title_favorite_progressBar.setVisible()
                    single_title_favorite_icon.setGone()
                }
                LoadingState.Status.SUCCESS -> {
                    single_title_favorite_progressBar.setGone()
                    single_title_favorite_icon.setVisible()
                }
            }
        })

        singleTitleViewModel.addToFavorites.observe(viewLifecycleOwner, {
            if (it) {
                single_title_favorite_icon.setColorFilter(ContextCompat.getColor(requireContext(), R.color.accent_color))
                single_title_favorite.setOnClickListener {
                    singleTitleViewModel.removeTitleFromFirestore(args.titleId)
                }
            } else {
                single_title_favorite_icon.setColorFilter(ContextCompat.getColor(requireContext(), R.color.general_text_color))
                single_title_favorite.setOnClickListener {
                    singleTitleViewModel.addTitleToFirestore()
                }
            }
        })

        singleTitleViewModel.singleTitleData.observe(viewLifecycleOwner, {
            if (it.primaryName.isNotBlank()) {
                single_title_name_geo.text = it.primaryName
            }
            single_title_name_eng.text = it.secondaryName

            Picasso.get().load(it.covers?.data?.x1050).placeholder(R.drawable.movie_image_placeholder_landscape).error(R.drawable.movie_image_placeholder_landscape).into(single_title_cover)

            single_title_trailer_container.setOnClickListener { _ ->
                if (it.trailers.data.isNotEmpty()) {
                    it.trailers.data.forEach { trailer ->
                        if (trailer.language == "ENG") {
                            singleTitleViewModel.onTrailerPressed(
                                    args.titleId,
                                    it.isTvShow,
                                    trailer.fileUrl
                            )
                        } else {
                            requireContext().createToast("other trailer")
                        }
                    }
                } else {
                    requireContext().createToast("ტრეილერი არ არის")
                }
            }

            if (it.plot.data.description.isNotEmpty()) {
                single_title_desc.text = it.plot.data.description
            }

            if (it.rating.imdb != null) {
                tv_single_movie_imdb_score.text = it.rating.imdb.score.toString()
            }

            tv_single_title_year.text = it.year.toString()
            tv_single_title_duration.text = "${it.duration} წ."
            if (it.countries.data.isNotEmpty()) {
                tv_single_title_country.text = it.countries.data[0].secondaryName
            }
        })

        singleTitleViewModel.titleGenres.observe(viewLifecycleOwner, {
            single_title_genre_names.text = TextUtils.join(", ", it)
        })

        single_post_to_files_button.setOnClickListener {
            singleTitleViewModel.onPlayButtonPressed(args.titleId)

            PlayButtonAnimations().rotatePlayButton(single_post_to_files_button, 1000)
        }

        singleTitleViewModel.titleDirector.observe(viewLifecycleOwner, {
            single_title_director_name.text = it.originalName
        })


        // Cast
        val castLayout = GridLayoutManager(requireActivity(), 1, GridLayoutManager.HORIZONTAL, false)
        singleTitleCastAdapter = SingleTitleCastAdapter(requireContext()) {
            requireContext().createToast(it)
        }
        rv_single_title_cast.layoutManager = castLayout
        rv_single_title_cast.adapter = singleTitleCastAdapter

        singleTitleViewModel.castData.observe(viewLifecycleOwner, {
            singleTitleCastAdapter.setCastList(it)
        })

        //Related
        val relatedLayout = GridLayoutManager(requireActivity(), 1, GridLayoutManager.HORIZONTAL, false)
        singleTitleRelatedAdapter = SingleTitleRelatedAdapter(requireContext()) {
            singleTitleViewModel.onRelatedTitlePressed(it)
        }
        rv_single_title_related.layoutManager = relatedLayout
        rv_single_title_related.adapter = singleTitleRelatedAdapter

        singleTitleViewModel.singleTitleRelated.observe(viewLifecycleOwner, {
            singleTitleRelatedAdapter.setRelatedList(it)
        })

        single_title_appbar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { _, verticalOffset ->
            if (kotlin.math.abs(verticalOffset) == single_title_appbar.totalScrollRange) {
                single_title_details_scroll.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.primaryColor
                    )
                )
            } else {
                single_title_details_scroll.background = ResourcesCompat.getDrawable(
                    requireContext().resources,
                    R.drawable.single_title_tabs_background,
                    null
                )
            }
        })

        singleTitleViewModel.navigateScreen.observe(viewLifecycleOwner, EventObserver {
            navController(it)
        })

        singleTitleViewModel.toastMessage.observe(viewLifecycleOwner, EventObserver {
            requireContext().createToast(it)
        })

    }
}