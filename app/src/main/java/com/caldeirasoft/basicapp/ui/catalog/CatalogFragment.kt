package com.caldeirasoft.basicapp.ui.catalog

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.databinding.library.baseAdapters.BR
import com.avast.android.githubbrowser.extensions.start
import com.caldeirasoft.basicapp.R
import com.caldeirasoft.basicapp.data.entity.Podcast
import com.caldeirasoft.basicapp.data.enum.SectionState
import com.caldeirasoft.basicapp.data.repository.NetworkState
import com.caldeirasoft.basicapp.databinding.FragmentCatalogBinding
import com.caldeirasoft.basicapp.databinding.ListitemCatalogBinding
import com.caldeirasoft.basicapp.ui.adapter.ItemViewClickListener
import com.caldeirasoft.basicapp.ui.adapter.SimplePagedDataBindingAdapter
import com.caldeirasoft.basicapp.ui.base.BaseFragment
import com.caldeirasoft.basicapp.ui.base.BindingFragment
import com.caldeirasoft.basicapp.ui.home.IMainFragment
import com.caldeirasoft.basicapp.ui.podcastdetail.PodcastDetailActivity
import com.caldeirasoft.basicapp.viewModelProviders
import com.google.android.material.bottomsheet.BottomSheetBehavior

import kotlinx.android.synthetic.main.fragment_catalog.*
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.android.UI

class CatalogFragment : BindingFragment<FragmentCatalogBinding>(), IMainFragment, ItemViewClickListener<Podcast> {

    private val viewModel by lazy { viewModelProviders<CatalogViewModel>() }
    private lateinit var mBottomSheetBehavior: BottomSheetBehavior<View>
    private val catalogAdapter by lazy {
        SimplePagedDataBindingAdapter<Podcast, ListitemCatalogBinding>(
                layoutId = R.layout.listitem_catalog,
                variableId = BR.podcast,
                itemViewClickListener = this,
                lifecycleOwner = this,
                clickAwareViewIds = *intArrayOf(R.id.itemCatalogLinearLayout))
    }

    var category:Int = 26

    override fun getMenuItem() = R.id.bb_menu_catalog

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?): View? {
        mBinding = FragmentCatalogBinding.inflate(inflater, container, false).apply {
            setLifecycleOwner(this@CatalogFragment)
            viewModel = this@CatalogFragment.viewModel
        }
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolbar()
        setTitle()
        setupRecyclerView()
        setHasOptionsMenu(true)
        setupSwipeRefreshLayout()
        observeCatalog()
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

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        menu?.clear();
        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun setupRecyclerView() =
            with(rw_catalog) {
                layoutManager = LinearLayoutManager(activity)
                addItemDecoration(DividerItemDecoration(activity, LinearLayoutManager.VERTICAL))
                adapter = catalogAdapter
            }

    private fun setupSwipeRefreshLayout() =
            with (catalog_swipeRefreshLayout) {
                setOnRefreshListener {
                    viewModel.refresh()
                    this.isRefreshing = false
                }
            }

    private fun observeCatalog() {
        category.let { it ->
            //viewModel
            viewModel.apply {

                //set category
                setCategory(it)

                // collection
                podcasts.observe(this@CatalogFragment, Observer { podcasts ->
                    catalogAdapter.submitList(podcasts)
                })

                // update section
                catalogCategory.observe(this@CatalogFragment, Observer { section ->
                    //updateBottomSheetBehavior(section)
                    //updateBottomSheetFragmentSelectedIndex(section)
                })

                // network updates
                loadingState.observe(this@CatalogFragment, Observer { state ->
                    state?.let {
                        catalogAdapter.setState(state)
                    }
                })
            }
        }
    }

    private fun setupBottomSheet() {
        /*
        val fm = childFragmentManager
        val bottomSheet:View? = this.activity?.findViewById<View>(R.id.bottom_sheet_filtersection)
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)

        var bottomSheetFragment = fm.findFragmentByTag("bottomSheetFragment") as CategoryFilterFragment?
        if (bottomSheetFragment == null) {
            bottomSheetFragment = CategoryFilterFragment().apply {
                setOnClickListener(listener = object : ItemViewClickListener<String> {
                    override fun onItemClick(item: String?, position: Int, viewId: Int) {
                        when (position) {
                            0 -> viewModel.setCategory(26)
                            1 -> viewModel.setSection(SectionState.QUEUE.value)
                            2 -> viewModel.setSection(SectionState.INBOX.value)
                            3 -> viewModel.setSection(SectionState.FAVORITE.value)
                            4 -> viewModel.setSection(SectionState.HISTORY.value)
                            else -> { }
                        }
                    }
                })
                setCloseListener(listener = object : View.OnClickListener {
                    override fun onClick(p0: View?) {
                        viewModel.setCategory(26)
                    }
                })
            }

            fm.beginTransaction().let {
                it.add(bottomSheet!!.id, bottomSheetFragment, "bottomSheetFragment")
                it.commit()
            }
            fm.executePendingTransactions()
        }

        mBottomSheetBehavior.apply {
            state = BottomSheetBehavior.STATE_COLLAPSED
            bottomSheetFragment.setBottomSheetBehavior(this)
        }
        */
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

    private fun updateBottomSheetFragmentSelectedIndex(category: Int?) {
        /*
        val fm = childFragmentManager
        var bottomSheetFragment = fm.findFragmentByTag("bottomSheetFragment") as CategoryFilterFragment?
        bottomSheetFragment?.let {
            when (category) {
                null -> it.setSelectedIndex(0)
                SectionState.QUEUE.value -> it.setSelectedIndex(1)
                SectionState.INBOX.value -> it.setSelectedIndex(2)
                SectionState.FAVORITE.value -> it.setSelectedIndex(3)
                SectionState.HISTORY.value -> it.setSelectedIndex(4)
                else -> {}
            }
        }
        */
    }

    private fun setupFabClick() {
        with(fab_filtersection_catalog) {
            setOnClickListener { view ->
                mBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
    }


    override fun onItemClick(item: Podcast?, position: Int, viewId: Int) {
        item?.let {
            activity?.start<PodcastDetailActivity>(Bundle().apply {
               putParcelable(EXTRA_FEED_ID, it)
            })
        }
    }

    companion object {
        const val EXTRA_FEED_ID = "FEED_ID"
    }
}