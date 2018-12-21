package com.caldeirasoft.basicapp.ui.discover

import android.animation.ObjectAnimator
import android.animation.StateListAnimator
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.avast.android.githubbrowser.extensions.start
import com.caldeirasoft.basicapp.R
import com.caldeirasoft.basicapp.data.entity.Podcast
import com.caldeirasoft.basicapp.databinding.FragmentDiscoverBinding
import com.caldeirasoft.basicapp.ui.adapter.ItemViewClickListener
import com.caldeirasoft.basicapp.ui.common.BindingFragment
import com.caldeirasoft.basicapp.ui.home.IMainFragment
import com.caldeirasoft.basicapp.ui.podcastdetail.PodcastDetailActivity
import com.caldeirasoft.basicapp.viewModelProviders
import com.ethanhua.skeleton.RecyclerViewSkeletonScreen
import kotlinx.android.synthetic.main.fragment_discover.*

class DiscoverFragment
    : BindingFragment<FragmentDiscoverBinding>(), IMainFragment, ItemViewClickListener<Podcast> {

    var category:Int = 26
    private val viewModel by lazy { viewModelProviders<DiscoverViewModel>() }
    lateinit private var viewPagerAdapter: DiscoverPagerAdapter

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
        viewPagerAdapter = DiscoverPagerAdapter(context = this.requireContext(), fm = this.childFragmentManager)
        with(viewpager_discover) {
            adapter = viewPagerAdapter
            tabs_discover.setupWithViewPager(this)
        }
    }

    private fun observeCatalog() {
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