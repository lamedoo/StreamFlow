package com.lukakordzaia.streamflowphone.ui.phonesingletitle.tvepisodesbottomsheet

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lukakordzaia.core.domain.domainmodels.TitleEpisodes
import com.lukakordzaia.core.utils.setImage
import com.lukakordzaia.core.utils.setVisibleOrGone
import com.lukakordzaia.streamflowphone.R
import com.lukakordzaia.streamflowphone.databinding.RvChooseDetailsEpisodesItemBinding

class TvEpisodesBottomSheetEpisodesAdapter(
    private val onEpisodeClick: (episodeId: Int) -> Unit,
    private val onChosenEpisode: (position: Int) -> Unit
) : RecyclerView.Adapter<TvEpisodesBottomSheetEpisodesAdapter.ViewHolder>() {
    private var list: List<TitleEpisodes> = ArrayList()
    private var chosenEpisode: Int = -1

    fun setEpisodeList(list: List<TitleEpisodes>) {
        this.list = list
        notifyDataSetChanged()
    }

    fun setChosenEpisode(episodeId: Int) {
        chosenEpisode = episodeId
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            RvChooseDetailsEpisodesItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val episodeModel = list[position]

        holder.bind(episodeModel, position)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder(val view: RvChooseDetailsEpisodesItemBinding) : RecyclerView.ViewHolder(view.root) {
        fun bind(model: TitleEpisodes, position: Int) {
            val isChosen = position == chosenEpisode-1

            onChosenEpisode(chosenEpisode)

            view.currentIndicator.setVisibleOrGone(isChosen)

            view.episodeNumber.text = view.root.context.getString(R.string.episode_number, model.episodeNum.toString())
            view.episodeName.text = model.episodeName

            view.itemPoster.setImage(model.episodePoster, false)

            view.itemSeekBar.setVisibleOrGone(model.titleDuration?.toInt() != 0)
            if (model.titleDuration?.toInt() != 0) {
                view.itemSeekBar.max = model.titleDuration!!.toInt()
                view.itemSeekBar.progress = model.watchDuration!!.toInt()
            }

            view.rvEpisodesContainer.setOnClickListener {
                onEpisodeClick(model.episodeNum)
            }
        }
    }
}