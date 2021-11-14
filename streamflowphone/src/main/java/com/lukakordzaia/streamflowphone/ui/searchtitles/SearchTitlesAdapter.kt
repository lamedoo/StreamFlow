package com.lukakordzaia.streamflowphone.ui.searchtitles

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lukakordzaia.core.datamodels.SingleTitleModel
import com.lukakordzaia.core.utils.setImage
import com.lukakordzaia.streamflowphone.R
import com.lukakordzaia.streamflowphone.databinding.RvSearchItemBinding

class SearchTitlesAdapter(private val context: Context, private val onTitleClick: (id : Int) -> Unit) : RecyclerView.Adapter<SearchTitlesAdapter.ViewHolder>() {
    private var list: List<SingleTitleModel> = ArrayList()

    fun setSearchTitleList(list: List<SingleTitleModel>) {
        this.list = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            RvSearchItemBinding.inflate(LayoutInflater.from(context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val listModel = list[position]

        holder.bind(listModel)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder(val view: RvSearchItemBinding) : RecyclerView.ViewHolder(view.root) {
        fun bind(model: SingleTitleModel) {
            view.rvSearchItemName.text = model.displayName
            view.rvSearchItemYear.text = "${model.releaseYear}, "

            view.rvSearchItemPoster.setImage(model.poster, true)

            view.rvSearchItemIsTvShow.text = if (model.isTvShow) context.getString(R.string.tv_show) else context.getString(R.string.movie)

            view.root.setOnClickListener {
                onTitleClick(model.id)
            }
        }
    }
}