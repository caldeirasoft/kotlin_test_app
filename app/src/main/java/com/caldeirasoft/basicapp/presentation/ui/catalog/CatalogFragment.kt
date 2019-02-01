package com.caldeirasoft.basicapp.presentation.ui.catalog

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.caldeirasoft.basicapp.R
import com.caldeirasoft.basicapp.databinding.FragmentCatalogBinding
import com.caldeirasoft.basicapp.presentation.ui.base.BindingFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior

import kotlinx.android.synthetic.main.fragment_catalog.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class CatalogFragment : BindingFragment<FragmentCatalogBinding>() {

    var category:Int = 26
    private val mViewModel:CatalogViewModel by viewModel { parametersOf(category)}
    private lateinit var mBottomSheetBehavior: BottomSheetBehavior<View>


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?): View? {
        mBinding = FragmentCatalogBinding.inflate(inflater, container, false)
        mBinding.let {
            it.setLifecycleOwner(this)
            it.viewModel = mViewModel
            return it.root
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolbar()
        setTitle()
        setupRecyclerView()
        setHasOptionsMenu(true)
        setupSwipeRefreshLayout()
    }

    private fun setToolbar() {
        (activity as AppCompatActivity).setSupportActionBar(toolbar);
    }

    private fun setTitle() {
        val strings = resources.getStringArray(R.array.categories_options)
        val codes = resources.getIntArray(R.array.categories_options_ids)
        val codePosition = codes.indexOf(category)
        if (codePosition >= 0)
        {
            val title = strings[codePosition]
            activity?.title = title;
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear();
        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun setupRecyclerView() =
            with(rw_catalog) {
                layoutManager = LinearLayoutManager(activity)
                addItemDecoration(DividerItemDecoration(activity, LinearLayoutManager.VERTICAL))
            }

    private fun setupSwipeRefreshLayout() =
            with (catalog_swipeRefreshLayout) {
                setOnRefreshListener {
                    mViewModel.refresh()
                    this.isRefreshing = false
                }
            }


    private fun updateBottomSheetBehavior(category: Int?) {
        when (category) {
            null -> {
                mBottomSheetBehavior.isHideable = true
                mBottomSheetBehavior.peekHeight = 0
                mBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }
            else -> {
                mBottomSheetBehavior.isHideable = false
                mBottomSheetBehavior.peekHeight = 95
                mBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }
        }
    }

    private fun setupFabClick() {
        with(fab_filtersection_catalog) {
            setOnClickListener { view ->
                mBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
    }


    companion object {
        const val EXTRA_FEED_ID = "FEED_ID"
    }
}