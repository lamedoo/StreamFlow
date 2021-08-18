package com.lukakordzaia.streamflow.ui.phone.searchtitles

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lukakordzaia.streamflow.databinding.RvSearchItemBinding
import com.lukakordzaia.streamflow.datamodels.SingleTitleModel
import com.lukakordzaia.streamflow.utils.setImage

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

            view.rvSearchItemIsTvShow.text = if (model.isTvShow) "სერიალი" else "ფილმი"

            view.root.setOnClickListener {
                onTitleClick(model.id)
            }
        }
    }
}