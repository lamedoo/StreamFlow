package com.lukakordzaia.imoviesapp.ui.tv.details.chooseFiles

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lukakordzaia.imoviesapp.R
import kotlinx.android.synthetic.main.rv_tv_files_episodes_item.view.*

class TvChooseFilesEpisodesAdapter(private val context: Context, private val onEpisodeClick: (episodeId: Int) -> Unit) : RecyclerView.Adapter<TvChooseFilesEpisodesAdapter.ViewHolder>() {
    private var list: List<Int> = ArrayList()

    fun setEpisodeList(list: List<Int>) {
        this.list = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return TvChooseFilesEpisodesAdapter.ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.rv_tv_files_episodes_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val episodeModel = list[position]

        holder.episodeNameTextView.text = episodeModel.toString()
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val episodeNameTextView: TextView = view.rv_tv_files_episode_name
    }
}