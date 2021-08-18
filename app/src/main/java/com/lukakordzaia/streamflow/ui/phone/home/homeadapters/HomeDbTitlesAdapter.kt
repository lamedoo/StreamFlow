package com.lukakordzaia.streamflow.ui.phone.home.homeadapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lukakordzaia.streamflow.databinding.RvDbTitleItemBinding
import com.lukakordzaia.streamflow.datamodels.ContinueWatchingModel
import com.lukakordzaia.streamflow.utils.setImage
import java.util.concurrent.TimeUnit

class HomeDbTitlesAdapter(
    private val context: Context,
    private val onWatchedTitleClick: (continueWatchingModel: ContinueWatchingModel) -> Unit,
    private val onInfoClick: (titleId: Int) -> Unit,
    private val onMoreMenuClick: (titleId: Int, titleName: String) -> Unit
) : RecyclerView.Adapter<HomeDbTitlesAdapter.ViewHolder>() {
    private var list: List<ContinueWatchingModel> = ArrayList()

    fun setWatchedTitlesList(list: List<ContinueWatchingModel>) {
        this.list = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            RvDbTitleItemBinding.inflate(LayoutInflater.from(context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val listModel = list[position]

        holder.bind(listModel)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder(val view: RvDbTitleItemBinding) : RecyclerView.ViewHolder(view.root) {
        fun bind(model: ContinueWatchingModel) {
            view.itemPoster.setImage(model.cover, true)

            if (model.isTvShow) {
                view.continueWatchingInfo.text = String.format("ს${model.season} ე${model.episode} / %02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(model.watchedDuration),
                    TimeUnit.MILLISECONDS.toSeconds(model.watchedDuration) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(model.watchedDuration))
                )
            } else {
                view.continueWatchingInfo.text = String.format("%02d:%02d:%02d",
                    TimeUnit.MILLISECONDS.toHours(model.watchedDuration),
                    TimeUnit.MILLISECONDS.toMinutes(model.watchedDuration) -
                            TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(model.watchedDuration)),
                    TimeUnit.MILLISECONDS.toSeconds(model.watchedDuration) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(model.watchedDuration))
                )
            }

            view.itemSeekBar.max = model.titleDuration.toInt()
            view.itemSeekBar.progress = model.watchedDuration.toInt()

            view.root.setOnClickListener {
                onWatchedTitleClick(model)
            }

            view.itemDetails.setOnClickListener {
                onInfoClick(model.id)
            }

            view.itemMore.setOnClickListener {
                onMoreMenuClick(model.id, if (model.originalName.isNullOrEmpty()) model.primaryName!! else model.originalName)
            }
        }
    }
}