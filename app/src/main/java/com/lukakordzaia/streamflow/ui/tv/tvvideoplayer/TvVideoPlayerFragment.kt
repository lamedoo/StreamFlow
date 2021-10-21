package com.lukakordzaia.streamflow.ui.tv.tvvideoplayer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.util.Util
import com.lukakordzaia.streamflow.R
import com.lukakordzaia.streamflow.databinding.FragmentTvVideoPlayerBinding
import com.lukakordzaia.streamflow.databinding.TvExoplayerControllerLayoutBinding
import com.lukakordzaia.streamflow.ui.baseclasses.fragments.BaseVideoPlayerFragment
import kotlinx.android.synthetic.main.tv_exoplayer_controller_layout.*


class TvVideoPlayerFragment : BaseVideoPlayerFragment<FragmentTvVideoPlayerBinding>() {
    private lateinit var playerBinding: TvExoplayerControllerLayoutBinding
    override val reload: () -> Unit = {
        viewModel.getTitleFiles(videoPlayerData)
        viewModel.getSingleTitleData(videoPlayerData.titleId)
    }

    override val autoBackPress = AutoBackPress {
        (requireActivity() as TvVideoPlayerActivity).setCurrentFragment(TvVideoPlayerActivity.BACK_BUTTON)
        requireActivity().onBackPressed()
    }

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentTvVideoPlayerBinding
        get() = FragmentTvVideoPlayerBinding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        playerBinding = TvExoplayerControllerLayoutBinding.bind(binding.root)

        if (!videoPlayerData.isTvShow) {
            playerBinding.nextDetailsTitle.text = "სხვა დეტალები"
        }

        mediaPlayer.setPlayerListener(PlayerListeners())

        playerBinding.backButton.setOnClickListener {
            (requireActivity() as TvVideoPlayerActivity).setCurrentFragment(TvVideoPlayerActivity.BACK_BUTTON)
            requireActivity().onBackPressed()
        }
    }

    override fun onStart() {
        super.onStart()
        if (Util.SDK_INT >= 24) {
            initPlayer(binding.titlePlayer, playerBinding.subtitleToggle)
        }
    }

    override fun onResume() {
        super.onResume()
        if (Util.SDK_INT < 24) {
            initPlayer(binding.titlePlayer, playerBinding.subtitleToggle)
        }
    }

    inner class PlayerListeners: Player.Listener {
        override fun onPlaybackStateChanged(state: Int) {
            super.onPlaybackStateChanged(state)

            when (state) {
                Player.STATE_READY -> {
                    baseStateReady(
                        playerBinding.playerTitle,
                        binding.continueWatching,
                        playerBinding.exoLiveDuration,
                        playerBinding.nextEpisode,
                        binding.titlePlayer
                    )

                    playerBinding.exoPlay.requestFocus()
                    playerBinding.exoPause.requestFocus()
                }
                Player.STATE_ENDED -> {
                    baseStateEnded(playerBinding.nextEpisode, playerBinding.playerTitle)
                }
            }

            binding.titlePlayer.keepScreenOn = !(state == Player.STATE_IDLE || state == Player.STATE_ENDED)
        }
    }

    fun onKeyUp() {
        if (binding.continueWatching.root.isVisible) {
            binding.continueWatching.confirmButton.requestFocus()
        } else {
            when {
                !binding.titlePlayer.isControllerVisible -> {
                    binding.titlePlayer.showController()
                    playerBinding.exoPause.requestFocus()
                }
                playerBinding.nextEpisode.isVisible -> {
                    playerBinding.exoPause.nextFocusUpId = R.id.next_episode
                    playerBinding.exoPlay.nextFocusUpId = R.id.next_episode
                }
                playerBinding.subtitleToggle.isVisible -> {
                    playerBinding.exoPause.nextFocusUpId = R.id.subtitle_toggle
                    playerBinding.exoPlay.nextFocusUpId = R.id.subtitle_toggle
                }
                else -> {
                    playerBinding.exoPause.nextFocusUpId = R.id.back_button
                    playerBinding.exoPlay.nextFocusUpId = R.id.back_button
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
                playerBinding.subtitleToggle.isFocused -> if (next_episode.isVisible) playerBinding.nextEpisode.requestFocus() else playerBinding.exoFfwd.callOnClick()
                else -> playerBinding.exoFfwd.callOnClick()
            }
        }
    }
}