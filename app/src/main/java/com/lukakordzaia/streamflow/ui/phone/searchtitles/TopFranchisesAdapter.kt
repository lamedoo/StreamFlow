package com.lukakordzaia.streamflow.ui.phone.searchtitles

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.lukakordzaia.streamflow.R
import com.lukakordzaia.streamflow.datamodels.FranchiseList
import com.lukakordzaia.streamflow.datamodels.TitleList
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.rv_franchise_item.view.*
import kotlinx.android.synthetic.main.rv_search_item.view.*

class TopFranchisesAdapter(private val context: Context, private val onTitleClick: (titleName : String, position: Int) -> Unit) : RecyclerView.Adapter<TopFranchisesAdapter.ViewHolder>() {
    private var list: List<FranchiseList.Data> = ArrayList()

    fun setFranchisesList(list: List<FranchiseList.Data>) {
        this.list = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.rv_franchise_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val listModel = list[position]

        holder.titleRoot.setOnClickListener {
            onTitleClick(listModel.name, position)
        }

        holder.titleNameGeoTextView.text = listModel.name

    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleRoot: ConstraintLayout = view.rv_franchise_item_root
        val titleNameGeoTextView: TextView = view.rv_franchise_item_name_geo
    }
}