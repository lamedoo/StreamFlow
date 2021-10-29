package com.lukakordzaia.streamflow.ui.phone.home.homeadapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lukakordzaia.streamflow.databinding.RvHomeItemBinding
import com.lukakordzaia.streamflow.datamodels.NewSeriesModel
import com.lukakordzaia.streamflow.utils.setImage

class HomeNewSeriesAdapter(private val context: Context, private val onTitleClick: (id: Int) -> Unit) : RecyclerView.Adapter<HomeNewSeriesAdapter.ViewHolder>() {
    private var list: List<NewSeriesModel> = ArrayList()

    fun setItems(list: List<NewSeriesModel>) {
        this.list = list
        notifyItemRangeInserted(0, list.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            RvHomeItemBinding.inflate(LayoutInflater.from(context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val listModel = list[position]

        holder.bind(listModel)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder(val view: RvHomeItemBinding) : RecyclerView.ViewHolder(view.root) {
        fun bind(model: NewSeriesModel) {
            view.itemName.text = model.displayName
            view.itemPoster.setImage(model.poster, true)

            view.root.setOnClickListener {
                onTitleClick(model.id)
            }
        }
    }

}