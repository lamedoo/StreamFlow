package com.lukakordzaia.streamflowphone.ui.sharedadapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.lukakordzaia.core.domain.domainmodels.SingleTitleModel
import com.lukakordzaia.core.utils.setImage
import com.lukakordzaia.streamflowphone.R
import com.lukakordzaia.streamflowphone.databinding.RvSingleGenreItemBinding

class SingleCategoryAdapter(private val onTitleClick: (id: Int) -> Unit) : RecyclerView.Adapter<SingleCategoryAdapter.ViewHolder>() {
    private var list: List<SingleTitleModel> = ArrayList()
    private var startPosition = 0

    fun setItems(list: List<SingleTitleModel>) {
        this.list = list
        startPosition += list.size
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            RvSingleGenreItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val listModel = list[position]

        holder.bind(listModel)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder(val view: RvSingleGenreItemBinding) : RecyclerView.ViewHolder(view.root) {
        fun bind(model: SingleTitleModel) {
            view.itemName.text = model.displayName
            view.isTvShow.text = if (model.isTvShow) view.root.context.getString(R.string.tv_show) else view.root.context.getString(R.string.movie)

            view.itemPoster.setImage(model.poster, true)

            view.root.setOnClickListener {
                onTitleClick(model.id)
                it.startAnimation(AnimationUtils.loadAnimation(it.context, R.anim.fade_in))
            }
        }
    }
}