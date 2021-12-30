package com.lukakordzaia.streamflowtv.ui.tvsingletitle.tvtitlerelated

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.leanback.widget.*
import androidx.leanback.widget.BrowseFrameLayout.OnFocusSearchListener
import androidx.recyclerview.widget.GridLayoutManager
import com.lukakordzaia.core.utils.AppConstants
import com.lukakordzaia.core.adapters.ChooseLanguageAdapter
import com.lukakordzaia.core.databinding.DialogChooseLanguageBinding
import com.lukakordzaia.core.datamodels.SingleTitleModel
import com.lukakordzaia.core.datamodels.TitleEpisodes
import com.lukakordzaia.core.datamodels.VideoPlayerData
import com.lukakordzaia.core.network.models.imovies.response.singletitle.GetSingleTitleCastResponse
import com.lukakordzaia.streamflowtv.R
import com.lukakordzaia.streamflowtv.baseclasses.BaseBrowseSupportFragment
import com.lukakordzaia.streamflowtv.ui.main.presenters.TvMainPresenter
import com.lukakordzaia.streamflowtv.ui.tvsingletitle.TvSingleTitleActivity
import com.lukakordzaia.streamflowtv.ui.tvsingletitle.tvtitlerelated.presenters.TvCastPresenter
import com.lukakordzaia.streamflowtv.ui.tvsingletitle.tvtitlerelated.presenters.TvEpisodesPresenter
import com.lukakordzaia.streamflowtv.ui.tvsingletitle.tvtitlerelated.presenters.TvSeasonsPresenter
import com.lukakordzaia.streamflowtv.ui.tvsingletitle.tvtitlerelated.presenters.TvSimilarPresenter
import com.lukakordzaia.streamflowtv.ui.tvvideoplayer.TvVideoPlayerActivity
import com.lukakordzaia.streamflowtv.ui.tvwatchlist.TvWatchlistActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class TvRelatedBrowse : BaseBrowseSupportFragment<TvRelatedViewModel>() {
    var titleId: Int? = 0

    override val viewModel by viewModel<TvRelatedViewModel>()
    override val reload: () -> Unit = {
        setTitleCast(titleId!!)
        setTitleRelated(titleId!!)
    }

    private var hasFocus = false

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        workaroundFocus()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val titleIdFromDetails = activity?.intent?.getSerializableExtra(AppConstants.TITLE_ID) as? Int
        val videoPlayerData = activity?.intent?.getParcelableExtra(AppConstants.VIDEO_PLAYER_DATA) as? VideoPlayerData

        titleId = titleIdFromDetails ?: videoPlayerData?.titleId

        initRowsAdapter()
        setTitleCast(titleId!!)
        setTitleRelated(titleId!!)

        setupEventListeners(ItemViewClickedListener(), ItemViewSelectedListener())
    }

    private fun initRowsAdapter() {
        val fourthItem = ListRow(HeaderItem(0, getString(R.string.actors)), ArrayObjectAdapter(TvCastPresenter()))
        val fifthItem = ListRow(HeaderItem(1, "მსგავსი"), ArrayObjectAdapter(TvSimilarPresenter()))
        val initListRows = mutableListOf(fourthItem, fifthItem)
        rowsAdapter.addAll(0, initListRows)
    }

    private fun setTitleCast(titleId: Int) {
        viewModel.getSingleTitleCast(titleId)

        viewModel.castResponseDataGetSingle.observe(viewLifecycleOwner, {
            castRowsAdapter(it)
        })
    }

    private fun setTitleRelated(titleId: Int) {
        viewModel.getSingleTitleRelated(titleId)

        viewModel.singleTitleRelated.observe(viewLifecycleOwner, {
            relatedRowsAdapter(it)
        })
    }

    private fun castRowsAdapter(castResponseListGetSingle: List<GetSingleTitleCastResponse.Data>) {
        val listRowAdapter = ArrayObjectAdapter(TvCastPresenter()).apply {
            castResponseListGetSingle.forEach {
                add(it)
            }
        }
        HeaderItem("მსახიობები"). also {
            rowsAdapter.replace( 0, ListRow(it, listRowAdapter))
        }
    }

    private fun relatedRowsAdapter(relatedList: List<SingleTitleModel>) {
        val listRowAdapter = ArrayObjectAdapter(TvSimilarPresenter()).apply {
            addAll(0, relatedList)
        }
        HeaderItem("მსგავსი"). also {
            rowsAdapter.replace(1, ListRow(it, listRowAdapter))
        }
    }

    private inner class ItemViewClickedListener : OnItemViewClickedListener {
        override fun onItemClicked(
                itemViewHolder: Presenter.ViewHolder,
                item: Any,
                rowViewHolder: RowPresenter.ViewHolder,
                row: Row
        ) {
            when (item) {
                is SingleTitleModel -> {
                    val intent = Intent(context, TvSingleTitleActivity::class.java).apply {
                        putExtra(AppConstants.TITLE_ID, item.id)
                        putExtra(AppConstants.IS_TV_SHOW, item.isTvShow)
                    }
                    requireActivity().startActivity(intent)
                    if (requireActivity() is TvVideoPlayerActivity) {
                        requireActivity().finish()
                    }
                }
            }
        }
    }

    private inner class ItemViewSelectedListener : OnItemViewSelectedListener {
        override fun onItemSelected(itemViewHolder: Presenter.ViewHolder?, item: Any?, rowViewHolder: RowPresenter.ViewHolder?, row: Row?) {

        }
    }

    private fun workaroundFocus() {
        if (view != null) {
            val viewToFocus = requireActivity().findViewById<View>(R.id.go_top_text)
            val browseFrameLayout: BrowseFrameLayout = requireView().findViewById(androidx.leanback.R.id.browse_frame)
            browseFrameLayout.onFocusSearchListener = OnFocusSearchListener setOnFocusSearchListener@{ _: View?, direction: Int ->
                if (direction == View.FOCUS_UP) {
                    this.hasFocus = true
                    if (requireActivity() is TvWatchlistActivity) {
                        (requireActivity() as TvWatchlistActivity).buttonFocusability(true)
                    }
                    return@setOnFocusSearchListener viewToFocus
                } else {
                    return@setOnFocusSearchListener null
                }
            }
        }
    }
}