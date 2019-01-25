package com.caldeirasoft.basicapp.ui.podcast

import android.os.Bundle
import android.view.View
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.caldeirasoft.basicapp.Mockup
import com.caldeirasoft.basicapp.R
import com.caldeirasoft.basicapp.data.entity.Podcast
import com.caldeirasoft.basicapp.extensions.putExtras
import com.caldeirasoft.basicapp.extensions.withArgs
import com.caldeirasoft.basicapp.ui.common.BaseFragment
import com.caldeirasoft.basicapp.ui.extensions.addFragment
import com.caldeirasoft.basicapp.ui.extensions.observeK
import com.caldeirasoft.basicapp.ui.extensions.viewModelProviders
import com.caldeirasoft.basicapp.ui.home.IMainFragment
import com.caldeirasoft.basicapp.ui.podcastinfo.PodcastInfoFragment
import kotlinx.android.synthetic.main.fragment_podcasts.*

class PodcastFragment : BaseFragment(), PodcastController.Callbacks {

    private var showHeader:Boolean = true
    private val viewModel by lazy { viewModelProviders<PodcastViewModel>() }
    private val controller = PodcastController(this)

    override fun getLayout() = R.layout.fragment_podcasts

    override fun onCreate() {
        initMotionLayout()
        initObservers()
        initUi()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.podcasts.removeObservers(this)
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
        viewModel.podcasts.observeK(this) {
            controller.setData(it)
        }
    }

    private fun initUi() {
        recyclerView.setController(controller)
        recyclerView.addItemDecoration(DividerItemDecoration(activity, LinearLayoutManager.VERTICAL))
    }

    override fun onPodcastClick(view: View, podcast: Podcast) {
        val bundle = Bundle()
        bundle.putParcelable(EXTRA_FEED_ID, podcast)
        findNavController(view).navigate(R.id.action_podcastFragment_to_podcastInfoFragment, bundle)
    }

    /*
    private fun changeLayout()
    {
        menu?.let {
            when (UserPref.podcastLayout) {
                EnumPodcastLayout.LIST.value -> // set grid icon
                    UserPref.podcastLayout = EnumPodcastLayout.GRID.value
                EnumPodcastLayout.GRID.value -> // set small grid icon
                    UserPref.podcastLayout = EnumPodcastLayout.GRID_SMALL.value
                EnumPodcastLayout.GRID_SMALL.value -> //set list icon
                    UserPref.podcastLayout = EnumPodcastLayout.LIST.value
            }

            setupRecyclerView()
            activity?.invalidateOptionsMenu()
        }
    }
    */

    companion object {
        const val EXTRA_FEED_ID = "FEED_ID"
        const val COLLAPSED = "COLLAPSED"
    }
}