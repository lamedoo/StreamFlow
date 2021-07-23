package com.lukakordzaia.streamflow.ui.phone.singletitle

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lukakordzaia.streamflow.R
import com.lukakordzaia.streamflow.databinding.RvSingleTitleCastItemBinding
import com.lukakordzaia.streamflow.network.models.imovies.response.singletitle.GetSingleTitleCastResponse
import com.squareup.picasso.Picasso

class SingleTitleCastAdapter(private val context: Context, private val onCastClick: (name: String) -> Unit) : RecyclerView.Adapter<SingleTitleCastAdapter.ViewHolder>() {
    private var list: List<GetSingleTitleCastResponse.Data> = ArrayList()

    fun setCastList(list: List<GetSingleTitleCastResponse.Data>) {
        this.list = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
                RvSingleTitleCastItemBinding.inflate(LayoutInflater.from(context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val listModel = list[position]

        holder.bind(listModel)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder(val view: RvSingleTitleCastItemBinding) : RecyclerView.ViewHolder(view.root) {
        fun bind(model: GetSingleTitleCastResponse.Data) {
            if (model.poster.isNotEmpty()) {
                Picasso.get().load(model.poster).into(view.rvCastItemPoster)
            } else {
                Picasso.get().load(R.drawable.no_profile_picture).into(view.rvCastItemPoster)
            }

            if (model.primaryName.isNotEmpty()) {
                view.rvCastItemName.text = model.primaryName
            } else {
                view.rvCastItemName.text = model.originalName
            }

            view.root.setOnClickListener {
                onCastClick(model.originalName)
            }
        }
    }

}