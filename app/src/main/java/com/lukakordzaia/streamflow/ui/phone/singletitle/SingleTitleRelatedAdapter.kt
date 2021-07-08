package com.lukakordzaia.streamflow.ui.phone.singletitle

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lukakordzaia.streamflow.R
import com.lukakordzaia.streamflow.databinding.RvHomeItemBinding
import com.lukakordzaia.streamflow.network.models.response.titles.GetTitlesResponse
import com.squareup.picasso.Picasso

class SingleTitleRelatedAdapter(private val context: Context, private val onTitleClick: (id: Int) -> Unit) : RecyclerView.Adapter<SingleTitleRelatedAdapter.ViewHolder>() {
    private var list: List<GetTitlesResponse.Data> = ArrayList()

    fun setRelatedList(list: List<GetTitlesResponse.Data>) {
        this.list = list
        notifyDataSetChanged()
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
        fun bind(model: GetTitlesResponse.Data) {
            if (model.primaryName.isNotEmpty()) {
                view.itemName.text = model.primaryName
            } else {
                view.itemName.text = model.secondaryName
            }

            Picasso.get().load(model.posters?.data?.x240).placeholder(R.drawable.movie_image_placeholder).error(R.drawable.movie_image_placeholder).into(view.itemPoster)

            view.root.setOnClickListener {
                onTitleClick(model.id)
            }
        }
    }

}