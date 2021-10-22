package com.lukakordzaia.streamflow.ui.tv.tvsingletitle.tvtitledetails

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
import com.lukakordzaia.streamflow.R
import com.lukakordzaia.streamflow.database.continuewatchingdb.ContinueWatchingRoom
import com.lukakordzaia.streamflow.databinding.DialogChooseLanguageBinding
import com.lukakordzaia.streamflow.databinding.DialogRemoveTitleBinding
import com.lukakordzaia.streamflow.databinding.FragmentTvTitleDetailsBinding
import com.lukakordzaia.streamflow.datamodels.SingleTitleModel
import com.lukakordzaia.streamflow.datamodels.VideoPlayerData
import com.lukakordzaia.streamflow.network.LoadingState
import com.lukakordzaia.streamflow.ui.baseclasses.fragments.BaseFragmentVM
import com.lukakordzaia.streamflow.ui.tv.tvsingletitle.TvSingleTitleActivity
import com.lukakordzaia.streamflow.ui.tv.tvsingletitle.tvtitlefiles.TvTitleFilesFragment
import com.lukakordzaia.streamflow.ui.tv.tvvideoplayer.TvVideoPlayerActivity
import com.lukakordzaia.streamflow.utils.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.concurrent.TimeUnit


class TvTitleDetailsFragment : BaseFragmentVM<FragmentTvTitleDetailsBinding, TvTitleDetailsViewModel>() {
    var titleId: Int = 0

    override val viewModel by viewModel<TvTitleDetailsViewModel>()
    override val reload: () -> Unit = {
        viewModel.getSingleTitleData(titleId)
        viewModel.getSingleTitleFiles(titleId)
    }

    private lateinit var chooseLanguageAdapter: ChooseLanguageAdapter
    private lateinit var titleInfo: SingleTitleModel
    private var hasFocus: Boolean = false
    private var startedWatching = false

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentTvTitleDetailsBinding
        get() = FragmentTvTitleDetailsBinding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        titleId = activity?.intent?.getSerializableExtra(AppConstants.TITLE_ID) as Int
        val isTvShow = activity?.intent?.getSerializableExtra(AppConstants.IS_TV_SHOW) as Boolean
        val continueWatching = activity?.intent?.getSerializableExtra(AppConstants.CONTINUE_WATCHING_NOW) as? Boolean
        val fromWatchlist = activity?.intent?.getSerializableExtra(AppConstants.FROM_WATCHLIST) as? Int

        fragmentListeners(titleId, isTvShow)
        fragmentObservers(titleId, isTvShow, fromWatchlist)
        favoriteContainer(titleId, fromWatchlist)
        titleDetails(titleId, isTvShow)
        checkContinueWatching(continueWatching)
    }

    override fun onStart() {
        super.onStart()
        viewModel.getSingleTitleData(activity?.intent?.getSerializableExtra(AppConstants.TITLE_ID) as Int)
    }

    private fun fragmentListeners(titleId: Int, isTvShow: Boolean) {
        binding.playButton.setOnClickListener {
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
            binding.rvChooseLanguage.layoutManager = chooseLanguageLayout
            binding.rvChooseLanguage.adapter = chooseLanguageAdapter

            viewModel.availableLanguages.observe(viewLifecycleOwner, {
                val languages = it.reversed()
                chooseLanguageAdapter.setLanguageList(languages)
            })
        }

        if (isTvShow) {
            binding.nextDetailsTitle.text = "ეპიზოდები და მეტი"
        } else {
            binding.nextDetailsTitle.text = "მსხახიობები და მეტი"
        }

        binding.nextDetails.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                (requireActivity() as TvSingleTitleActivity).setCurrentFragment(TvTitleFilesFragment())
            }
            this.hasFocus = hasFocus
        }
    }

    private fun fragmentObservers(titleId: Int, isTvShow: Boolean, fromWatchlist: Int?) {
        viewModel.startedWatching.observe(viewLifecycleOwner, {
            startedWatching = it
        })

        viewModel.generalLoader.observe(viewLifecycleOwner, {
            when (it) {
                LoadingState.LOADING -> binding.tvDetailsProgressBar.setVisible()
                LoadingState.LOADED -> {
                    binding.tvDetailsProgressBar.setGone()
                    binding.mainContainer.setVisible()
                }
            }
        })

        viewModel.movieNotYetAdded.observe(viewLifecycleOwner, {
            if (!it) {
                binding.noFilesContainer.setGone()
                binding.buttonsRow.setVisible()
            } else {
                binding.favoriteContainer.requestFocus()
            }
        })

        viewModel.hideContinueWatchingLoader.observe(viewLifecycleOwner, {
            when (it) {
                LoadingState.LOADING -> {}
                LoadingState.LOADED -> {
                    sharedPreferences.saveTvVideoPlayerOn(true)

                    val intent = Intent(requireContext(), TvSingleTitleActivity::class.java).apply {
                        putExtra(AppConstants.TITLE_ID, titleId)
                        putExtra(AppConstants.IS_TV_SHOW, isTvShow)
                        putExtra(AppConstants.FROM_WATCHLIST, fromWatchlist)
                        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    startActivity(intent)
                }
                LoadingState.ERROR -> {
                    viewModel.newToastMessage("სამწუხაროდ, ვერ მოხეხრდა სიიდან დამალვა")
                }
            }
        })
    }

    private fun favoriteContainer(titleId: Int, fromWatchlist: Int?) {
        viewModel.addToFavorites.observe(viewLifecycleOwner, {
            if (it) {
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
        })

        viewModel.favoriteLoader.observe(viewLifecycleOwner, {
            when (it) {
                LoadingState.LOADING -> {
                    binding.favoriteProgressBar.setVisible()
                    binding.favoriteIcon.setGone()
                }
                LoadingState.LOADED -> {
                    binding.favoriteProgressBar.setGone()
                    binding.favoriteIcon.setVisible()
                }
            }
        })
    }

    private fun titleDetails(titleId: Int, isTvShow: Boolean) {
        viewModel.getSingleTitleFiles(titleId)

        viewModel.getSingleTitleResponse.observe(viewLifecycleOwner, {
            titleInfo = it

            binding.titleName.text = it.nameEng

            binding.trailerButton.setOnClickListener { _ ->
                if (it.trailer != null) {
                    playTitleTrailer(titleId, isTvShow, it.trailer)
                } else {
                    viewModel.newToastMessage("ტრეილერი ვერ მოიძებნა")
                }
            }

            binding.backgroundPoster.setImage(it.cover, false)

            binding.year.text = it.releaseYear
            binding.imdbScore.text = "IMDB ${it.imdbScore}"
            binding.country.text = it.country

            if (it.isTvShow) {
                binding.duration.text = "${it.seasonNum} სეზონი"
            } else {
                binding.duration.text = it.duration
            }

            binding.titleDescription.text = it.description
        })
    }

    private fun checkContinueWatching(continueWatching: Boolean?) {
        binding.deleteButton.setGone()
        binding.playButton.requestFocus()
        binding.continueButton.setGone()

        viewModel.continueWatchingDetails.observe(viewLifecycleOwner, {
            if (it != null) {
                binding.deleteButton.setOnClickListener { _ ->
                    val binding = DialogRemoveTitleBinding.inflate(LayoutInflater.from(requireContext()))
                    val removeTitle = Dialog(requireContext())
                    removeTitle.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    removeTitle.setContentView(binding.root)

                    if (sharedPreferences.getLoginToken() != "") {
                        binding.title.text = resources.getString(R.string.remove_from_list_title)
                    }

                    binding.continueButton.setOnClickListener { _ ->
                        if (sharedPreferences.getLoginToken() == "") {
                            viewModel.deleteSingleContinueWatchingFromRoom(it.titleId)
                        } else {
                            viewModel.hideSingleContinueWatching(it.titleId)
                        }
                    }
                    binding.cancelButton.setOnClickListener {
                        removeTitle.dismiss()
                    }
                    removeTitle.show()
                    binding.continueButton.requestFocus()
                }

                binding.continueButton.setOnClickListener { _ ->
                    continueTitlePlay(ContinueWatchingRoom(
                        it.titleId,
                        it.language,
                        TimeUnit.SECONDS.toMillis(it.watchedDuration),
                        TimeUnit.SECONDS.toMillis(it.titleDuration),
                        it.isTvShow,
                        it.season,
                        it.episode,
                    ))
                }

                binding.continueButton.text = "განაგრძეთ - " + if (it.isTvShow) {
                    it.watchedDuration.titlePosition(it.season, it.episode)
                } else {
                    it.watchedDuration.titlePosition(null, null)
                }

                binding.continueButton.setVisible()
                binding.continueButton.requestFocus()
                binding.playButton.text = "თავიდან ყურება"

                if (sharedPreferences.getLoginToken() == "") {
                    binding.deleteButton.setVisible()
                } else {
                    binding.deleteButton.setVisibleOrGone(titleInfo.visibility!!)
                }

                if (continueWatching != null) {
                    viewModel.startedWatching.observe(viewLifecycleOwner, {
                        if (!it) {
                            binding.continueButton.callOnClick()
                            viewModel.setStartedWatching(true)
                        }
                    })
                }
            }
        })
    }

    private fun playTitleTrailer(titleId: Int, isTvShow: Boolean, trailerUrl: String) {
        val intent = Intent(context, TvVideoPlayerActivity::class.java)
        intent.putExtra(AppConstants.VIDEO_PLAYER_DATA, VideoPlayerData(
            titleId,
            isTvShow,
            0,
            "ENG",
            0,
            0L,
            trailerUrl
        ))
        activity?.startActivity(intent)
    }

    private fun playTitleFromStart(titleId: Int, isTvShow: Boolean, chosenLanguage: String) {
        val intent = Intent(context, TvVideoPlayerActivity::class.java)
        intent.putExtra(AppConstants.VIDEO_PLAYER_DATA, VideoPlayerData(
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
        intent.putExtra(AppConstants.VIDEO_PLAYER_DATA, VideoPlayerData(
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