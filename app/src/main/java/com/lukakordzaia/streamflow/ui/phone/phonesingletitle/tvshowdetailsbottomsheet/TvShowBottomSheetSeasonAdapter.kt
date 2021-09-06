package com.lukakordzaia.streamflow.ui.phone.phonesingletitle.tvshowdetailsbottomsheet

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.lukakordzaia.streamflow.R
import com.lukakordzaia.streamflow.databinding.RvChooseDetailsSeasonItemBinding
import com.lukakordzaia.streamflow.utils.setDrawableBackground

class TvShowBottomSheetSeasonAdapter(
        private val context: Context,
        private val onSeasonClick: (seasonId: Int) -> Unit
) : RecyclerView.Adapter<TvShowBottomSheetSeasonAdapter.ViewHolder>() {
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
                view.rvSeasonContainer.setDrawableBackground(R.drawable.background_rv_season_current_phone)
                view.rvSeasonContainer.setDrawableBackground(R.drawable.background_rv_season_phone)
            }

            view.rvSeasonContainer.setOnClickListener {
                onSeasonClick(model)
            }

            view.rvSeasonNumber.text = "სეზონი $model"
        }
    }
}