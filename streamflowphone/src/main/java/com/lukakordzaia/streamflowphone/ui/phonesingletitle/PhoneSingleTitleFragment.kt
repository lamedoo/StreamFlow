package com.lukakordzaia.streamflowphone.ui.phonesingletitle

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.lukakordzaia.core.adapters.ChooseLanguageAdapter
import com.lukakordzaia.core.database.continuewatchingdb.ContinueWatchingRoom
import com.lukakordzaia.core.databinding.DialogChooseLanguageBinding
import com.lukakordzaia.core.domain.domainmodels.SingleTitleModel
import com.lukakordzaia.core.domain.domainmodels.VideoPlayerData
import com.lukakordzaia.core.network.LoadingState
import com.lukakordzaia.core.utils.setImage
import com.lukakordzaia.core.utils.setVisible
import com.lukakordzaia.core.utils.setVisibleOrGone
import com.lukakordzaia.core.utils.titlePosition
import com.lukakordzaia.streamflowphone.R
import com.lukakordzaia.streamflowphone.databinding.FragmentPhoneSingleTitleOldBinding
import com.lukakordzaia.streamflowphone.ui.baseclasses.BaseFragmentPhoneVM
import com.lukakordzaia.streamflowphone.ui.videoplayer.VideoPlayerActivity
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.concurrent.TimeUnit

class PhoneSingleTitleFragment : BaseFragmentPhoneVM<FragmentPhoneSingleTitleOldBinding, PhoneSingleTitleViewModel>() {
    private val args: PhoneSingleTitleFragmentArgs by navArgs()

    override val viewModel by viewModel<PhoneSingleTitleViewModel>()
    override val reload: () -> Unit = { viewModel.fetchContent(args.titleId) }

    private lateinit var titleInfo: SingleTitleModel
    private lateinit var chooseLanguageAdapter: ChooseLanguageAdapter
    private lateinit var phoneSingleTitleCastAdapter: PhoneSingleTitleCastAdapter
    private lateinit var phoneSingleTitleRelatedAdapter: PhoneSingleTitleRelatedAdapter

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentPhoneSingleTitleOldBinding
        get() = FragmentPhoneSingleTitleOldBinding::inflate

    private var languages: List<String> = emptyList()

    override fun onStart() {
        super.onStart()
        viewModel.fetchContent(args.titleId)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragmentSetUi()
        fragmentListeners()
        fragmentObservers()
    }

    private fun fragmentSetUi() {
//        binding.singleTitleAppbar.setExpanded(true)
        castContainer()
        relatedContainer()
    }

    private fun fragmentListeners() {
        binding.playButton.setOnClickListener {
            languagePickerDialog()
        }

        binding.singleTitleBackButton.setOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.replayButton.setOnClickListener {
            languagePickerDialog()
        }
    }

    private fun fragmentObservers() {
        viewModel.generalLoader.observe(viewLifecycleOwner) {
            binding.progressBar.setVisibleOrGone(it == LoadingState.LOADING)
            binding.singleTitleMainContainer.setVisibleOrGone(it == LoadingState.LOADED)
        }

        viewModel.favoriteLoader.observe(viewLifecycleOwner) {
            binding.singleTitleFavoriteProgressBar.setVisibleOrGone(it == LoadingState.LOADING)
            binding.singleTitleFavoriteIcon.setVisibleOrGone(it != LoadingState.LOADING)
        }

        viewModel.addToFavorites.observe(viewLifecycleOwner) {
            checkFavorites(it)
        }

        viewModel.singleTitleData.observe(viewLifecycleOwner) {
            titleDetailsContainer(it)
            titleInfo = it
        }

        viewModel.titleGenres.observe(viewLifecycleOwner) {
            binding.titleDescription.titleGenre.text = TextUtils.join(", ", it)
        }

        viewModel.titleDirector.observe(viewLifecycleOwner) {
            binding.titleDescription.titleDirector.text = it.originalName
        }

        viewModel.castData.observe(viewLifecycleOwner) {
            phoneSingleTitleCastAdapter.setCastList(it)
        }

        viewModel.singleTitleRelated.observe(viewLifecycleOwner) {
            phoneSingleTitleRelatedAdapter.setRelatedList(it)
        }

        viewModel.continueWatchingDetails.observe(viewLifecycleOwner) {
            checkContinueWatching(it)
        }

        viewModel.availableLanguages.observe(viewLifecycleOwner) { languageList ->
            languages = languageList.reversed()
        }

        viewModel.movieNotYetAdded.observe(viewLifecycleOwner) {
            binding.playButton.setVisibleOrGone(!it)
            binding.noFilesContainer.setVisibleOrGone(it)
        }
    }

    private fun checkFavorites(isFavorite: Boolean) {
        if (isFavorite) {
            binding.singleTitleFavoriteIcon.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.icon_favorite_full, null))
            binding.singleTitleFavoriteIcon.setColorFilter(ContextCompat.getColor(requireContext(), R.color.accent_color))
            binding.singleTitleFavorite.setOnClickListener {
                viewModel.deleteWatchlistTitle(args.titleId)
            }
        } else {
            binding.singleTitleFavoriteIcon.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.icon_favorite, null))
            binding.singleTitleFavoriteIcon.setColorFilter(ContextCompat.getColor(requireContext(), R.color.general_text_color))
            binding.singleTitleFavorite.setOnClickListener {
                viewModel.addWatchlistTitle(args.titleId)
            }
        }
    }

    private fun titleDetailsContainer(info: SingleTitleModel) {
        binding.titleDescription.titleName.text = info.displayName

        binding.singleTitleCover.setImage(info.cover, false)

        binding.titleTrailer.setOnClickListener {
            startTrailer(info)
        }

        binding.titleDescription.titleDescription.text = info.description
        binding.infoDetails.imdbScore.text = getString(R.string.imdb_score, info.imdbScore)
        binding.infoDetails.year.text = info.releaseYear
        binding.infoDetails.duration.text = if (info.isTvShow) getString(R.string.season_number, info.seasonNum.toString()) else info.duration

        binding.episodesButton.setVisibleOrGone(info.isTvShow)
        if (info.isTvShow) {
            binding.episodesButton.setOnClickListener {
                viewModel.onEpisodesPressed(info.id, info.displayName!!, info.seasonNum!!)
            }
        }
    }

    private fun checkContinueWatching(info: ContinueWatchingRoom?) {
        binding.continueWatchingSeekBar.setVisibleOrGone(info != null)

        if (info != null) {
            binding.continueWatchingSeekBar.max = info.titleDuration.toInt()
            binding.continueWatchingSeekBar.progress = info.watchedDuration.toInt()

            val time = if (info.isTvShow) {
                info.watchedDuration.titlePosition(info.season, info.episode)
            } else {
                info.watchedDuration.titlePosition(null, null)
            }
            binding.continueWatchingInfo.text = time

            if (!info.isTvShow) {
                binding.replayButton.setVisible()
            }

            binding.playButton.setOnClickListener {
                startVideoPlayer(
                    ContinueWatchingRoom(
                        info.titleId,
                        info.language,
                        TimeUnit.SECONDS.toMillis(info.watchedDuration),
                        TimeUnit.SECONDS.toMillis(info.titleDuration),
                        info.isTvShow,
                        info.season,
                        info.episode
                    ), null)
            }
        }
    }

    private fun castContainer() {
        val castLayout = GridLayoutManager(requireActivity(), 2, GridLayoutManager.HORIZONTAL, false)
        phoneSingleTitleCastAdapter = PhoneSingleTitleCastAdapter {
            viewModel.newToastMessage(it)
        }
        binding.singleTitleCastSimilar.rvSingleTitleCast.layoutManager = castLayout
        binding.singleTitleCastSimilar.rvSingleTitleCast.adapter = phoneSingleTitleCastAdapter
    }

    private fun relatedContainer() {
        val relatedLayout = GridLayoutManager(requireActivity(), 2, GridLayoutManager.HORIZONTAL, false)
        phoneSingleTitleRelatedAdapter = PhoneSingleTitleRelatedAdapter {
            viewModel.onRelatedTitlePressed(it)
        }
        binding.singleTitleCastSimilar.rvSingleTitleRelated.layoutManager = relatedLayout
        binding.singleTitleCastSimilar.rvSingleTitleRelated.adapter = phoneSingleTitleRelatedAdapter
    }

    private fun languagePickerDialog() {
        val binding = DialogChooseLanguageBinding.inflate(LayoutInflater.from(requireContext()))
        val chooseLanguageDialog = Dialog(requireContext())
        chooseLanguageDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        chooseLanguageDialog.window?.setDimAmount(0.6F)
        chooseLanguageDialog.setContentView(binding.root)
        chooseLanguageDialog.show()

        val chooseLanguageLayout = GridLayoutManager(requireActivity(), 1, GridLayoutManager.HORIZONTAL, false)
        chooseLanguageAdapter = ChooseLanguageAdapter { language ->
            chooseLanguageDialog.hide()
            startVideoPlayer(null, language)
        }
        binding.rvChooseLanguage.apply {
            adapter = chooseLanguageAdapter
            layoutManager = chooseLanguageLayout
        }

        chooseLanguageAdapter.setLanguageList(languages)
    }

    private fun startTrailer(data: SingleTitleModel) {
        if (data.trailer != null) {
            requireActivity().startActivity(
                VideoPlayerActivity.startFromTrailers(requireContext(), VideoPlayerData(
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
            viewModel.newToastMessage(getString(R.string.no_trailer_found))
        }
    }

    private fun startVideoPlayer(contWatching: ContinueWatchingRoom?, language: String?) {
        requireActivity().startActivity(
            VideoPlayerActivity.startFromSingleTitle(requireContext(), VideoPlayerData(
                contWatching?.titleId ?: titleInfo.id,
                contWatching?.isTvShow ?: titleInfo.isTvShow,
                contWatching?.season ?: if (titleInfo.isTvShow) 1 else 0,
                contWatching?.language ?: language!!,
                contWatching?.episode ?: if (titleInfo.isTvShow) 1 else 0,
                contWatching?.watchedDuration ?: 0L,
                null
            )
            )
        )
    }

    override fun onDestroyView() {
        with(binding.singleTitleCastSimilar) {
            rvSingleTitleCast.adapter = null
            rvSingleTitleRelated.adapter = null
        }
        super.onDestroyView()
    }
}