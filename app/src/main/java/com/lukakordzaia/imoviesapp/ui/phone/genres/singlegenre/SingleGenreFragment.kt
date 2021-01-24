package com.lukakordzaia.imoviesapp.ui.phone.genres.singlegenre

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.lukakordzaia.imoviesapp.R
import com.lukakordzaia.imoviesapp.utils.EventObserver
import com.lukakordzaia.imoviesapp.utils.navController
import com.lukakordzaia.imoviesapp.utils.setGone
import com.lukakordzaia.imoviesapp.utils.setVisible
import kotlinx.android.synthetic.main.fragment_single_genre.*

class SingleGenreFragment : Fragment(R.layout.fragment_single_genre) {
    private lateinit var viewModel: SingleGenreViewModel
    private lateinit var singleGenreAdapter: SingleGenreAdapter
    private val args: SingleGenreFragmentArgs by navArgs()
    private var page = 1

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(SingleGenreViewModel::class.java)
        viewModel.getSingleGenre(args.genreId, page)

        val layoutManager = GridLayoutManager(requireActivity(), 2, GridLayoutManager.VERTICAL, false)

        viewModel.isLoading.observe(viewLifecycleOwner, EventObserver {
            if (!it) {
                single_genre_progressBar.setGone()
            }
        })

        singleGenreAdapter = SingleGenreAdapter(requireContext()) {
            viewModel.onSingleTitlePressed(it)
        }
        rv_single_genre.adapter = singleGenreAdapter
        rv_single_genre.layoutManager = layoutManager

        viewModel.singleGenreList.observe(viewLifecycleOwner, {
            singleGenreAdapter.setGenreTitleList(it)
        })

        viewModel.hasMorePage.observe(viewLifecycleOwner, {
            if (it) {
                singlegenre_nested_scroll.setOnScrollChangeListener {
                        v: NestedScrollView, _: Int, scrollY: Int, _: Int, oldScrollY: Int ->

                    if (scrollY == v.getChildAt(v.childCount - 1).measuredHeight - v.measuredHeight &&
                        scrollY > oldScrollY) {

                        val visibleItemCount = layoutManager.childCount;
                        val totalItemCount = layoutManager.itemCount;
                        val pastVisibleItems = layoutManager.findFirstVisibleItemPosition();

                        if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                            single_genre_progressBar.setVisible()
                            page++
                            Log.d("currentpage", page.toString())
                            viewModel.getSingleGenre(args.genreId, page)
                        }
                    }
                }
            }
        })

        viewModel.navigateScreen.observe(viewLifecycleOwner, EventObserver {
            navController(it)
        })
    }
}