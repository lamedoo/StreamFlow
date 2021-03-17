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
import kotlinx.android.synthetic.main.rv_choose_details_season_item.view.*

class ChooseTitleDetailsSeasonAdapter(
        private val context: Context,
        private val onSeasonClick: (seasonId: Int) -> Unit,
        private val onSeasonChosen: (chosenSeason: Int) -> Unit
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
            LayoutInflater.from(context).inflate(R.layout.rv_choose_details_season_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val seasonModel = list[position]

        val isChosen = seasonModel == chosenSeason

        if (isChosen) {
            holder.seasonContainer.background = ResourcesCompat.getDrawable(context.resources, R.drawable.rv_choose_details_season_chosen, null)
        } else {
            holder.seasonContainer.background = ResourcesCompat.getDrawable(context.resources, R.drawable.rv_choose_details_season, null)
        }

        holder.seasonContainer.setOnClickListener {
            onSeasonClick(seasonModel)
            onSeasonChosen(position)
        }

        holder.seasonNumberTextView.text = "სეზონი $seasonModel"
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val seasonContainer: ConstraintLayout = view.rv_season_container
        val seasonNumberTextView: TextView = view.rv_season_number
    }
}