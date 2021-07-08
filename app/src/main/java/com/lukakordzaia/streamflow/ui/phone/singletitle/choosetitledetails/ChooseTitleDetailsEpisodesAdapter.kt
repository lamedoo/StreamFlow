package com.lukakordzaia.streamflow.ui.phone.singletitle.choosetitledetails

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.lukakordzaia.streamflow.R
import com.lukakordzaia.streamflow.databinding.RvChooseDetailsEpisodesItemBinding
import com.lukakordzaia.streamflow.datamodels.TitleEpisodes
import com.lukakordzaia.streamflow.utils.setGone
import com.lukakordzaia.streamflow.utils.setVisible
import kotlinx.android.synthetic.main.rv_choose_details_episodes_item.view.*

class ChooseTitleDetailsEpisodesAdapter(
        private val context: Context,
        private val onEpisodeClick: (episodeId: Int) -> Unit,
        private val onChosenEpisode: (position: Int) -> Unit
) : RecyclerView.Adapter<ChooseTitleDetailsEpisodesAdapter.ViewHolder>() {
    private var list: List<TitleEpisodes> = ArrayList()
    private var chosenEpisode: Int = -1

    fun setEpisodeList(list: List<TitleEpisodes>) {
        this.list = list
        onChosenEpisode(chosenEpisode)
        notifyDataSetChanged()
    }

    fun setChosenEpisode(episodeId: Int) {
        chosenEpisode = episodeId
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            RvChooseDetailsEpisodesItemBinding.inflate(LayoutInflater.from(context), parent, false)
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

            if (isChosen) {
                view.rvEpisodesChosenIndicator.setVisible()
            } else {
                view.rvEpisodesChosenIndicator.setGone()
            }

            view.rvEpisodesNumber.text = "ეპიზოდი ${model.episodeNum}"
            view.rvEpisodesName.text = model.episodeName

            Glide.with(context).load(model.episodePoster).error(R.drawable.movie_image_placeholder_landscape).into(view.rvEpisodesPoster)

            view.rvEpisodesContainer.setOnClickListener {
                onEpisodeClick(model.episodeNum)
            }
        }
    }
}