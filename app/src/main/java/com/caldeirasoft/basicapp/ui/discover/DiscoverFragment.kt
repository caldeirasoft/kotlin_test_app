package com.caldeirasoft.basicapp.ui.discover

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.caldeirasoft.basicapp.R
import com.caldeirasoft.basicapp.data.entity.Podcast
import com.caldeirasoft.basicapp.databinding.FragmentDiscoverBinding
import com.caldeirasoft.basicapp.extensions.withArgs
import com.caldeirasoft.basicapp.ui.common.BindingFragment
import com.caldeirasoft.basicapp.ui.extensions.addFragment
import com.caldeirasoft.basicapp.ui.extensions.observeK
import com.caldeirasoft.basicapp.ui.extensions.viewModelProviders
import com.caldeirasoft.basicapp.ui.home.IMainFragment
import com.caldeirasoft.basicapp.ui.podcastinfo.PodcastInfoFragment
import com.ethanhua.skeleton.RecyclerViewSkeletonScreen
import kotlinx.android.synthetic.main.fragment_discover.*

class DiscoverFragment :
        BindingFragment<FragmentDiscoverBinding>(),
        IMainFragment, DiscoverController.Callbacks {
    private val mViewModel by lazy { viewModelProviders<DiscoverViewModel>() }
    private val controller = DiscoverController(this)

    override fun getMenuItem() = R.id.bb_menu_catalog

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?): View? {
        mBinding = FragmentDiscoverBinding.inflate(inflater, container, false)
        mBinding.let {
            it.setLifecycleOwner(this)
            it.viewModel = mViewModel
            return it.root
        }
    }

    override fun onCreate() {
        initObservers()
        initUi()
        mViewModel.request()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mViewModel.itunesStore.removeObservers(this@DiscoverFragment)
    }

    private fun initObservers() {
        mViewModel.itunesStore.observeK(this) {
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