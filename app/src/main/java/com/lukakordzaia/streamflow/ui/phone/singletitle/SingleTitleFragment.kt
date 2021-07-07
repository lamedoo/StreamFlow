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
import com.lukakordzaia.streamflow.databinding.FragmentPhoneSingleTitleBinding
import com.lukakordzaia.streamflow.datamodels.VideoPlayerData
import com.lukakordzaia.streamflow.network.LoadingState
import com.lukakordzaia.streamflow.ui.baseclasses.BaseFragment
import com.lukakordzaia.streamflow.ui.phone.singletitle.choosetitledetails.ChooseTitleDetailsViewModel
import com.lukakordzaia.streamflow.ui.phone.videoplayer.VideoPlayerActivity
import com.lukakordzaia.streamflow.ui.tv.details.titledetails.TvChooseLanguageAdapter
import com.lukakordzaia.streamflow.utils.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.phone_single_title_details.*
import kotlinx.android.synthetic.main.phone_single_title_info.*
import kotlinx.android.synthetic.main.tv_choose_language_dialog.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.concurrent.TimeUnit

class SingleTitleFragment : BaseFragment<FragmentPhoneSingleTitleBinding>() {
    private val singleTitleViewModel: SingleTitleViewModel by viewModel()
    private val chooseTitleDetailsViewModel: ChooseTitleDetailsViewModel by viewModel()
    private lateinit var chooseLanguageAdapter: TvChooseLanguageAdapter
    private lateinit var singleTitleCastAdapter: SingleTitleCastAdapter
    private lateinit var singleTitleRelatedAdapter: SingleTitleRelatedAdapter
    private val args: SingleTitleFragmentArgs by navArgs()

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentPhoneSingleTitleBinding
        get() = FragmentPhoneSingleTitleBinding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!hasInitializedRootView) {
            hasInitializedRootView = true
            singleTitleViewModel.checkTitleInFirestore(args.titleId)
            singleTitleViewModel.getSingleTitleData(args.titleId, "Bearer ${authSharedPreferences.getAccessToken()}")
            chooseTitleDetailsViewModel.getSeasonFiles(args.titleId, 1)
        }

        checkAuth()
        fragmentListeners()
        fragmentObservers()
        titleDetailsContainer()
        castContainer()
        relatedContainer()
    }

    private fun checkAuth() {
        if (requireActivity().resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            binding.singleTitleAppbar.setExpanded(true)
            if (Firebase.auth.currentUser == null) {
                chooseTitleDetailsViewModel.checkContinueWatchingTitleInRoom(requireContext(), args.titleId).observe(viewLifecycleOwner, { exists ->
                    if (exists) {
                        chooseTitleDetailsViewModel.getSingleContinueWatchingFromRoom(requireContext(), args.titleId)
                    }
                })
            } else {
                chooseTitleDetailsViewModel.checkContinueWatchingInFirestore1(args.titleId)
            }
        }
    }

    private fun fragmentListeners() {
        binding.singleTitleAppbar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { _, verticalOffset ->
            if (kotlin.math.abs(verticalOffset) == binding.singleTitleAppbar.totalScrollRange) {
                binding.singleTitleDetailsScroll.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.primaryColor
                    )
                )
            } else {
                binding.singleTitleDetailsScroll.background = ResourcesCompat.getDrawable(
                    requireContext().resources,
                    R.drawable.single_title_tabs_background,
                    null
                )
            }
        })

        binding.singleTitleBackButton.setOnClickListener {
            requireActivity().onBackPressed()
        }

        singleTitleViewModel.addToFavorites.observe(viewLifecycleOwner, {
            if (it) {
                binding.singleTitleFavoriteIcon.setColorFilter(ContextCompat.getColor(requireContext(), R.color.accent_color))
                binding.singleTitleFavorite.setOnClickListener {
                    singleTitleViewModel.removeTitleFromFirestore(args.titleId)
                }
            } else {
                binding.singleTitleFavoriteIcon.setColorFilter(ContextCompat.getColor(requireContext(), R.color.general_text_color))
                binding.singleTitleFavorite.setOnClickListener {
                    singleTitleViewModel.addTitleToFirestore()
                }
            }
        })

        chooseTitleDetailsViewModel.continueWatchingDetails.observe(viewLifecycleOwner, {
            if (it != null) {
                binding.playButton.setOnClickListener { _ ->
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
                    binding.playButton.text = String.format(
                        "ს:${it.season} ე:${it.episode} / %02d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes(it.watchedDuration),
                        TimeUnit.MILLISECONDS.toSeconds(it.watchedDuration) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(it.watchedDuration))
                    )
                } else {
                    binding.playButton.text = String.format(
                        "%02d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes(it.watchedDuration),
                        TimeUnit.MILLISECONDS.toSeconds(it.watchedDuration) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(it.watchedDuration))
                    )

                    binding.replayButton.setVisible()
                    binding.replayButton.setOnClickListener {
                        languagePickerDialog()
                    }
                }
            } else {
                binding.playButton.setOnClickListener { _ ->
                    languagePickerDialog()
                }
            }
        })

        binding.episodesButton.setOnClickListener {
            singleTitleViewModel.onEpisodesPressed(args.titleId)
        }
    }

    private fun fragmentObservers() {
        singleTitleViewModel.noInternet.observe(viewLifecycleOwner, EventObserver {
            if (it) {
                requireContext().createToast(AppConstants.NO_INTERNET)
                Handler(Looper.getMainLooper()).postDelayed({
                    singleTitleViewModel.getSingleTitleData(args.titleId, "Bearer ${authSharedPreferences.getAccessToken()}")
                }, 5000)
            }
        })

        singleTitleViewModel.traktFavoriteLoader.observe(viewLifecycleOwner, {
            when (it.status) {
                LoadingState.Status.RUNNING -> {
                    binding.singleTitleFavoriteProgressBar.setVisible()
                    binding.singleTitleFavoriteIcon.setGone()
                }
                LoadingState.Status.SUCCESS -> {
                    binding.singleTitleFavoriteProgressBar.setGone()
                    binding.singleTitleFavoriteIcon.setVisible()
                }
            }
        })

        singleTitleViewModel.navigateScreen.observe(viewLifecycleOwner, EventObserver {
            navController(it)
        })

        singleTitleViewModel.toastMessage.observe(viewLifecycleOwner, EventObserver {
            requireContext().createToast(it)
        })
    }

    private fun titleDetailsContainer() {
        singleTitleViewModel.singleTitleLoader.observe(viewLifecycleOwner, {
            when (it.status) {
                LoadingState.Status.RUNNING -> {
                    binding.singleTitleProgressBar.setVisible()
                }
                LoadingState.Status.SUCCESS -> {
                    binding.singleTitleProgressBar.setGone()
                    binding.singleTitleMainContainer.setVisible()
                }
            }
        })

        singleTitleViewModel.singleTitleData.observe(viewLifecycleOwner, {
            if (it.primaryName.isNotBlank()) {
                binding.singleTitleNameGeo.text = it.primaryName
            }
            binding.singleTitleNameEng.text = it.secondaryName

            Picasso.get().load(it.covers?.data?.x1050).placeholder(R.drawable.movie_image_placeholder_landscape).error(R.drawable.movie_image_placeholder_landscape).into(binding.singleTitleCover)

            binding.singleTitleTrailerContainer.setOnClickListener { _ ->
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
                binding.singleTitleDesc.text = it.plot.data.description
            }

            if (it.rating.imdb != null) {
                binding.infoDetails.tvSingleMovieImdbScore.text = it.rating.imdb.score.toString()
            }

            binding.infoDetails.tvSingleTitleYear.text = it.year.toString()
            binding.infoDetails.tvSingleTitleDuration.text = "${it.duration} წ."
            if (it.countries.data.isNotEmpty()) {
                binding.infoDetails.tvSingleTitleCountry.text = it.countries.data[0].secondaryName
            }

            if (it.isTvShow) {
                binding.episodesButton.setVisible()
            }
        })

        singleTitleViewModel.titleGenres.observe(viewLifecycleOwner, {
            single_title_genre_names.text = TextUtils.join(", ", it)
        })

        singleTitleViewModel.titleDirector.observe(viewLifecycleOwner, {
            single_title_director_name.text = it.originalName
        })
    }

    private fun castContainer() {
        val castLayout = GridLayoutManager(requireActivity(), 1, GridLayoutManager.HORIZONTAL, false)
        singleTitleCastAdapter = SingleTitleCastAdapter(requireContext()) {
            requireContext().createToast(it)
        }
        rv_single_title_cast.layoutManager = castLayout
        rv_single_title_cast.adapter = singleTitleCastAdapter

        singleTitleViewModel.castData.observe(viewLifecycleOwner, {
            singleTitleCastAdapter.setCastList(it)
        })
    }

    private fun relatedContainer() {
        val relatedLayout = GridLayoutManager(requireActivity(), 1, GridLayoutManager.HORIZONTAL, false)
        singleTitleRelatedAdapter = SingleTitleRelatedAdapter(requireContext()) {
            singleTitleViewModel.onRelatedTitlePressed(it)
        }
        rv_single_title_related.layoutManager = relatedLayout
        rv_single_title_related.adapter = singleTitleRelatedAdapter

        singleTitleViewModel.singleTitleRelated.observe(viewLifecycleOwner, {
            singleTitleRelatedAdapter.setRelatedList(it)
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
                if (singleTitleViewModel.isTvShow.value!!) 1 else 0,
                language,
                if (singleTitleViewModel.isTvShow.value!!) 1 else 0,
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