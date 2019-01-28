package com.caldeirasoft.basicapp.ui.episodelist

import android.os.Bundle
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.caldeirasoft.basicapp.R
import com.caldeirasoft.basicapp.data.entity.Episode
import com.caldeirasoft.basicapp.databinding.FragmentEpisodelistBinding
import com.caldeirasoft.basicapp.ui.common.BindingFragment
import com.caldeirasoft.basicapp.ui.extensions.observeK
import kotlinx.android.synthetic.main.fragment_episodelist.*


abstract class EpisodeListFragment : BindingFragment<FragmentEpisodelistBinding>(), EpisodeListController.Callbacks {

    protected var showHeader:Boolean = true
    protected abstract val mViewModel:EpisodeListViewModel
    protected val controller = EpisodeListController(this)

    override fun onCreate() {
        initMotionLayout()
        initObservers()
        initUi()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mViewModel.episodes.removeObservers(this)
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
        mViewModel.episodes.observeK(this) {
            controller.setData(it)
        }
    }

    private fun initUi() {
        recyclerView.setController(controller)
        recyclerView.addItemDecoration(DividerItemDecoration(activity, LinearLayoutManager.VERTICAL))
    }

    override fun onEpisodeClick(episode: Episode) {
        view?.let {
            val bundle = Bundle()
            bundle.putParcelable(EXTRA_FEED_ID, episode)
            findNavController(it).navigate(R.id.action_podcastFragment_to_podcastInfoFragment, bundle)
        }
    }

    companion object {
        const val EXTRA_FEED_ID = "FEED_ID"
    }
}