package com.lukakordzaia.streamflowtv.ui.tvsingletitle.tvtitledetails

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isGone
import androidx.recyclerview.widget.GridLayoutManager
import com.lukakordzaia.core.utils.AppConstants
import com.lukakordzaia.core.adapters.ChooseLanguageAdapter
import com.lukakordzaia.core.baseclasses.BaseFragmentVM
import com.lukakordzaia.core.database.continuewatchingdb.ContinueWatchingRoom
import com.lukakordzaia.core.databinding.DialogChooseLanguageBinding
import com.lukakordzaia.core.databinding.DialogRemoveTitleBinding
import com.lukakordzaia.core.datamodels.SingleTitleModel
import com.lukakordzaia.core.datamodels.VideoPlayerData
import com.lukakordzaia.core.network.LoadingState
import com.lukakordzaia.core.utils.setImage
import com.lukakordzaia.core.utils.setVisibleOrGone
import com.lukakordzaia.core.utils.titlePosition
import com.lukakordzaia.streamflowtv.R
import com.lukakordzaia.streamflowtv.databinding.FragmentTvTitleDetailsBinding
import com.lukakordzaia.streamflowtv.ui.tvsingletitle.TvSingleTitleActivity
import com.lukakordzaia.streamflowtv.ui.tvsingletitle.tvtitlefiles.TvTitleFilesFragment
import com.lukakordzaia.streamflowtv.ui.tvvideoplayer.TvVideoPlayerActivity
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.concurrent.TimeUnit


class TvTitleDetailsFragment : BaseFragmentVM<FragmentTvTitleDetailsBinding, TvTitleDetailsViewModel>() {
    private var titleId: Int = 0
    private var isTvShow: Boolean = false
    private var fromWatchlist: Int? = null
    private var continueWatching: Boolean = false

    override val viewModel by viewModel<TvTitleDetailsViewModel>()
    override val reload: () -> Unit = {
        viewModel.getSingleTitleData(titleId)
        viewModel.getSingleTitleFiles(titleId)
    }

    private lateinit var languages: List<String>
    private lateinit var chooseLanguageAdapter: ChooseLanguageAdapter
    private lateinit var titleInfo: SingleTitleModel
    private var hasFocus: Boolean = false
    private var startedWatching = false

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentTvTitleDetailsBinding
        get() = FragmentTvTitleDetailsBinding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val args = arguments
        val isContinue = args?.getBoolean(AppConstants.CONTINUE_WATCHING_NOW) ?: false
        continueWatching = isContinue

        titleId = activity?.intent?.getSerializableExtra(AppConstants.TITLE_ID) as Int
        isTvShow = activity?.intent?.getSerializableExtra(AppConstants.IS_TV_SHOW) as Boolean
        fromWatchlist = activity?.intent?.getSerializableExtra(AppConstants.FROM_WATCHLIST) as? Int

        fragmentSetUi()
        fragmentListeners()
        fragmentObservers()
    }

    override fun onStart() {
        super.onStart()
        viewModel.getContinueWatching(titleId)
    }

    private fun fragmentSetUi() {
        if (!isTvShow) {
            binding.nextDetailsTitle.text = getString(R.string.cast_more)
        }

        viewModel.getSingleTitleData(titleId)
        viewModel.getSingleTitleFiles(titleId)
    }

    private fun fragmentListeners() {
        binding.playButton.setOnClickListener {
            languagePickerDialog()
        }

        binding.deleteButton.setOnClickListener {
            removeTitleDialog()
        }

        binding.nextDetails.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                (requireActivity() as TvSingleTitleActivity).setCurrentFragment(TvTitleFilesFragment())
            }
            this.hasFocus = hasFocus
        }
    }

    private fun fragmentObservers() {
        viewModel.generalLoader.observe(viewLifecycleOwner, {
            binding.tvDetailsProgressBar.setVisibleOrGone(it == LoadingState.LOADING)
        })

        viewModel.movieNotYetAdded.observe(viewLifecycleOwner, {
            binding.noFilesContainer.setVisibleOrGone(it)
            binding.buttonsRow.setVisibleOrGone(!it)
        })

        viewModel.getSingleTitleResponse.observe(viewLifecycleOwner, {
            setTitleInfo(it)
            titleInfo = it
        })

        viewModel.focusedButton.observe(viewLifecycleOwner, {
            when (it) {
                TvTitleDetailsViewModel.Buttons.CONTINUE_WATCHING -> binding.continueButton.requestFocus()
                TvTitleDetailsViewModel.Buttons.FAVORITES -> {
                    if (binding.buttonsRow.isGone) {
                        binding.favoriteContainer.requestFocus()
                    }
                }
                else -> binding.playButton.requestFocus()
            }
        })

        viewModel.favoriteLoader.observe(viewLifecycleOwner, {
            binding.favoriteProgressBar.setVisibleOrGone(it == LoadingState.LOADING)
            binding.favoriteIcon.setVisibleOrGone(it != LoadingState.LOADING)
        })

        viewModel.addToFavorites.observe(viewLifecycleOwner, {
            favoriteContainer(it)
        })

        viewModel.continueWatchingDetails.observe(viewLifecycleOwner, {
            checkContinueWatching(it)
            if (it != null) {
                viewModel.setFocusedButton(TvTitleDetailsViewModel.Buttons.CONTINUE_WATCHING)
            }
        })

        viewModel.hideContinueWatchingLoader.observe(viewLifecycleOwner, {
            when (it) {
                LoadingState.LOADING -> {}
                LoadingState.LOADED -> {
                    restartFragment()
                }
                LoadingState.ERROR -> {
                    viewModel.newToastMessage(getString(R.string.unable_to_hide_from_list))
                }
            }
        })

        viewModel.availableLanguages.observe(viewLifecycleOwner, {
            languages = it.reversed()
        })
    }

    private fun favoriteContainer(isSaved: Boolean) {
        if (isSaved) {
            binding.favoriteIcon.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.icon_favorite_full, null))
            binding.favoriteIcon.setColorFilter(ContextCompat.getColor(requireContext(), R.color.accent_color))
            binding.favoriteContainer.setOnClickListener {
                viewModel.deleteWatchlistTitle(titleId, fromWatchlist)
            }
        } else {
            binding.favoriteIcon.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.icon_favorite, null))
            binding.favoriteIcon.setColorFilter(ContextCompat.getColor(requireContext(), R.color.general_text_color))
            binding.favoriteContainer.setOnClickListener {
                viewModel.addWatchlistTitle(titleId)
            }
        }
    }

    private fun setTitleInfo(info: SingleTitleModel) {
        binding.trailerButton.setOnClickListener {
            playTitleTrailer(info.trailer)
        }

        binding.backgroundPoster.setImage(info.cover, false)

        binding.titleName.text = info.nameEng
        binding.year.text = info.releaseYear
        binding.imdbScore.text = getString(R.string.imdb_score, info.imdbScore)
        binding.country.text = info.country
        binding.duration.text = if (info.isTvShow) getString(R.string.season_number, info.seasonNum.toString()) else info.duration
        binding.titleDescription.text = info.description

        info.visibility?.let { binding.deleteButton.setVisibleOrGone(it) }
    }

    private fun checkContinueWatching(info: ContinueWatchingRoom?) {
        if (sharedPreferences.getLoginToken() == "") {
            binding.deleteButton.setVisibleOrGone(info != null)
        }
        binding.continueButton.setVisibleOrGone(info != null)

        if (info != null) {
            binding.continueButton.setOnClickListener {
                continueTitlePlay(ContinueWatchingRoom(
                    info.titleId,
                    info.language,
                    TimeUnit.SECONDS.toMillis(info.watchedDuration),
                    TimeUnit.SECONDS.toMillis(info.titleDuration),
                    info.isTvShow,
                    info.season,
                    info.episode,
                ))
            }

            binding.continueButton.text = getString(R.string.continue_watching_button,
                    if (info.isTvShow) {
                        info.watchedDuration.titlePosition(info.season, info.episode)
                    } else {
                        info.watchedDuration.titlePosition(null, null)
                    })

            binding.playButton.text = getString(R.string.start_over)

            if (continueWatching && !startedWatching) {
                binding.continueButton.callOnClick()
                startedWatching = true
            }
        }
    }

    private fun restartFragment() {
        sharedPreferences.saveRefreshContinueWatching(true)

        val intent = Intent(requireContext(), TvSingleTitleActivity::class.java).apply {
            putExtra(AppConstants.TITLE_ID, titleId)
            putExtra(AppConstants.IS_TV_SHOW, isTvShow)
            putExtra(AppConstants.FROM_WATCHLIST, fromWatchlist)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        startActivity(intent)
    }

    private fun languagePickerDialog() {
        val binding = DialogChooseLanguageBinding.inflate(LayoutInflater.from(requireContext()))
        val chooseLanguageDialog = Dialog(requireContext())
        chooseLanguageDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        chooseLanguageDialog.setContentView(binding.root)
        chooseLanguageDialog.show()

        val chooseLanguageLayout = GridLayoutManager(requireActivity(), 1, GridLayoutManager.HORIZONTAL, false)
        chooseLanguageAdapter = ChooseLanguageAdapter(requireContext()) {
            chooseLanguageDialog.hide()
            playTitleFromStart(titleId, isTvShow, it)
        }
        binding.rvChooseLanguage.apply {
            adapter = chooseLanguageAdapter
            layoutManager = chooseLanguageLayout
        }

        chooseLanguageAdapter.setLanguageList(languages)
    }

    private fun removeTitleDialog() {
        val binding = DialogRemoveTitleBinding.inflate(LayoutInflater.from(requireContext()))
        val removeTitle = Dialog(requireContext())
        removeTitle.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        removeTitle.setContentView(binding.root)

        if (sharedPreferences.getLoginToken() != "") {
            binding.title.text = resources.getString(R.string.remove_from_list_title)
        }

        binding.continueButton.setOnClickListener {
            if (sharedPreferences.getLoginToken() == "") {
                viewModel.deleteSingleContinueWatchingFromRoom(titleId)
            } else {
                viewModel.hideSingleContinueWatching(titleId)
            }
        }
        binding.cancelButton.setOnClickListener {
            removeTitle.dismiss()
        }
        removeTitle.show()
        binding.continueButton.requestFocus()
    }

    private fun playTitleTrailer(trailerUrl: String?) {
        if (trailerUrl != null) {
            val intent = Intent(context, TvVideoPlayerActivity::class.java)
            intent.putExtra(
                AppConstants.VIDEO_PLAYER_DATA, VideoPlayerData(
                titleId,
                isTvShow,
                0,
                "ENG",
                0,
                0L,
                trailerUrl
            )
            )
            activity?.startActivity(intent)
        } else {
            viewModel.newToastMessage(getString(R.string.no_trailer_found))
        }
    }

    private fun playTitleFromStart(titleId: Int, isTvShow: Boolean, chosenLanguage: String) {
        val intent = Intent(context, TvVideoPlayerActivity::class.java)
        intent.putExtra(
            AppConstants.VIDEO_PLAYER_DATA, VideoPlayerData(
            titleId,
            isTvShow,
            if (isTvShow) 1 else 0,
            chosenLanguage,
            if (isTvShow) 1 else 0,
            0L,
            null
        ))
        activity?.startActivity(intent)
    }

    private fun continueTitlePlay(item: ContinueWatchingRoom) {
        val intent = Intent(context, TvVideoPlayerActivity::class.java)
        intent.putExtra(
            AppConstants.VIDEO_PLAYER_DATA, VideoPlayerData(
            item.titleId,
            item.isTvShow,
            item.season,
            item.language,
            item.episode,
            item.watchedDuration,
            null
        ))
        activity?.startActivity(intent)
    }
}