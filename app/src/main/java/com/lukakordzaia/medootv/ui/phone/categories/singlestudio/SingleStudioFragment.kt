package com.lukakordzaia.medootv.ui.phone.categories.singlestudio

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.lukakordzaia.medootv.R
import com.lukakordzaia.medootv.ui.phone.categories.singlegenre.SingleCategoryAdapter
import com.lukakordzaia.medootv.ui.phone.categories.singlegenre.SingleCategoryViewModel
import com.lukakordzaia.medootv.utils.*
import kotlinx.android.synthetic.main.phone_single_category_fragment.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class SingleStudioFragment : Fragment(R.layout.phone_single_category_fragment) {
    private val singleGenreViewModel by viewModel<SingleCategoryViewModel>()
    private lateinit var singleCategoryAdapter: SingleCategoryAdapter
    private val args: SingleStudioFragmentArgs by navArgs()
    private var page = 1

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        singleGenreViewModel.getSingleStudio(args.studioId, page)

        val layoutManager = GridLayoutManager(requireActivity(), 2, GridLayoutManager.VERTICAL, false)

        singleGenreViewModel.isLoading.observe(viewLifecycleOwner, EventObserver {
            if (!it) {
                single_category_progressBar.setGone()
            }
        })

        singleCategoryAdapter = SingleCategoryAdapter(requireContext()) {
            singleGenreViewModel.onSingleTitlePressed(it, AppConstants.NAV_STUDIO_TO_SINGLE)
        }
        rv_single_category.adapter = singleCategoryAdapter
        rv_single_category.layoutManager = layoutManager

        singleGenreViewModel.singleStudioList.observe(viewLifecycleOwner, {
            singleCategoryAdapter.setGenreTitleList(it)
        })

        singleGenreViewModel.hasMorePage.observe(viewLifecycleOwner, {
            if (it) {
                infiniteScroll(single_category_nested_scroll) { fetchMoreTitle() }
            }
        })

        singleGenreViewModel.navigateScreen.observe(viewLifecycleOwner, EventObserver {
            navController(it)
        })
    }

    private fun fetchMoreTitle() {
        single_category_progressBar.setVisible()
        page++
        Log.d("currentpage", page.toString())
        singleGenreViewModel.getSingleGenre(args.studioId, page)
    }
}