package com.lukakordzaia.streamflow.ui.phone.searchtitles

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lukakordzaia.streamflow.databinding.RvFranchiseItemBinding
import com.lukakordzaia.streamflow.network.models.response.categories.GetTopFranchisesResponse

class TopFranchisesAdapter(
    private val context: Context,
    private val onTitleClick: (titleName: String, position: Int) -> Unit
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

        holder.bind(listModel, position)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder(val view: RvFranchiseItemBinding) : RecyclerView.ViewHolder(view.root) {
        fun bind(model: GetTopFranchisesResponse.Data, position: Int) {
            view.rvFranchiseItemName.text = model.name

            view.root.setOnClickListener {
                onTitleClick(model.name, position)
            }
        }
    }
}