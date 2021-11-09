package com.lukakordzaia.streamflow.ui.phone.videoplayer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import com.google.android.exoplayer2.ui.PlayerView
import com.lukakordzaia.streamflow.databinding.ContinueWatchingDialogBinding
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
        get() = playerBinding.exoDuration

    override val continueWatchingDialog: ContinueWatchingDialogBinding
        get() = binding.continueWatching

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        playerBinding = PhoneExoplayerControllerLayoutBinding.bind(binding.root)

        playerBinding.backButton.setOnClickListener {
            requireActivity().onBackPressed()
        }
        prevButtonClickListener()
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
                    videoPlayerData.chosenEpisode-1,
                    0L,
                    null,
                    videoPlayerData.chosenSubtitle,
                    ))
                mediaPlayer.initPlayer(binding.titlePlayer, 0, 0L)
            }
        }
    }
}