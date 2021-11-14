package com.lukakordzaia.streamflowphone.ui.phone.searchtitles

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lukakordzaia.core.network.models.imovies.response.categories.GetTopFranchisesResponse
import com.lukakordzaia.streamflowphone.databinding.RvFranchiseItemBinding

class TopFranchisesAdapter(
    private val context: Context,
    private val onTitleClick: (titleName: String) -> Unit
) : RecyclerView.Adapter<TopFranchisesAdapter.ViewHolder>() {
    private var list: List<GetTopFranchisesResponse.Data> = ArrayList()

    fun setFranchisesList(list: List<GetTopFranchisesResponse.Data>) {
        this.list = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            RvFranchiseItemBinding.inflate(LayoutInflater.from(context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val listModel = list[position]

        holder.bind(listModel)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder(val view: RvFranchiseItemBinding) : RecyclerView.ViewHolder(view.root) {
        fun bind(model: GetTopFranchisesResponse.Data) {
            view.rvFranchiseItemName.text = model.name

            view.root.setOnClickListener {
                onTitleClick(model.name)
            }
        }
    }
}