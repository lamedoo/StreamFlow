package com.lukakordzaia.streamflow.ui.phone.phonesingletitle

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.appbar.AppBarLayout
import com.lukakordzaia.streamflow.R
import com.lukakordzaia.streamflow.database.continuewatchingdb.ContinueWatchingRoom
import com.lukakordzaia.streamflow.databinding.DialogChooseLanguageBinding
import com.lukakordzaia.streamflow.databinding.FragmentPhoneSingleTitleBinding
import com.lukakordzaia.streamflow.databinding.FragmentPhoneSingleTitleNewBinding
import com.lukakordzaia.streamflow.datamodels.SingleTitleModel
import com.lukakordzaia.streamflow.datamodels.VideoPlayerData
import com.lukakordzaia.streamflow.network.LoadingState
import com.lukakordzaia.streamflow.ui.baseclasses.BaseFragment
import com.lukakordzaia.streamflow.ui.phone.phonesingletitle.tvshowdetailsbottomsheet.TvShowBottomSheetViewModel
import com.lukakordzaia.streamflow.ui.phone.videoplayer.VideoPlayerActivity
import com.lukakordzaia.streamflow.ui.tv.tvsingletitle.tvtitledetails.TvChooseLanguageAdapter
import com.lukakordzaia.streamflow.utils.*
import kotlinx.android.synthetic.main.phone_single_title_info.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.concurrent.TimeUnit

class PhoneSingleTitleFragment : BaseFragment<FragmentPhoneSingleTitleNewBinding>() {
    private val phoneSingleTitleViewModel: PhoneSingleTitleViewModel by viewModel()
    private val tvShowBottomSheetViewModel: TvShowBottomSheetViewModel by viewModel()
    private lateinit var titleInfo: SingleTitleModel
    private lateinit var chooseLanguageAdapter: TvChooseLanguageAdapter
    private lateinit var phoneSingleTitleCastAdapter: PhoneSingleTitleCastAdapter
    private lateinit var phoneSingleTitleRelatedAdapter: PhoneSingleTitleRelatedAdapter
    private val args: PhoneSingleTitleFragmentArgs by navArgs()

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentPhoneSingleTitleNewBinding
        get() = FragmentPhoneSingleTitleNewBinding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.singleTitleAppbar.setExpanded(true)

        checkAuth()
        checkFavorites()

        fragmentListeners()
        fragmentObservers()
        titleDetailsContainer()
        castContainer()
        relatedContainer()
    }

    private fun checkAuth() {
        tvShowBottomSheetViewModel.checkAuthDatabase(args.titleId)
        tvShowBottomSheetViewModel.continueWatchingDetails.observe(viewLifecycleOwner, {
            if (it != null) {
                binding.playButton.setOnClickListener { _ ->
                    startVideoPlayer(it, null)
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
                    binding.replayButton.setOnClickListener { _ ->
                        languagePickerDialog()
                    }
                }
            } else {
                binding.playButton.setOnClickListener { _ ->
                    languagePickerDialog()
                }
            }
        })
    }

    private fun checkFavorites() {
        phoneSingleTitleViewModel.checkTitleInFavorites(args.titleId)

        phoneSingleTitleViewModel.addToFavorites.observe(viewLifecycleOwner, {
            if (it) {
                binding.singleTitleFavoriteIcon.setColorFilter(ContextCompat.getColor(requireContext(), R.color.accent_color))
                binding.singleTitleFavorite.setOnClickListener {
                    phoneSingleTitleViewModel.removeTitleFromFavorites(args.titleId)
                }
            } else {
                binding.singleTitleFavoriteIcon.setColorFilter(ContextCompat.getColor(requireContext(), R.color.general_text_color))
                binding.singleTitleFavorite.setOnClickListener {
                    phoneSingleTitleViewModel.addTitleToFirestore(titleInfo)
                }
            }
        })

        phoneSingleTitleViewModel.favoriteLoader.observe(viewLifecycleOwner, {
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
    }

    private fun fragmentObservers() {
        phoneSingleTitleViewModel.noInternet.observe(viewLifecycleOwner, EventObserver {
            if (it) {
                requireContext().createToast(AppConstants.NO_INTERNET)
                Handler(Looper.getMainLooper()).postDelayed({
                    phoneSingleTitleViewModel.getSingleTitleData(args.titleId, "Bearer ${authSharedPreferences.getAccessToken()}")
                }, 5000)
            }
        })

        phoneSingleTitleViewModel.navigateScreen.observe(viewLifecycleOwner, EventObserver {
            navController(it)
        })

        phoneSingleTitleViewModel.toastMessage.observe(viewLifecycleOwner, EventObserver {
            requireContext().createToast(it)
        })
    }

    private fun titleDetailsContainer() {
        phoneSingleTitleViewModel.getSingleTitleData(args.titleId, "Bearer ${authSharedPreferences.getAccessToken()}")

        phoneSingleTitleViewModel.singleTitleLoader.observe(viewLifecycleOwner, {
            when (it.status) {
                LoadingState.Status.RUNNING -> {
                    binding.progressBar.setVisible()
                }
                LoadingState.Status.SUCCESS -> {
                    binding.progressBar.setGone()
                    binding.singleTitleMainContainer.setVisible()
                }
            }
        })

        phoneSingleTitleViewModel.getSingleTitleResponse.observe(viewLifecycleOwner, {
            titleInfo = it

            binding.singleTitleNameGeo.text = it.nameGeo
            binding.singleTitleNameEng.text = it.nameEng

            binding.singleTitleCover.setImage(it.cover, false)

            binding.titleTrailer.setOnClickListener { _ ->
                startTrailer(it)
            }

            binding.singleTitleDesc.text = it.description
            binding.infoDetails.tvSingleMovieImdbScore.text = it.imdbScore
            binding.infoDetails.tvSingleTitleYear.text = it.releaseYear
            binding.infoDetails.tvSingleTitleDuration.text = it.duration
            binding.infoDetails.tvSingleTitleCountry.text = it.country

            if (it.isTvShow) {
                binding.episodesButton.setVisible()
                binding.episodesButton.setOnClickListener { _ ->
                    phoneSingleTitleViewModel.onEpisodesPressed(it.id, it.displayName!!, it.seasonNum!!)
                }
            }
        })

        phoneSingleTitleViewModel.titleGenres.observe(viewLifecycleOwner, {
            single_title_genre_names.text = TextUtils.join(", ", it)
        })

        phoneSingleTitleViewModel.getSingleTitleDirectorResponse.observe(viewLifecycleOwner, {
            single_title_director_name.text = it.originalName
        })
    }

    private fun castContainer() {
        val castLayout = GridLayoutManager(requireActivity(), 1, GridLayoutManager.HORIZONTAL, false)
        phoneSingleTitleCastAdapter = PhoneSingleTitleCastAdapter(requireContext()) {
            requireContext().createToast(it)
        }
        rv_single_title_cast.layoutManager = castLayout
        rv_single_title_cast.adapter = phoneSingleTitleCastAdapter

        phoneSingleTitleViewModel.castResponseDataGetSingle.observe(viewLifecycleOwner, {
            phoneSingleTitleCastAdapter.setCastList(it)
        })
    }

    private fun relatedContainer() {
        val relatedLayout = GridLayoutManager(requireActivity(), 1, GridLayoutManager.HORIZONTAL, false)
        phoneSingleTitleRelatedAdapter = PhoneSingleTitleRelatedAdapter(requireContext()) {
            phoneSingleTitleViewModel.onRelatedTitlePressed(it)
        }
        rv_single_title_related.layoutManager = relatedLayout
        rv_single_title_related.adapter = phoneSingleTitleRelatedAdapter

        phoneSingleTitleViewModel.singleTitleRelated.observe(viewLifecycleOwner, {
            phoneSingleTitleRelatedAdapter.setRelatedList(it)
        })
    }

    private fun languagePickerDialog() {
        tvShowBottomSheetViewModel.getSeasonFiles(args.titleId, 1)

        val binding = DialogChooseLanguageBinding.inflate(LayoutInflater.from(requireContext()))
        val chooseLanguageDialog = Dialog(requireContext())
        chooseLanguageDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        chooseLanguageDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        chooseLanguageDialog.setContentView(binding.root)
        chooseLanguageDialog.show()

        val chooseLanguageLayout = GridLayoutManager(requireActivity(), 1, GridLayoutManager.HORIZONTAL, false)
        chooseLanguageAdapter = TvChooseLanguageAdapter(requireContext()) { language ->
            chooseLanguageDialog.hide()
            startVideoPlayer(null, language)
        }
        binding.rvChooseLanguage.layoutManager = chooseLanguageLayout
        binding.rvChooseLanguage.adapter = chooseLanguageAdapter

        tvShowBottomSheetViewModel.availableLanguages.observe(viewLifecycleOwner, { languageList ->
            val languages = languageList.reversed()
            chooseLanguageAdapter.setLanguageList(languages)
        })
    }

    private fun startTrailer(data: SingleTitleModel) {
        if (data.trailer != null) {
            requireActivity().startActivity(VideoPlayerActivity.startFromTrailers(requireContext(), VideoPlayerData(
                data.id,
                false,
                0,
                "ENG",
                0,
                0L,
                data.trailer
            )
            ))
        } else {
            phoneSingleTitleViewModel.newToastMessage("ტრეილერი ვერ მოიძებნა")
        }
    }

    private fun startVideoPlayer(contWatching: ContinueWatchingRoom?, language: String?) {
        requireActivity().startActivity(VideoPlayerActivity.startFromSingleTitle(requireContext(), VideoPlayerData(
            contWatching?.titleId ?: titleInfo.id,
            contWatching?.isTvShow ?: titleInfo.isTvShow,
            contWatching?.season ?: if (titleInfo.isTvShow) 1 else 0,
            contWatching?.language ?: language!!,
            contWatching?.episode ?: if (titleInfo.isTvShow) 1 else 0,
             contWatching?.watchedDuration ?: 0L,
            null
        )
        ))
    }
}