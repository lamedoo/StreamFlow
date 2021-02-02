package com.lukakordzaia.imoviesapp.ui.tv.details.titlefiles

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.lukakordzaia.imoviesapp.R
import com.lukakordzaia.imoviesapp.datamodels.TitleEpisodes
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.tv_details_episodes_item.view.*

class TvTitleFilesEpisodesAdapter(
        private val context: Context,
        private val onEpisodeClick: (episodeId: Int) -> Unit,
) : RecyclerView.Adapter<TvTitleFilesEpisodesAdapter.ViewHolder>() {
    private var list: List<TitleEpisodes> = ArrayList()

    fun setEpisodeList(list: List<TitleEpisodes>) {
        this.list = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.tv_details_episodes_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val episodeModel = list[position]

        holder.episodeContainer.setOnClickListener {
            onEpisodeClick(episodeModel.episodeNum)
        }

        if (episodeModel.episodePoster != null) {
            Picasso.get().load(episodeModel.episodePoster).into(holder.episodePosterImageView)
        }

        holder.episodeNumberTextView.text = "ეპიზოდი ${episodeModel.episodeNum}"
        holder.episodeNameTextView.text = episodeModel.episodeName
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val episodeContainer: ConstraintLayout = view.rv_tv_files_episode_container
        val episodePosterImageView: ImageView = view.rv_tv_files_episode_poster
        val episodeNumberTextView: TextView = view.rv_tv_files_episode_number
        val episodeNameTextView: TextView = view.rv_tv_files_episode_name
    }
}