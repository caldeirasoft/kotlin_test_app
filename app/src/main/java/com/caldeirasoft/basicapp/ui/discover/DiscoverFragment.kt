package com.caldeirasoft.basicapp.ui.discover

import android.animation.ObjectAnimator
import android.animation.StateListAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.avast.android.githubbrowser.extensions.start
import com.caldeirasoft.basicapp.R
import com.caldeirasoft.basicapp.data.entity.Podcast
import com.caldeirasoft.basicapp.databinding.FragmentDiscoverBinding
import com.caldeirasoft.basicapp.extensions.withArgs
import com.caldeirasoft.basicapp.ui.adapter.ItemViewClickListener
import com.caldeirasoft.basicapp.ui.common.BindingFragment
import com.caldeirasoft.basicapp.ui.extensions.addFragment
import com.caldeirasoft.basicapp.ui.home.IMainFragment
import com.caldeirasoft.basicapp.ui.podcastdetail.PodcastDetailActivity
import com.caldeirasoft.basicapp.ui.extensions.viewModelProviders
import com.caldeirasoft.basicapp.ui.podcastinfo.PodcastInfoFragment
import com.ethanhua.skeleton.RecyclerViewSkeletonScreen
import com.ethanhua.skeleton.Skeleton
import kotlinx.android.synthetic.main.fragment_discover.*

class DiscoverFragment
    : BindingFragment<FragmentDiscoverBinding>(), IMainFragment, ItemViewClickListener<Podcast> {

    var category:Int = 26
    private var skeletonScreen: RecyclerViewSkeletonScreen? = null
    private val viewModel by lazy { viewModelProviders<DiscoverViewModel>() }
    lateinit private var viewPagerAdapter: DiscoverInfiniteAdapter
    private val discoverAdapter: DiscoverSnapAdapter by lazy {
        DiscoverSnapAdapter(lifecycleOwner = this, itemViewClickListener = this)
    }


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
        setupViewPager()
        setupRecyclerView()
        observeCatalog()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.trendingPodcasts.removeObservers(this@DiscoverFragment)
        viewModel.podcastGroups.removeObservers(this@DiscoverFragment)
    }


    private fun setupViewPager() {
        viewPagerAdapter = DiscoverInfiniteAdapter(context = this.requireContext())
        with(viewpager_slider) {
            adapter = viewPagerAdapter
            pageMargin = 24
            setPadding(48, 10, 48, 10)
            clipToPadding = false
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
        //viewModel
        viewModel.apply {
            // header
            this.trendingPodcasts.observe(this@DiscoverFragment, Observer { list ->
                list?.let { items ->
                    if (::viewPagerAdapter.isInitialized) {
                        viewPagerAdapter.setItemList(items)
                        viewpager_slider.setCurrentItem(1, false)
                    }
                }
            })

            // groups
            this.podcastGroups.observe(this@DiscoverFragment, Observer { list ->
                skeletonScreen?.hide()
                discoverAdapter.submitList(list)
            })

            // request
            this.request()
        }
    }

    override fun onItemClick(item: Podcast?, position: Int, viewId: Int) {
        item?.let { podcast ->
            PodcastInfoFragment().let {
                it.withArgs(EXTRA_FEED_ID to podcast)
                this.activity?.addFragment(it, "podcastinfo" + podcast.feedUrl, true)
            }
        }
    }

    companion object {
        const val EXTRA_FEED_ID = "FEED_ID"
    }
}