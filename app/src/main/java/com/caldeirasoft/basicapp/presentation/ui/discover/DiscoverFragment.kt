package com.caldeirasoft.basicapp.presentation.ui.discover

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.navigation.Navigation
import android.view.ViewGroup
import com.caldeirasoft.basicapp.R
import com.caldeirasoft.basicapp.domain.entity.Podcast
import com.caldeirasoft.basicapp.databinding.FragmentDiscoverBinding
import com.caldeirasoft.basicapp.presentation.ui.base.BindingFragment
import com.caldeirasoft.basicapp.presentation.extensions.observeK
import kotlinx.android.synthetic.main.fragment_discover.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class DiscoverFragment :
        BindingFragment<FragmentDiscoverBinding>(),
        DiscoverController.Callbacks {

    private var showHeader: Boolean = true
    private val mViewModel: DiscoverViewModel by viewModel()
    private val controller = DiscoverController(this)

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
            motionLayout_root.progress = 1f
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