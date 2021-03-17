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
import com.lukakordzaia.streamflow.datamodels.TitleEpisodes
import com.lukakordzaia.streamflow.utils.setGone
import com.lukakordzaia.streamflow.utils.setVisible
import kotlinx.android.synthetic.main.rv_choose_details_episodes_item.view.*

class ChooseTitleDetailsEpisodesAdapter(
        private val context: Context,
        private val onEpisodeClick: (episodeId: Int) -> Unit,
) : RecyclerView.Adapter<ChooseTitleDetailsEpisodesAdapter.ViewHolder>() {
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
            LayoutInflater.from(context).inflate(R.layout.rv_choose_details_episodes_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val episodeModel = list[position]

        val isChosen = position == chosenEpisode-1

        if (isChosen) {
            holder.episodeChosenIndicator.setVisible()
        } else {
            holder.episodeChosenIndicator.setGone()
        }

        holder.episodeContainer.setOnClickListener {
            onEpisodeClick(episodeModel.episodeNum)
        }

        holder.episodeNumberTextView.text = "ეპიზოდი ${episodeModel.episodeNum}"
        holder.episodeNameTextView.text = episodeModel.episodeName

        Glide.with(context).load(episodeModel.episodePoster).error(R.drawable.movie_image_placeholder_landscape).into(holder.episodePosterImageView)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val episodeContainer: ConstraintLayout = view.rv_episodes_container
        val episodeNumberTextView: TextView = view.rv_episodes_number
        val episodeNameTextView: TextView = view.rv_episodes_name
        val episodePosterImageView: ImageView = view.rv_episodes_poster
        val episodeChosenIndicator: ImageView = view.rv_episodes_chosen_indicator
    }
}