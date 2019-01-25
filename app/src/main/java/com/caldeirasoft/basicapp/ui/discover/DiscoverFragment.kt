package com.caldeirasoft.basicapp.ui.discover

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
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

    private var showHeader: Boolean = true
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
        initMotionLayout()
        initObservers()
        initUi()
        mViewModel.request()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mViewModel.itunesStore.removeObservers(this@DiscoverFragment)
    }

    override fun onResume() {
        super.onResume()
        if (!showHeader)
            motionLayout_root.transitionToEnd()
    }

    private fun initMotionLayout() {
        motionLayout_root.setTransitionListener(object: MotionLayout.TransitionListener {
            override fun onTransitionChange(p0: MotionLayout?, p1: Int, p2: Int, p3: Float) { }
            override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) { }
            override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) { }
            override fun onTransitionCompleted(p0: MotionLayout?, p1: Int) {
                showHeader = (p1 != R.id.end)
            }
        })
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
        view?.let {
            val bundle = Bundle()
            bundle.putParcelable(EXTRA_FEED_ID, podcast)
            Navigation.findNavController(it).navigate(R.id.action_discoverFragment_to_podcastInfoFragment, bundle)
        }
    }

    companion object {
        const val EXTRA_FEED_ID = "FEED_ID"
    }
}