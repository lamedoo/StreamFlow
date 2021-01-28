package com.lukakordzaia.imoviesapp.ui.tv.details.chooseFiles

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.lukakordzaia.imoviesapp.R
import com.lukakordzaia.imoviesapp.datamodels.TitleEpisodes
import kotlinx.android.synthetic.main.rv_tv_files_episodes_item.view.*

class TvChooseFilesEpisodesAdapter(
        private val context: Context,
        private val onEpisodeClick: (episodeId: Int) -> Unit,
) : RecyclerView.Adapter<TvChooseFilesEpisodesAdapter.ViewHolder>() {
    private var list: List<TitleEpisodes> = ArrayList()

    fun setEpisodeList(list: List<TitleEpisodes>) {
        this.list = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.rv_tv_files_episodes_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val episodeModel = list[position]

        holder.episodeContainer.setOnClickListener {
            onEpisodeClick(episodeModel.episodeNum)
        }

        holder.episodeNumberTextView.text = "ეპიზოდი ${episodeModel.episodeNum}"
        holder.episodeNameTextView.text = episodeModel.episodeName
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val episodeContainer: ConstraintLayout = view.rv_tv_files_episode_container
        val episodeNumberTextView: TextView = view.rv_tv_files_episode_number
        val episodeNameTextView: TextView = view.rv_tv_files_episode_name
    }
}