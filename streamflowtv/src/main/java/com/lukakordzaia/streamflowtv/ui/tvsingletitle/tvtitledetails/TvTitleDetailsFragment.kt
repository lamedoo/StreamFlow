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
import androidx.recyclerview.widget.GridLayoutManager
import com.lukakordzaia.core.adapters.ChooseLanguageAdapter
import com.lukakordzaia.core.baseclasses.BaseFragmentVM
import com.lukakordzaia.core.database.continuewatchingdb.ContinueWatchingRoom
import com.lukakordzaia.core.databinding.DialogChooseLanguageBinding
import com.lukakordzaia.core.domain.domainmodels.SingleTitleModel
import com.lukakordzaia.core.domain.domainmodels.VideoPlayerData
import com.lukakordzaia.core.network.LoadingState
import com.lukakordzaia.core.utils.*
import com.lukakordzaia.streamflowtv.R
import com.lukakordzaia.streamflowtv.databinding.FragmentTvTitleDetailsBinding
import com.lukakordzaia.streamflowtv.ui.login.TvLoginActivity
import com.lukakordzaia.streamflowtv.ui.tvsingletitle.TvSingleTitleActivity
import com.lukakordzaia.streamflowtv.ui.tvsingletitle.tvepisodes.TvEpisodesActivity
import com.lukakordzaia.streamflowtv.ui.tvsingletitle.tvtitlerelated.TvRelatedFragment
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

    private var languages: List<String> = emptyList()
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

        callData()
        fragmentListeners()
        fragmentObservers()
    }

    override fun onStart() {
        super.onStart()
        viewModel.getContinueWatching(titleId)
    }

    private fun callData() {
        viewModel.getSingleTitleData(titleId)
        viewModel.getSingleTitleFiles(titleId)
    }

    private fun fragmentListeners() {
        binding.playButton.setOnClickListener {
            languagePickerDialog()
        }

        binding.replayButton.setOnClickListener {
            languagePickerDialog()
        }

        binding.deleteButton.setOnClickListener {
            removeTitleDialog()
        }

        binding.episodesButton.setOnClickListener {
            startActivity(TvEpisodesActivity.newIntent(requireContext(), titleId))
        }

        binding.nextDetails.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                (requireActivity() as TvSingleTitleActivity).setCurrentFragment(TvRelatedFragment())
            }
            this.hasFocus = hasFocus
        }
    }

    private fun fragmentObservers() {
        viewModel.generalLoader.observe(viewLifecycleOwner) {
            binding.tvDetailsProgressBar.setVisibleOrGone(it == LoadingState.LOADING)
        }

        viewModel.movieNotYetAdded.observe(viewLifecycleOwner) {
            binding.noFilesContainer.setVisibleOrGone(it)
            binding.buttonsRow.setVisibleOrInvisible(!it)
        }

        viewModel.getSingleTitleResponse.observe(viewLifecycleOwner) {
            setTitleInfo(it)
        }

        viewModel.favoriteLoader.observe(viewLifecycleOwner) {
            binding.favoriteProgressBar.setVisibleOrGone(it == LoadingState.LOADING)
            binding.favoriteIcon.setVisibleOrGone(it != LoadingState.LOADING)
        }

        viewModel.addToFavorites.observe(viewLifecycleOwner) {
            favoriteContainer(it)
        }

        viewModel.continueWatchingDetails.observe(viewLifecycleOwner) {
            checkContinueWatching(it)
        }

        viewModel.hideContinueWatchingLoader.observe(viewLifecycleOwner) {
            when (it) {
                LoadingState.LOADING -> {}
                LoadingState.LOADED -> {
                    restartFragment()
                }
                LoadingState.ERROR -> {
                    viewModel.newToastMessage(getString(R.string.unable_to_hide_from_list))
                }
            }
        }

        viewModel.availableLanguages.observe(viewLifecycleOwner) {
            languages = it.reversed()
        }
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
                if (sharedPreferences.getLoginToken() == "") {
                    context.createToast(getString(R.string.log_in_to_see_watchlist))
                    startActivity(Intent(requireActivity(), TvLoginActivity::class.java))
                } else {
                    viewModel.addWatchlistTitle(titleId)
                }
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
        binding.episodesButton.setVisibleOrGone(info.isTvShow)
    }

    private fun checkContinueWatching(info: ContinueWatchingRoom?) {
        binding.continueWatchingInfo.setVisibleOrGone(info != null)
        binding.continueWatchingSeekBar.setVisibleOrGone(info != null)

        if (sharedPreferences.getLoginToken() == "") {
            binding.deleteButton.setVisibleOrGone(info != null)
        }

        if (info != null) {
            binding.continueWatchingSeekBar.max = info.titleDuration.toInt()
            binding.continueWatchingSeekBar.progress = info.watchedDuration.toInt()

            val time = if (info.isTvShow) {
                info.watchedDuration.titlePosition(info.season, info.episode)
            } else {
                info.watchedDuration.titlePosition(null, null)
            }
            binding.continueWatchingInfo.text = time

            binding.replayButton.setVisibleOrGone(!info.isTvShow)

            binding.playButton.setOnClickListener {
                continueTitlePlay(
                    ContinueWatchingRoom(
                        info.titleId,
                        info.language,
                        TimeUnit.SECONDS.toMillis(info.watchedDuration),
                        TimeUnit.SECONDS.toMillis(info.titleDuration),
                        info.isTvShow,
                        info.season,
                        info.episode,
                    )
                )
            }

            if (continueWatching && !startedWatching) {
                binding.playButton.callOnClick()
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
        chooseLanguageDialog.window?.setDimAmount(0.6F)
        chooseLanguageDialog.setContentView(binding.root)
        chooseLanguageDialog.show()

        val chooseLanguageLayout = GridLayoutManager(requireActivity(), 1, GridLayoutManager.HORIZONTAL, false)
        val chooseLanguageAdapter = ChooseLanguageAdapter {
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
        DialogUtils.generalAlertDialog(
            requireContext(),
            R.string.remove_from_list_title,
            R.drawable.icon_remove
        ) {
            if (sharedPreferences.getLoginToken() == "") {
                viewModel.deleteSingleContinueWatchingFromRoom(titleId)
            } else {
                viewModel.hideSingleContinueWatching(titleId)
            }
        }
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
            )
        )
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
            )
        )
        activity?.startActivity(intent)
    }
}