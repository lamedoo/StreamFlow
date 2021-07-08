package com.lukakordzaia.streamflow.ui.phone.searchtitles

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lukakordzaia.streamflow.R
import com.lukakordzaia.streamflow.databinding.RvSearchItemBinding
import com.lukakordzaia.streamflow.network.models.imovies.response.titles.GetTitlesResponse
import com.squareup.picasso.Picasso

class SearchTitlesAdapter(private val context: Context, private val onTitleClick: (id : Int) -> Unit) : RecyclerView.Adapter<SearchTitlesAdapter.ViewHolder>() {
    private var list: List<GetTitlesResponse.Data> = ArrayList()

    fun setSearchTitleList(list: List<GetTitlesResponse.Data>) {
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
        fun bind(model: GetTitlesResponse.Data) {
            if (!model.secondaryName.isNullOrEmpty()) {
                view.rvSearchItemName.text = model.secondaryName
            } else {
                view.rvSearchItemName.text = model.primaryName
            }

            view.rvSearchItemYear.text = "${model.year.toString()}, "

            if (!model.posters?.data?.x240.isNullOrEmpty()) {
                Picasso.get().load(model.posters?.data?.x240).placeholder(R.drawable.movie_image_placeholder).error(R.drawable.movie_image_placeholder).into(view.rvSearchItemPoster)
            } else {
                Picasso.get().load(R.drawable.movie_image_placeholder).placeholder(R.drawable.movie_image_placeholder).into(view.rvSearchItemPoster)

            }

            if (model.isTvShow == true) {
                view.rvSearchItemIsTvShow.text = "სერიალი"
            } else {
                view.rvSearchItemIsTvShow.text = "ფილმი"
            }

            view.root.setOnClickListener {
                onTitleClick(model.id)
            }
        }
    }
}