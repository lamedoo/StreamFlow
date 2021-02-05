package com.lukakordzaia.imoviesapp.ui.phone.singletitle.tabs

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter


class TabsPagerAdapter(fm: FragmentManager,
                       lifecycle: Lifecycle,
                       private var numberOfTabs: Int,
                       private val titleId: Int) : FragmentStateAdapter(fm, lifecycle) {

    override fun createFragment(position: Int): Fragment {
        when (position) {
            0 -> {
                val bundle = Bundle()
                bundle.putString("fragmentName", "Info Fragment")
                bundle.putString("titleId", titleId.toString())
                val infoFragment = SingleTitleFragmentInfo()
                infoFragment.arguments = bundle
                return infoFragment
            }
            1 -> {
                val bundle = Bundle()
                bundle.putString("fragmentName", "Movies Fragment")
                bundle.putString("titleId", titleId.toString())
                val similarFragment = SingleTitleFragmentSimilar()
                similarFragment.arguments = bundle
                return similarFragment
            }
            else -> return SingleTitleFragmentInfo()
        }
    }

    override fun getItemCount(): Int {
        return numberOfTabs
    }
}