package com.lukakordzaia.streamflowphone.ui.phonesingletitle.episodesbottomsheet

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lukakordzaia.core.domain.domainmodels.SeasonEpisodesModel
import com.lukakordzaia.core.utils.setImage
import com.lukakordzaia.core.utils.setVisibleOrGone
import com.lukakordzaia.streamflowphone.R
import com.lukakordzaia.streamflowphone.databinding.RvChooseDetailsEpisodesItemBinding

class EpisodesBottomSheetEpisodesAdapter(
    private val onEpisodeClick: (episodeId: Int, languages: List<String>) -> Unit,
    private val onChosenEpisode: (position: Int) -> Unit
) : RecyclerView.Adapter<EpisodesBottomSheetEpisodesAdapter.ViewHolder>() {
    private var list: List<SeasonEpisodesModel> = ArrayList()
    private var chosenEpisode: Int? = null

    fun setEpisodeList(list: List<SeasonEpisodesModel>) {
        this.list = list
        notifyDataSetChanged()
    }

    fun setChosenEpisode(episodeId: Int? = null) {
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
        fun bind(model: SeasonEpisodesModel, position: Int) {
            var isChosen = false

            chosenEpisode?.let {
                isChosen = position == it - 1
                onChosenEpisode(it)
            }

            view.currentIndicator.setVisibleOrGone(isChosen)

            view.episodeNumber.text = view.root.context.getString(R.string.episode_number, model.episode.toString())
            view.episodeName.text = model.title

            view.itemPoster.setImage(model.cover, false)

            view.itemSeekBar.setVisibleOrGone(model.titleDuration?.toInt() != 0)
            if (model.titleDuration?.toInt() != 0 && model.titleDuration != null) {
                view.itemSeekBar.max = model.titleDuration!!.toInt()
                view.itemSeekBar.progress = model.watchedDuration!!.toInt()
            }

            view.rvEpisodesContainer.setOnClickListener {
                onEpisodeClick(model.episode, model.languages)
            }
        }
    }
}