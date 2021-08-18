package com.lukakordzaia.streamflow.ui.phone.phonesingletitle

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.lukakordzaia.streamflow.R
import com.lukakordzaia.streamflow.databinding.RvSingleTitleCastItemBinding
import com.lukakordzaia.streamflow.network.models.imovies.response.singletitle.GetSingleTitleCastResponse
import com.lukakordzaia.streamflow.utils.setImage

class PhoneSingleTitleCastAdapter(private val context: Context, private val onCastClick: (name: String) -> Unit) : RecyclerView.Adapter<PhoneSingleTitleCastAdapter.ViewHolder>() {
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
                view.rvCastItemPoster.setImage(model.poster, true)
            } else {
                Glide.with(context)
                    .load(R.drawable.no_profile_picture)
                    .into(view.rvCastItemPoster)
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