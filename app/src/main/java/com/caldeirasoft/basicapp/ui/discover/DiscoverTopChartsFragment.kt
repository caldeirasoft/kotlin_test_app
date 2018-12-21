package com.caldeirasoft.basicapp.ui.discover

import android.animation.ObjectAnimator
import android.animation.StateListAnimator
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.avast.android.githubbrowser.extensions.start
import com.caldeirasoft.basicapp.BR
import com.caldeirasoft.basicapp.R
import com.caldeirasoft.basicapp.data.entity.Podcast
import com.caldeirasoft.basicapp.data.repository.PodcastArtwork
import com.caldeirasoft.basicapp.databinding.FragmentDiscoverChartsBinding
import com.caldeirasoft.basicapp.databinding.FragmentDiscoverForyouBinding
import com.caldeirasoft.basicapp.databinding.ListitemCatalogBinding
import com.caldeirasoft.basicapp.ui.adapter.ItemViewClickListener
import com.caldeirasoft.basicapp.ui.adapter.SimplePagedDataBindingAdapter
import com.caldeirasoft.basicapp.ui.catalog.CatalogViewModel
import com.caldeirasoft.basicapp.ui.common.BindingFragment
import com.caldeirasoft.basicapp.ui.discover.DiscoverViewModel
import com.caldeirasoft.basicapp.ui.home.IMainFragment
import com.caldeirasoft.basicapp.ui.podcastdetail.PodcastDetailActivity
import com.ethanhua.skeleton.RecyclerViewSkeletonScreen
import com.ethanhua.skeleton.Skeleton
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.fragment_discover_charts.*
import kotlinx.android.synthetic.main.fragment_discover_foryou.*

class DiscoverTopChartsFragment
    : BindingFragment<FragmentDiscoverChartsBinding>(), ItemViewClickListener<Podcast> {

    var category:Int = 26
    private val viewModel : CatalogViewModel
            by lazy { ViewModelProviders.of(this).get(CatalogViewModel::class.java) }
    private lateinit var mBottomSheetBehavior: BottomSheetBehavior<View>
    private val catalogAdapter by lazy {
        SimplePagedDataBindingAdapter<Podcast, ListitemCatalogBinding>(
                layoutId = R.layout.listitem_catalog,
                variableId = BR.podcast,
                itemViewClickListener = this,
                lifecycleOwner = this,
                clickAwareViewIds = *intArrayOf(R.id.itemCatalogLinearLayout))
    }

    private var skeletonScreen: RecyclerViewSkeletonScreen? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?): View? {
        mBinding = FragmentDiscoverChartsBinding.inflate(inflater, container, false)
                .apply {
                    setLifecycleOwner(this@DiscoverTopChartsFragment)
                    viewModel = this@DiscoverTopChartsFragment.viewModel
                }
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setHasOptionsMenu(true)
        setupSwipeRefreshLayout()
        observeCatalog()
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
                    //viewModel.refresh()
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
                data.observe(this@DiscoverTopChartsFragment, Observer { podcasts ->
                    catalogAdapter.submitList(podcasts)
                })

                // update section
                catalogCategory.observe(this@DiscoverTopChartsFragment, Observer { section ->
                    //updateBottomSheetBehavior(section)
                    //updateBottomSheetFragmentSelectedIndex(section)
                })

                // network updates
                loadingState.observe(this@DiscoverTopChartsFragment, Observer { state ->
                    state?.let {
                        //catalogAdapter.setState(state)
                    }
                })
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
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment ProfileFragment.
         */
        @JvmStatic
        fun newInstance() = DiscoverTopChartsFragment()
    }
}