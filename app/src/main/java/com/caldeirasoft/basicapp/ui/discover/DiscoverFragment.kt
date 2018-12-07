package com.caldeirasoft.basicapp.ui.catalog

import android.animation.ObjectAnimator
import android.animation.StateListAnimator
import android.os.Bundle
import android.view.*
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.avast.android.githubbrowser.extensions.start
import com.caldeirasoft.basicapp.R
import com.caldeirasoft.basicapp.data.entity.Podcast
import com.caldeirasoft.basicapp.databinding.FragmentDiscoverBinding
import com.caldeirasoft.basicapp.ui.adapter.ItemViewClickListener
import com.caldeirasoft.basicapp.ui.common.BindingFragment
import com.caldeirasoft.basicapp.ui.discover.DiscoverSliderPagerAdapter
import com.caldeirasoft.basicapp.ui.discover.DiscoverSnapAdapter
import com.caldeirasoft.basicapp.ui.discover.Snap
import com.caldeirasoft.basicapp.ui.home.IMainFragment
import com.caldeirasoft.basicapp.ui.podcastdetail.PodcastDetailActivity
import com.caldeirasoft.basicapp.viewModelProviders
import com.ethanhua.skeleton.RecyclerViewSkeletonScreen
import com.ethanhua.skeleton.Skeleton
import com.google.android.material.bottomsheet.BottomSheetBehavior

import kotlinx.android.synthetic.main.fragment_discover.*

class DiscoverFragment
    : BindingFragment<FragmentDiscoverBinding>(), IMainFragment, ItemViewClickListener<Podcast> {

    var category:Int = 26
    private val viewModel by lazy { viewModelProviders<DiscoverViewModel>() }
    private lateinit var mBottomSheetBehavior: BottomSheetBehavior<View>
    private val discoverAdapter = DiscoverSnapAdapter(lifecycleOwner = this)
    lateinit private var viewPagerAdapter: DiscoverSliderPagerAdapter
    private var skeletonScreen: RecyclerViewSkeletonScreen? = null

    override fun getMenuItem() = R.id.bb_menu_catalog

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?): View? {
        mBinding = FragmentDiscoverBinding.inflate(inflater, container, false).apply {
            setLifecycleOwner(this@DiscoverFragment)
            viewModel = this@DiscoverFragment.viewModel
        }
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAppBar()
        setTitle()
        setupViewPager()
        setupRecyclerView()
        setHasOptionsMenu(true)
        observeCatalog()
    }

    private fun setAppBar() {
       StateListAnimator().apply {
           addState(IntArray(0), ObjectAnimator.ofFloat(discover_appbar_layout, "elevation", 0.1f))
           discover_appbar_layout.stateListAnimator = this
       }
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

    private fun setupViewPager() {
        viewPagerAdapter = DiscoverSliderPagerAdapter(context = this.requireContext(), fm = this.childFragmentManager)
        with(viewpager_slider) {
            adapter = viewPagerAdapter
        }
    }

    private fun setupRecyclerView() {
        with(recyclerView) {
            layoutManager = LinearLayoutManager(activity)
            setHasFixedSize(true)
            skeletonScreen = Skeleton.bind(this)
                    .adapter(discoverAdapter)
                    .load(R.layout.listitem_snap_discover_skeleton)
                    .show()
        }
    }

    private fun observeCatalog() {
        category.let { it ->
            //viewModel
            viewModel.apply {
                // header
                this.trendingPodcasts.observe(this@DiscoverFragment, Observer { list ->
                    list?.let { items ->
                        if (::viewPagerAdapter.isInitialized)
                            viewPagerAdapter.submitPodcasts(items)
                    }
                })

                // groups
                this.podcastGroups.observe(this@DiscoverFragment, Observer { list ->
                    list?.let { groups ->
                        skeletonScreen?.hide()
                        groups.forEach { item ->
                            // add snap to adapter
                            discoverAdapter.addSnap(Snap(Gravity.START, item.name, false, item))
                            // request group
                            viewModel.requestGroup(item)
                        }
                    }
                })

                // request
                this.request()
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