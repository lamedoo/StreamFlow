package com.lukakordzaia.streamflowphone.ui.videoplayer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.exoplayer2.ui.PlayerView
import com.lukakordzaia.core.adapters.ChooseVideoAudioAdapter
import com.lukakordzaia.core.baseclasses.BaseVideoPlayerFragment
import com.lukakordzaia.core.databinding.ContinueWatchingDialogBinding
import com.lukakordzaia.core.domain.domainmodels.VideoPlayerData
import com.lukakordzaia.core.utils.setGone
import com.lukakordzaia.core.utils.setVisible
import com.lukakordzaia.core.utils.setVisibleOrGone
import com.lukakordzaia.streamflowphone.databinding.FragmentPhoneVideoPlayerBinding
import com.lukakordzaia.streamflowphone.databinding.PhoneExoplayerControllerLayoutBinding
import com.lukakordzaia.streamflowphone.ui.videoplayer.VideoPlayerActivity.Companion.AUDIO_SIDEBAR
import com.lukakordzaia.streamflowphone.ui.videoplayer.VideoPlayerActivity.Companion.VIDEO_PLAYER

class VideoPlayerFragment : BaseVideoPlayerFragment<FragmentPhoneVideoPlayerBinding>() {
    private lateinit var playerBinding: PhoneExoplayerControllerLayoutBinding
    override val reload: () -> Unit = {
        viewModel.getTitleFiles(videoPlayerData)
        viewModel.getSingleTitleData(videoPlayerData.titleId)
    }

    override val autoBackPress = AutoBackPress { requireActivity().onBackPressed() }

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentPhoneVideoPlayerBinding
        get() = FragmentPhoneVideoPlayerBinding::inflate

    override val playerView: PlayerView
        get() = binding.titlePlayer

    override val subtitleButton: ImageButton
        get() = playerBinding.subtitleToggle

    override val playerTitle: TextView
        get() = playerBinding.playerTitle

    override val nextButton: ImageButton
        get() = playerBinding.nextEpisode

    override val exoDuration: TextView
        get() = playerBinding.duration

    override val continueWatchingDialog: ContinueWatchingDialogBinding
        get() = binding.continueWatching

    private lateinit var chooseSubtitlesAdapter: ChooseVideoAudioAdapter
    private lateinit var chooseLanguageAdapter: ChooseVideoAudioAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        playerBinding = PhoneExoplayerControllerLayoutBinding.bind(binding.root)

        fragmentListeners()
    }

    private fun fragmentListeners() {
        playerBinding.subtitleToggle.setOnClickListener {
            audioObservers()
            binding.chooseAudioSidebar.root.setVisible()
            binding.chooseAudioSidebar.rvSubtitles.requestFocus()

            binding.titlePlayer.player?.pause()
            (requireActivity() as VideoPlayerActivity).setCurrentFragment(AUDIO_SIDEBAR)
        }

        playerBinding.backButton.setOnClickListener {
            requireActivity().onBackPressed()
        }

        prevButtonClickListener()

        binding.chooseAudioSidebar.backButton.setOnClickListener {
            binding.chooseAudioSidebar.root.setGone()
        }
    }

    private fun audioObservers() {
        viewModel.availableSubtitles.observe(viewLifecycleOwner, {
            setAvailableSubtitles(it)
        })

        viewModel.availableLanguages.observe(viewLifecycleOwner, {
            setAvailableLanguages(it)
        })
    }

    private fun setAvailableSubtitles(subtitles: List<String>) {
        val layout = LinearLayoutManager(requireActivity(), GridLayoutManager.VERTICAL, false)
        chooseSubtitlesAdapter = ChooseVideoAudioAdapter {
            hideAudioSidebar()
            switchSubtitleLanguage(it)
        }

        binding.chooseAudioSidebar.rvSubtitles.apply {
            layoutManager = layout
            adapter = chooseSubtitlesAdapter
        }

        chooseSubtitlesAdapter.setItems(subtitles)
        videoPlayerData.chosenSubtitle?.let { chooseSubtitlesAdapter.setCurrentItem(it) }
    }

    private fun setAvailableLanguages(languages: List<String>) {
        val layout = LinearLayoutManager(requireActivity(), GridLayoutManager.VERTICAL, false)
        chooseLanguageAdapter = ChooseVideoAudioAdapter {
            hideAudioSidebar()
            switchAudioLanguage(it)
        }

        binding.chooseAudioSidebar.rvLanguage.apply {
            layoutManager = layout
            adapter = chooseLanguageAdapter
        }

        chooseLanguageAdapter.setCurrentItem(videoPlayerData.chosenLanguage)
        chooseLanguageAdapter.setItems(languages)
    }

    fun hideAudioSidebar() {
        binding.chooseAudioSidebar.root.setGone()
        (requireActivity() as VideoPlayerActivity).setCurrentFragment(VIDEO_PLAYER)
    }

    private fun prevButtonClickListener() {
        val prevButton = playerBinding.prevEpisode

        playerBinding.prevEpisode.setVisibleOrGone(videoPlayerData.trailerUrl == null && videoPlayerData.isTvShow)

        if (videoPlayerData.chosenEpisode == 1) {
            prevButton.setOnClickListener {
                mediaItemsPlayed = 0
                playerBinding.exoPrev.callOnClick()
            }
        } else {
            prevButton.setOnClickListener {
                mediaItemsPlayed = 0
                viewModel.setVideoPlayerData(VideoPlayerData(
                    videoPlayerData.titleId,
                    videoPlayerData.isTvShow,
                    videoPlayerData.chosenSeason,
                    videoPlayerData.chosenLanguage,
                    videoPlayerData.chosenEpisode - 1,
                    0L,
                    null,
                    videoPlayerData.chosenSubtitle,
                )
                )
                mediaPlayer.initPlayer(binding.titlePlayer, 0, 0L)
            }
        }
    }

    override fun onDestroyView() {
        with(binding.chooseAudioSidebar) {
            rvSubtitles.adapter = null
            rvLanguage.adapter = null
        }
        super.onDestroyView()
    }
}