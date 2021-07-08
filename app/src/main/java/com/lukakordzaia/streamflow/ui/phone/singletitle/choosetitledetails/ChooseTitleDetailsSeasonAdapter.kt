package com.lukakordzaia.streamflow.ui.phone.singletitle.choosetitledetails

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.lukakordzaia.streamflow.R
import com.lukakordzaia.streamflow.databinding.RvChooseDetailsSeasonItemBinding
import kotlinx.android.synthetic.main.rv_choose_details_season_item.view.*

class ChooseTitleDetailsSeasonAdapter(
        private val context: Context,
        private val onSeasonClick: (seasonId: Int) -> Unit
) : RecyclerView.Adapter<ChooseTitleDetailsSeasonAdapter.ViewHolder>() {
    private var list: List<Int> = ArrayList()
    private var chosenSeason: Int = 1

    fun setSeasonList(list: List<Int>) {
        this.list = list
        notifyDataSetChanged()
    }

    fun setChosenSeason(seasonId: Int) {
        chosenSeason = seasonId
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            RvChooseDetailsSeasonItemBinding.inflate(LayoutInflater.from(context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val seasonModel = list[position]

        holder.bind(seasonModel)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder(val view: RvChooseDetailsSeasonItemBinding) : RecyclerView.ViewHolder(view.root) {
        fun bind(model: Int) {
            val isChosen = model == chosenSeason

            if (isChosen) {
                view.rvSeasonContainer.background = ResourcesCompat.getDrawable(context.resources, R.drawable.rv_choose_details_season_chosen, null)
            } else {
                view.rvSeasonContainer.background = ResourcesCompat.getDrawable(context.resources, R.drawable.rv_choose_details_season, null)
            }

            view.rvSeasonContainer.setOnClickListener {
                onSeasonClick(model)
            }

            view.rvSeasonNumber.text = "სეზონი $model"
        }
    }
}