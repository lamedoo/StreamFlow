package com.lukakordzaia.streamflowtv.ui.tvvideoplayer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerView
import com.lukakordzaia.core.adapters.ChooseVideoAudioAdapter
import com.lukakordzaia.core.baseclasses.BaseVideoPlayerFragment
import com.lukakordzaia.core.databinding.ContinueWatchingDialogBinding
import com.lukakordzaia.core.utils.EventObserver
import com.lukakordzaia.core.utils.setGone
import com.lukakordzaia.core.utils.setVisible
import com.lukakordzaia.streamflowtv.R
import com.lukakordzaia.streamflowtv.databinding.FragmentTvVideoPlayerBinding
import com.lukakordzaia.streamflowtv.databinding.TvExoplayerControllerLayoutBinding
import com.lukakordzaia.streamflowtv.ui.tvvideoplayer.TvVideoPlayerActivity.Companion.VIDEO_DETAILS

class TvVideoPlayerFragment : BaseVideoPlayerFragment<FragmentTvVideoPlayerBinding>() {
    private lateinit var playerBinding: TvExoplayerControllerLayoutBinding
    override val reload: () -> Unit = {
        viewModel.getTitleFiles(videoPlayerData)
        viewModel.getSingleTitleData(videoPlayerData.titleId)
    }

    override val autoBackPress = AutoBackPress {
        (activity as TvVideoPlayerActivity).setCurrentFragmentState(TvVideoPlayerActivity.BACK_BUTTON)
        requireActivity().onBackPressed()
    }

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentTvVideoPlayerBinding
        get() = FragmentTvVideoPlayerBinding::inflate

    override val playerView: PlayerView
        get() = binding.titlePlayer

    override val subtitleButton: ImageButton
        get() = playerBinding.subtitleToggle

    override val playerTitle: TextView
        get() = playerBinding.playerTitle

    override val nextButton: ImageButton
        get() = playerBinding.nextEpisode

    override val exoDuration: TextView
        get() = playerBinding.exoDuration

    override val continueWatchingDialog: ContinueWatchingDialogBinding
        get() = binding.continueWatching

    private var isBuffering = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        playerBinding = TvExoplayerControllerLayoutBinding.bind(binding.root)

        if (!videoPlayerData.isTvShow) {
            playerBinding.nextDetailsTitle.setGone()
        }

        fragmentListeners()
        fragmentObservers()

        mediaPlayer.setPlayerListener(PlayerListeners())

        (activity as TvVideoPlayerActivity).setVideoPlayerFragment()
    }

    private fun fragmentListeners() {
        playerBinding.subtitleToggle.setOnClickListener {
            audioObservers()
            binding.chooseAudioSidebar.root.setVisible()
            binding.chooseAudioSidebar.rvSubtitles.requestFocus()

            binding.titlePlayer.player?.pause()
            (requireActivity() as TvVideoPlayerActivity).setCurrentFragmentState(TvVideoPlayerActivity.AUDIO_SIDEBAR)
        }

        playerBinding.backButton.setOnClickListener {
            (requireActivity() as TvVideoPlayerActivity).setCurrentFragmentState(TvVideoPlayerActivity.BACK_BUTTON)
            requireActivity().onBackPressed()
        }
    }

    private fun fragmentObservers() {
        viewModel.newEpisodeStarted.observe(viewLifecycleOwner, EventObserver {
            if (it) {
                playerBinding.exoPlay.requestFocus()
                playerBinding.exoPause.requestFocus()
            }
        })
    }

    private fun audioObservers() {
        viewModel.availableSubtitles.observe(viewLifecycleOwner) {
            setAvailableSubtitles(it)
        }

        viewModel.availableLanguages.observe(viewLifecycleOwner) {
            setAvailableLanguages(it)
        }
    }

    private fun setAvailableSubtitles(subtitles: List<String>) {
        val layout = LinearLayoutManager(requireActivity(), GridLayoutManager.VERTICAL, false)
        val chooseSubtitlesAdapter = ChooseVideoAudioAdapter {
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
        val chooseLanguageAdapter = ChooseVideoAudioAdapter {
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
        playerBinding.subtitleToggle.requestFocus()
        (requireActivity() as TvVideoPlayerActivity).setCurrentFragmentState(TvVideoPlayerActivity.VIDEO_PLAYER)
    }

    inner class PlayerListeners: Player.Listener {
        override fun onPlaybackStateChanged(state: Int) {
            super.onPlaybackStateChanged(state)

            if (state == Player.STATE_READY) {
                if (continueWatchingDialog.root.isVisible) {
                    continueWatchingDialog.confirmButton.requestFocus()
                }
            }

            isBuffering = state == Player.STATE_BUFFERING
        }
    }

    fun onKeyUp(): Boolean {
        return if (binding.continueWatching.root.isVisible) {
            binding.continueWatching.confirmButton.requestFocus()
            true
        } else {
            return when {
                !binding.titlePlayer.isControllerVisible -> {
                    binding.titlePlayer.showController()
                    playerBinding.exoPause.requestFocus()
                    true
                }
                playerBinding.nextEpisode.isVisible -> {
                    playerBinding.exoPause.nextFocusUpId = R.id.next_episode
                    playerBinding.exoPlay.nextFocusUpId = R.id.next_episode
                    true
                }
                playerBinding.subtitleToggle.isVisible -> {
                    playerBinding.exoPause.nextFocusUpId = R.id.subtitle_toggle
                    playerBinding.exoPlay.nextFocusUpId = R.id.subtitle_toggle
                    true
                }
                else -> {
                    playerBinding.exoPause.nextFocusUpId = R.id.back_button
                    playerBinding.exoPlay.nextFocusUpId = R.id.back_button
                    true
                }
            }
        }
    }

    fun onKeyCenter() {
        if (!binding.titlePlayer.isControllerVisible) {
            binding.titlePlayer.showController()
            binding.titlePlayer.player!!.pause()
        } else if (!binding.titlePlayer.player!!.isPlaying && !binding.titlePlayer.isControllerVisible) {
            binding.titlePlayer.player!!.play()
        }
    }

    fun onKeyLeft() {
        if (!binding.continueWatching.root.isVisible) {
            when {
                !binding.titlePlayer.isControllerVisible -> {
                    binding.titlePlayer.showController()
                    playerBinding.exoRew.callOnClick()
                }
                playerBinding.nextEpisode.isFocused ->
                    if (playerBinding.subtitleToggle.isVisible) playerBinding.subtitleToggle.requestFocus() else playerBinding.backButton.requestFocus()
                playerBinding.subtitleToggle.isFocused -> playerBinding.backButton.requestFocus()
                else -> playerBinding.exoRew.callOnClick()
            }
        }
    }

    fun onKeyRight() {
        if (!binding.continueWatching.root.isVisible) {
            when {
                !binding.titlePlayer.isControllerVisible -> {
                    binding.titlePlayer.showController()
                    playerBinding.exoFfwd.callOnClick()
                }
                playerBinding.backButton.isFocused ->
                    if (playerBinding.subtitleToggle.isVisible) playerBinding.subtitleToggle.requestFocus() else {
                        if (playerBinding.nextEpisode.isVisible) playerBinding.nextEpisode.requestFocus() else playerBinding.exoFfwd.callOnClick()
                    }
                playerBinding.subtitleToggle.isFocused ->
                    if (playerBinding.nextEpisode.isVisible) playerBinding.nextEpisode.requestFocus() else playerBinding.exoFfwd.callOnClick()
                else -> playerBinding.exoFfwd.callOnClick()
            }
        }
    }

    fun onKeyDown() {
        if (binding.continueWatching.root.isVisible) {
            binding.continueWatching.goBackButton.requestFocus()
        } else {
            when {
                !binding.titlePlayer.isControllerVisible -> {
                    binding.titlePlayer.showController()
                    playerBinding.exoPause.requestFocus()
                }
                playerBinding.exoPlay.isFocused || playerBinding.exoPause.isFocused -> {
                    if (videoPlayerData.isTvShow && !isBuffering) {
                        (activity as TvVideoPlayerActivity).setCurrentFragmentState(VIDEO_DETAILS)
                        binding.titlePlayer.player!!.pause()
                        binding.titlePlayer.hideController()
                        saveCurrentProgress()

                        if (videoPlayerData.trailerUrl == null) {
                            (activity as TvVideoPlayerActivity).showDetails()
                        }
                    }
                }
                else -> {
                    playerBinding.subtitleToggle.nextFocusDownId = if (binding.titlePlayer.player!!.isPlaying) R.id.exo_pause else R.id.exo_play
                    playerBinding.nextEpisode.nextFocusDownId = if (binding.titlePlayer.player!!.isPlaying) R.id.exo_pause else R.id.exo_play
                }
            }
        }
    }

    // Need this function to access from activity
    fun getPlayer(): PlayerView = binding.titlePlayer
    fun focusOnPause() = playerBinding.exoPause.requestFocus()
    fun checkContinueWatchingVisibility(): Boolean = binding.continueWatching.root.isVisible
}