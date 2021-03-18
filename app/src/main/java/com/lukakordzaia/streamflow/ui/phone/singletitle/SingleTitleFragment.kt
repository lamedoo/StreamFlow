package com.lukakordzaia.streamflow.ui.phone.singletitle

import android.app.Dialog
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.appbar.AppBarLayout
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.lukakordzaia.streamflow.R
import com.lukakordzaia.streamflow.datamodels.VideoPlayerData
import com.lukakordzaia.streamflow.network.LoadingState
import com.lukakordzaia.streamflow.ui.baseclasses.BaseFragment
import com.lukakordzaia.streamflow.ui.phone.singletitle.choosetitledetails.ChooseTitleDetailsViewModel
import com.lukakordzaia.streamflow.ui.phone.videoplayer.VideoPlayerActivity
import com.lukakordzaia.streamflow.ui.tv.details.titledetails.TvChooseLanguageAdapter
import com.lukakordzaia.streamflow.utils.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.phone_single_title_details.*
import kotlinx.android.synthetic.main.phone_single_title_fragment.*
import kotlinx.android.synthetic.main.phone_single_title_info.*
import kotlinx.android.synthetic.main.tv_choose_language_dialog.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.concurrent.TimeUnit

class SingleTitleFragment : BaseFragment() {
    private val singleTitleViewModel: SingleTitleViewModel by viewModel()
    private val chooseTitleDetailsViewModel: ChooseTitleDetailsViewModel by viewModel()
    private lateinit var chooseLanguageAdapter: TvChooseLanguageAdapter
    private lateinit var singleTitleCastAdapter: SingleTitleCastAdapter
    private lateinit var singleTitleRelatedAdapter: SingleTitleRelatedAdapter
    private val args: SingleTitleFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return getPersistentView(inflater, container, savedInstanceState, R.layout.phone_single_title_fragment)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!hasInitializedRootView) {
            Log.d("onviewcreated", "true")
            hasInitializedRootView = true
            singleTitleViewModel.checkTitleInFirestore(args.titleId)
            singleTitleViewModel.getSingleTitleData(args.titleId, "Bearer ${authSharedPreferences.getAccessToken()}")
            chooseTitleDetailsViewModel.getSeasonFiles(args.titleId, 1)
        }

        if (requireActivity().resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            single_title_appbar.setExpanded(true)
            if (Firebase.auth.currentUser == null) {
                chooseTitleDetailsViewModel.checkContinueWatchingTitleInRoom(requireContext(), args.titleId).observe(viewLifecycleOwner, { exists ->
                    if (exists) {
                        chooseTitleDetailsViewModel.getSingleContinueWatchingFromRoom(requireContext(), args.titleId)
                    }
                })
            } else {
                chooseTitleDetailsViewModel.checkContinueWatchingInFirestore(args.titleId)
            }
        }

        singleTitleViewModel.noInternet.observe(viewLifecycleOwner, EventObserver {
            if (it) {
                requireContext().createToast(AppConstants.NO_INTERNET)
                Handler(Looper.getMainLooper()).postDelayed({
                    singleTitleViewModel.getSingleTitleData(args.titleId, "Bearer ${authSharedPreferences.getAccessToken()}")
                }, 5000)
            }
        })

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
                    val intent = Intent(context, VideoPlayerActivity::class.java)
                    intent.putExtra("videoPlayerData", VideoPlayerData(
                        args.titleId,
                        it.isTvShow,
                        0,
                        "ENG",
                        0,
                        0L,
                        it.trailers.data[0].fileUrl
                    ))
                    activity?.startActivity(intent)
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

            if (it.isTvShow) {
                single_post_to_episodes_button.setVisible()
            }


            single_post_to_files_button.setOnClickListener { _ ->
                languagePickerDialog()
            }
        })

        chooseTitleDetailsViewModel.continueWatchingDetails.observe(viewLifecycleOwner, {
            if (it != null) {

                single_post_to_files_button.setOnClickListener { _ ->
                    val intent = Intent(context, VideoPlayerActivity::class.java)
                    intent.putExtra("videoPlayerData", VideoPlayerData(
                        it.titleId,
                        it.isTvShow,
                        it.season,
                        it.language,
                        it.episode,
                        it.watchedDuration,
                        null
                    ))
                    activity?.startActivity(intent)
                }

                if (it.isTvShow) {
                    single_post_to_files_button.text = String.format(
                            "ს:${it.season} ე:${it.episode} / %02d:%02d",
                            TimeUnit.MILLISECONDS.toMinutes(it.watchedDuration),
                            TimeUnit.MILLISECONDS.toSeconds(it.watchedDuration) -
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(it.watchedDuration))
                    )
                } else {
                    single_post_to_files_button.text = String.format(
                            "%02d:%02d",
                            TimeUnit.MILLISECONDS.toMinutes(it.watchedDuration),
                            TimeUnit.MILLISECONDS.toSeconds(it.watchedDuration) -
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(it.watchedDuration))
                    )

                    single_post_replay_button.setVisible()
                    single_post_replay_button.setOnClickListener {
                        languagePickerDialog()
                    }
                }
            }
        })

        single_post_to_episodes_button?.setOnClickListener {
            singleTitleViewModel.onEpisodesPressed(args.titleId)
        }

        singleTitleViewModel.titleGenres.observe(viewLifecycleOwner, {
            single_title_genre_names.text = TextUtils.join(", ", it)
        })

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

    private fun languagePickerDialog() {
        val chooseLanguageDialog = Dialog(requireContext())
        chooseLanguageDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        chooseLanguageDialog.setContentView(layoutInflater.inflate(R.layout.tv_choose_language_dialog, null))
        chooseLanguageDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        chooseLanguageDialog.show()

        val chooseLanguageLayout = GridLayoutManager(requireActivity(), 1, GridLayoutManager.HORIZONTAL, false)
        chooseLanguageAdapter = TvChooseLanguageAdapter(requireContext()) { language ->
            chooseLanguageDialog.hide()
            val intent = Intent(context, VideoPlayerActivity::class.java)
            intent.putExtra("videoPlayerData", VideoPlayerData(
                args.titleId,
                singleTitleViewModel.isTvShow.value!!,
                0,
                language,
                0,
                0L,
                null
            ))
            activity?.startActivity(intent)
        }
        chooseLanguageDialog.rv_tv_choose_language.layoutManager = chooseLanguageLayout
        chooseLanguageDialog.rv_tv_choose_language.adapter = chooseLanguageAdapter

        chooseTitleDetailsViewModel.availableLanguages.observe(viewLifecycleOwner, { languageList ->
            val languages = languageList.reversed()
            chooseLanguageAdapter.setLanguageList(languages)
        })
    }
}