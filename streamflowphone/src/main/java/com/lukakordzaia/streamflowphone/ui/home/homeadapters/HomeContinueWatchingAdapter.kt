package com.lukakordzaia.streamflowphone.ui.home.homeadapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lukakordzaia.core.domain.domainmodels.ContinueWatchingModel
import com.lukakordzaia.core.utils.setImage
import com.lukakordzaia.core.utils.titlePosition
import com.lukakordzaia.streamflowphone.databinding.RvDbTitleItemBinding

class HomeContinueWatchingAdapter(
    private val onWatchedTitleClick: (continueWatchingModel: ContinueWatchingModel) -> Unit,
    private val onMoreMenuClick: (titleId: Int, titleName: String) -> Unit
) : RecyclerView.Adapter<HomeContinueWatchingAdapter.ViewHolder>() {
    private var list: List<ContinueWatchingModel> = ArrayList()

    fun setWatchedTitlesList(list: List<ContinueWatchingModel>) {
        this.list = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            RvDbTitleItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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

            view.continueWatchingInfo.text = if (model.isTvShow) {
                model.watchedDuration.titlePosition(model.season, model.episode)
            } else {
                model.watchedDuration.titlePosition(null, null)
            }

            view.itemSeekBar.max = model.titleDuration.toInt()
            view.itemSeekBar.progress = model.watchedDuration.toInt()

            view.root.setOnClickListener {
                onWatchedTitleClick(model)
            }

            view.itemMore.setOnClickListener {
                onMoreMenuClick(model.id, if (model.originalName.isNullOrEmpty()) model.primaryName!! else model.originalName!!)
            }
        }
    }
}