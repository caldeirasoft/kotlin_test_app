package com.caldeirasoft.basicapp.ui.discover

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.caldeirasoft.basicapp.R
import com.caldeirasoft.basicapp.data.entity.Podcast
import com.caldeirasoft.basicapp.extensions.withArgs
import com.caldeirasoft.basicapp.ui.common.BaseFragment
import com.caldeirasoft.basicapp.ui.common.RecyclerHeaderHelper
import com.caldeirasoft.basicapp.ui.extensions.addFragment
import com.caldeirasoft.basicapp.ui.extensions.observeK
import com.caldeirasoft.basicapp.ui.extensions.viewModelProviders
import com.caldeirasoft.basicapp.ui.home.IMainFragment
import com.caldeirasoft.basicapp.ui.podcastinfo.PodcastInfoFragment
import com.ethanhua.skeleton.RecyclerViewSkeletonScreen
import kotlinx.android.synthetic.main.fragment_discover.*

class DiscoverFragment : BaseFragment(), IMainFragment, DiscoverController.Callbacks {

    var category:Int = 26
    private var skeletonScreen: RecyclerViewSkeletonScreen? = null
    private val viewModel by lazy { viewModelProviders<DiscoverViewModel>() }
    private val controller = DiscoverController(this)

    override fun getLayout(): Int = R.layout.fragment_discover

    override fun getMenuItem() = R.id.bb_menu_catalog

    override fun onCreate() {
        initObservers()
        initUi()
        viewModel.request()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.itunesStore.removeObservers(this@DiscoverFragment)
    }

    private fun initObservers() {
        viewModel.itunesStore.observeK(this) {
            controller.setData(it)
        }
    }

    private fun initUi() {
        recyclerView.setController(controller)
    }

    override fun onPodcastClick(podcast: Podcast) {
        PodcastInfoFragment().let {
            it.withArgs(EXTRA_FEED_ID to podcast)
            this.activity?.addFragment(it, "podcastinfo" + podcast.feedUrl, true)
        }
    }

    fun onItemClick(item: Podcast?, position: Int, viewId: Int) {
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