package com.lukakordzaia.streamflow.ui.phone.categories

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.lukakordzaia.streamflow.R
import com.lukakordzaia.streamflow.databinding.RvStudioItemBinding
import com.lukakordzaia.streamflow.datamodels.StudioList
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.rv_studio_item.view.*

class StudiosAdapter(private val context: Context, private val onStudiosClick: (studioId: Int, studioName: String) -> Unit) : RecyclerView.Adapter<StudiosAdapter.ViewHolder>() {
    private var list: List<StudioList.Data> = ArrayList()

    fun setStudioList(list: List<StudioList.Data>) {
        this.list = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            RvStudioItemBinding.inflate(LayoutInflater.from(context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val studioModel = list[position]

        holder.bind(studioModel)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder(val view: RvStudioItemBinding) : RecyclerView.ViewHolder(view.root) {
        fun bind(model: StudioList.Data) {
            Picasso.get().load(model.poster).into(view.rvStudioPoster)
            view.rvStudioPoster.setOnClickListener {
                onStudiosClick(model.id, model.name)
            }
        }
    }
}