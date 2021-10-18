package com.lukakordzaia.streamflow.ui.tv.tvvideoplayer

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.util.Util
import com.lukakordzaia.streamflow.R
import com.lukakordzaia.streamflow.databinding.FragmentTvVideoPlayerBinding
import com.lukakordzaia.streamflow.databinding.TvExoplayerControllerLayoutBinding
import com.lukakordzaia.streamflow.datamodels.TitleMediaItemsUri
import com.lukakordzaia.streamflow.ui.baseclasses.fragments.BaseVideoPlayerFragment
import com.lukakordzaia.streamflow.ui.tv.main.TvActivity
import com.lukakordzaia.streamflow.utils.setGone
import com.lukakordzaia.streamflow.utils.videoPlayerPosition
import kotlinx.android.synthetic.main.tv_exoplayer_controller_layout.*


class TvVideoPlayerFragment : BaseVideoPlayerFragment<FragmentTvVideoPlayerBinding>() {
    private lateinit var playerBinding: TvExoplayerControllerLayoutBinding

    private var tracker: ProgressTracker? = null

    private val autoBackPress = autoBackPress {
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

        videoPlayerViewModel.videoPlayerInfo.observe(viewLifecycleOwner, {
            videoPlayerInfo = it
            nextButtonClickListener(playerBinding.nextEpisode, binding.tvTitlePlayer)

            setTitleName(playerBinding.playerTitle)
            playerBinding.exoPlay.requestFocus()
            playerBinding.exoPause.requestFocus()
        })

        if (videoPlayerData.trailerUrl != null) {
            playerBinding.nextEpisode.setGone()
        }
    }

    override fun onStart() {
        super.onStart()
        if (Util.SDK_INT >= 24) {
            initPlayer(videoPlayerData.watchedTime, videoPlayerData.trailerUrl)
        }
    }

    override fun onResume() {
        super.onResume()
        if (Util.SDK_INT < 24) {
            initPlayer(videoPlayerData.watchedTime, videoPlayerData.trailerUrl)
        }
    }

    inner class PlayerListeners: Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            super.onIsPlayingChanged(isPlaying)

            if (isPlaying) autoBackPress.cancel() else autoBackPress.start()
        }

        override fun onPlaybackStateChanged(state: Int) {
            super.onPlaybackStateChanged(state)

            when (state) {
                Player.STATE_READY -> {
                    episodeHasEnded = true

                    showContinueWatchingDialog(binding.continueWatching) {
                        val intent = Intent(requireContext(), TvActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                    }

                    tracker = ProgressTracker(player, object :
                        PositionListener {
                        override fun progress(position: Long) {
                            playerBinding.exoLiveDuration.text = position.videoPlayerPosition()
                        }
                    })
                }
                Player.STATE_ENDED -> {
                    stateHasEnded(playerBinding.nextEpisode)
                }
            }

            binding.tvTitlePlayer.keepScreenOn = !(state == Player.STATE_IDLE || state == Player.STATE_ENDED)
        }
    }

    private fun subtitleFunctions(hasSubs: Boolean) {
        subtitleFunctions(binding.tvTitlePlayer.subtitleView!!, playerBinding.subtitleToggle, hasSubs)
    }

    private fun initPlayer(watchedTime: Long, trailerUrl: String?) {
        if (trailerUrl != null) {
            mediaPlayer.setMediaItems(listOf(MediaItem.fromUri(Uri.parse(trailerUrl))))
            mediaPlayer.setPlayerMediaSource(buildMediaSource.movieMediaSource(
                TitleMediaItemsUri(MediaItem.fromUri(
                    Uri.parse(trailerUrl)), "0")
            ))
            subtitleFunctions(false)
        } else {
            videoPlayerViewModel.mediaAndSubtitle.observe(viewLifecycleOwner, {
                mediaPlayer.setPlayerMediaSource(buildMediaSource.movieMediaSource(it))

                subtitleFunctions(it.titleSubUri.isNotEmpty() && it.titleSubUri != "0")
            })
        }
        mediaPlayer.initPlayer(binding.tvTitlePlayer, 0, watchedTime)
    }

    inner class ProgressTracker(private val player: Player, private val positionListener: PositionListener) : Runnable {
        private val handler: Handler = Handler(Looper.myLooper()!!)
        override fun run() {
            val position = if (player.duration <= 0) 0 else player.duration - player.currentPosition
            positionListener.progress(position)
            handler.postDelayed(this, 1000)
        }

        fun purgeHandler() {
            handler.removeCallbacks(this)
        }

        init {
            handler.post(this)
        }
    }

    interface PositionListener {
        fun progress(position: Long)
    }

    override fun onDetach() {
        super.onDetach()
        tracker?.purgeHandler()
        autoBackPress.cancel()
    }

    fun onKeyUp() {
        if (binding.continueWatching.root.isVisible) {
            binding.continueWatching.confirmButton.requestFocus()
        } else {
            when {
                !binding.tvTitlePlayer.isControllerVisible -> {
                    binding.tvTitlePlayer.showController()
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
        if (!binding.tvTitlePlayer.isControllerVisible) {
            binding.tvTitlePlayer.showController()
            binding.tvTitlePlayer.player!!.pause()
        } else if (!binding.tvTitlePlayer.player!!.isPlaying && !binding.tvTitlePlayer.isControllerVisible) {
            binding.tvTitlePlayer.player!!.play()
        }
    }

    fun onKeyLeft() {
        if (!binding.continueWatching.root.isVisible) {
            when {
                !binding.tvTitlePlayer.isControllerVisible -> {
                    binding.tvTitlePlayer.showController()
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
                !binding.tvTitlePlayer.isControllerVisible -> {
                    binding.tvTitlePlayer.showController()
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