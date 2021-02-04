package com.lukakordzaia.imoviesapp.ui.phone.singletitle.tabs

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import kotlinx.android.synthetic.main.phone_single_title_fragment.view.*


//class TabsPagerAdapter(fm: FragmentManager,
//                       lifecycle: Lifecycle,
//                       private var numberOfTabs: Int,
//                       private val titleId: Int) : FragmentStateAdapter(fm, lifecycle) {
//
//    override fun createFragment(position: Int): Fragment {
//        when (position) {
//            0 -> {
//                val bundle = Bundle()
//                bundle.putString("fragmentName", "Info Fragment")
//                bundle.putString("titleId", titleId.toString())
//                val infoFragment = SingleTitleFragmentInfo()
//                infoFragment.arguments = bundle
//                return infoFragment
//            }
//            1 -> {
//                val bundle = Bundle()
//                bundle.putString("fragmentName", "Movies Fragment")
//                bundle.putString("titleId", titleId.toString())
//                val similaFragment = SingleTitleFragmentSimilar()
//                similaFragment.arguments = bundle
//                return similaFragment
//            }
//            else -> return SingleTitleFragmentInfo()
//        }
//    }
//
//    override fun getItemCount(): Int {
//        return numberOfTabs
//    }
//}

class TabsPagerAdapter(private val activity: Activity) : PagerAdapter() {
    override fun getCount(): Int {
        return 2
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        return when (position) {
            0  -> {
                container.page_one
            }
            1 -> {
                container.page_two
            }
            else -> {
                container.page_one
            }
        }
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }
}