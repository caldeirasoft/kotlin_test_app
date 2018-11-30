package com.caldeirasoft.basicapp.ui.common

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.*
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.caldeirasoft.basicapp.databinding.FragmentCatalogBinding
import com.caldeirasoft.basicapp.databinding.FragmentEntryListBinding
import com.caldeirasoft.basicapp.ui.viewmodel.ListViewModel
import com.caldeirasoft.basicapp.ui.viewmodel.PagedListViewModel
import com.caldeirasoft.basicapp.util.LoadingState

abstract class ListFragment<T> : BindingFragment<FragmentEntryListBinding>()
{
    private lateinit var progress: View
    private lateinit var btnRetry: View
    private lateinit var errorText: View
    private lateinit var emptyView: View
    private lateinit var loadMoreProgress: ProgressBar
    private lateinit var rvList: RecyclerView
    private lateinit var refreshLayout: SwipeRefreshLayout

    protected abstract val viewModel: ListViewModel<T>
    protected abstract val adapter: ListAdapter<T, out RecyclerView.ViewHolder>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?): View? {
        mBinding = FragmentEntryListBinding.inflate(inflater, container, false).apply {
            setLifecycleOwner(this@ListFragment)
            viewModel = this@ListFragment.viewModel
        }.let {
            progress = it.progress
            btnRetry = it.btnRetry
            errorText = it.textError
            emptyView = it.textEmpty
            rvList = it.rvList
            refreshLayout = it.refreshLayout
            return it.root
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initList()
    }

    private fun initList() {
        setupRecyclerView()
        setupSwipeRefreshLayout()

        // collection without section
        viewModel.data.observe(this@ListFragment, Observer { items ->
            items?.let { list ->
                adapter.submitList(list)
            }
        })

        //state
        viewModel.loadingState.observe(this@ListFragment, Observer { state ->
            showHideViews(state)
        })
    }

    private fun setupRecyclerView() =
            with(mBinding.rvList) {
                layoutManager = LinearLayoutManager(activity)
                addItemDecoration(DividerItemDecoration(activity, LinearLayoutManager.VERTICAL))
                adapter = adapter
            }

    private fun setupSwipeRefreshLayout() =
            with (mBinding.refreshLayout) {
                setOnRefreshListener {
                    viewModel.update()
                    this.isRefreshing = false
                }
            }

    protected open fun showHideViews(state: LoadingState?) {
        if (state == null) {
            return
        }
        progress.visibleGone(state.isLoading)
        emptyView.visibleGone(state.isEmpty)
        errorText.visibleGone(state.isLoadErr)
        btnRetry.visibleGone(state.isLoadErr)
        refreshLayout.visibleGone(state.isOK)
        loadMoreProgress.visibleGone(state.isLoadingMore)
        refreshLayout.isRefreshing = state.isLoading //refreshing
    }
}