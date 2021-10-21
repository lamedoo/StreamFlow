package com.lukakordzaia.streamflow.ui.phone.videoplayer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.util.Util
import com.lukakordzaia.streamflow.databinding.FragmentPhoneVideoPlayerBinding
import com.lukakordzaia.streamflow.databinding.PhoneExoplayerControllerLayoutBinding
import com.lukakordzaia.streamflow.datamodels.VideoPlayerData
import com.lukakordzaia.streamflow.ui.baseclasses.fragments.BaseVideoPlayerFragment
import com.lukakordzaia.streamflow.utils.setVisibleOrGone

class VideoPlayerFragment : BaseVideoPlayerFragment<FragmentPhoneVideoPlayerBinding>() {
    private lateinit var playerBinding: PhoneExoplayerControllerLayoutBinding
    override val reload: () -> Unit = {
        viewModel.getTitleFiles(videoPlayerData)
        viewModel.getSingleTitleData(videoPlayerData.titleId)
    }


    override val autoBackPress = AutoBackPress {
        requireActivity().onBackPressed()
    }

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentPhoneVideoPlayerBinding
        get() = FragmentPhoneVideoPlayerBinding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        playerBinding = PhoneExoplayerControllerLayoutBinding.bind(binding.root)

        mediaPlayer.setPlayerListener(PlayerListeners())

        playerBinding.backButton.setOnClickListener {
            requireActivity().onBackPressed()
        }

        nextButtonClickListener(playerBinding.nextEpisode, binding.titlePlayer)
        prevButtonClickListener()
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

    inner class PlayerListeners : Player.Listener {
        override fun onPlaybackStateChanged(state: Int) {
            super.onPlaybackStateChanged(state)

            when (state) {
                Player.STATE_READY -> {
                    baseStateReady(playerBinding.playerTitle, binding.continueWatching)
                }
                Player.STATE_ENDED -> {
                    baseStateEnded(playerBinding.nextEpisode)
                }
            }

            binding.titlePlayer.keepScreenOn = !(state == Player.STATE_IDLE || state == Player.STATE_ENDED)
        }
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
                player.clearMediaItems()
                viewModel.setVideoPlayerData(VideoPlayerData(
                    videoPlayerData.titleId,
                    videoPlayerData.isTvShow,
                    videoPlayerData.chosenSeason,
                    videoPlayerData.chosenLanguage,
                    videoPlayerData.chosenEpisode-1,
                    0L,
                    null
                ))
                mediaPlayer.initPlayer(binding.titlePlayer, 0, 0L)
            }
        }
    }
}