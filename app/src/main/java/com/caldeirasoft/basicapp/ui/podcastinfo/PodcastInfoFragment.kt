package com.caldeirasoft.basicapp.ui.podcastinfo

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.caldeirasoft.basicapp.R
import com.caldeirasoft.basicapp.data.entity.Episode
import com.caldeirasoft.basicapp.data.entity.Podcast
import com.caldeirasoft.basicapp.data.enum.SubscribeAction
import com.caldeirasoft.basicapp.databinding.FragmentPodcastinfoBinding
import com.caldeirasoft.basicapp.extensions.lazyArg
import com.caldeirasoft.basicapp.ui.common.BindingFragment
import com.caldeirasoft.basicapp.ui.common.MainNavigationFragment
import com.caldeirasoft.basicapp.ui.extensions.observeK
import com.caldeirasoft.basicapp.ui.extensions.viewModelProviders
import com.caldeirasoft.basicapp.widget.BottomSheetBehavior
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.fragment_podcastinfo.*

class PodcastInfoFragment :
        BindingFragment<FragmentPodcastinfoBinding>(),
        PodcastInfoController.Callbacks,
        MainNavigationFragment
{
    val podcast by lazyArg<Podcast>(EXTRA_FEED_ID)
    val collapsed by lazyArg<Boolean?>(COLLAPSED)

    private var menu: Menu? = null
    private var showHeader:Boolean = true
    private lateinit var filtersFab: FloatingActionButton

    private val mViewModel by lazy { viewModelProviders<PodcastInfoViewModel>() }
    private val controller by lazy { PodcastInfoController(this, mViewModel) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?): View? {
        mBinding = FragmentPodcastinfoBinding.inflate(inflater, container, false)
        mBinding.let {
            it.setLifecycleOwner(this)
            it.viewModel = mViewModel
            return it.root
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initMotionLayout()
        initObservers()
        initUi()
    }

    override fun onResume() {
        super.onResume()
        if (!showHeader)
            motionLayout_root.progress = 1f
    }

    private fun initObservers() {
        // set loading
        mViewModel.setDataSource(podcast)
        mViewModel.episodes.observeK(this) {
            controller.submitList(it)
        }
        mViewModel.updateEpisodeEvent.observeK(this) {
            //controller.
        }
    }

    private fun initMotionLayout() {
        motionLayout_root.setTransitionListener(object : MotionLayout.TransitionListener {
            override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) { }
            override fun onTransitionChange(p0: MotionLayout?, p1: Int, p2: Int, p3: Float) { }
            override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) { }
            override fun onTransitionCompleted(p0: MotionLayout?, p1: Int) {
                showHeader = p1 != R.id.podcastinfo_end
            }
        })
    }

    private fun initUi() {
        recyclerView.setController(controller)
        recyclerView.addItemDecoration(DividerItemDecoration(activity, LinearLayoutManager.VERTICAL))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        /*
        mViewModel.updateEpisodeEvent.removeObservers(this)
        mViewModel.episodes.removeObservers(this)
        mViewModel.subscribePodcastEvent.removeObservers(this)
        mViewModel.loadingState.removeObservers(this)
        */
    }

    /*
    override fun onItemClick(item: Episode?, position: Int, viewId: Int) {
        item?.let {
            when (viewId) {
                R.id.button_favorite -> mViewModel.toggleEpisodeFavorite(it)
                R.id.button_archive -> mViewModel.archiveEpisode(it)
                R.id.button_queuelast -> mViewModel.queueEpisodeLast(it)
                R.id.button_queuenext -> mViewModel.queueEpisodeFirst(it)
                R.id.button_play -> mViewModel.playEpisode(it)
                else -> openEpisodeDetail(it)
            }
        }
    }
    */

    override fun onEpisodeClick(episode: Episode) {
        val bundle = Bundle()
        bundle.putParcelable(EXTRA_FEED_ID, episode)
        findNavController(view!!).navigate(R.id.action_podcastInfoFragment_to_episodeInfoFragment, bundle)
    }

    fun subscribeToPodcast()
    {
        podcast.let {
            when (mViewModel.isInDatabase.value) {
                false -> showSubscribeDialog(it)
                true -> mViewModel.unsubscribeFromPodcast(it)
            }
        }
    }

    fun showSubscribeDialog(podcast: Podcast) {
        val options = requireContext().resources?.getStringArray(R.array.subscribe_options)
        val builder = AlertDialog.Builder(requireContext())
                //alt_bld.setIcon(R.drawable.icon);
                .setTitle("Select a Group Name")
                .setSingleChoiceItems(
                        R.array.subscribe_options,
                        -1
                ) { dialog, item ->
                    Toast.makeText(requireContext(),
                            "Group Name = " + options!![item], Toast.LENGTH_SHORT).show()
                    when (item) {
                        0 -> mViewModel.subscribeToPodcast(podcast, SubscribeAction.INBOX)
                        1 -> mViewModel.subscribeToPodcast(podcast, SubscribeAction.QUEUE_NEXT)
                        2 -> mViewModel.subscribeToPodcast(podcast, SubscribeAction.QUEUE_LAST)
                        3 -> mViewModel.subscribeToPodcast(podcast, SubscribeAction.ARCHIVE)
                    }
                    dialog.dismiss()// dismiss the alertbox after chose option
                }
        val alert = builder.create()
        alert.show()
    }

    companion object {
        const val EXTRA_FEED_ID = "FEED_ID"
        const val COLLAPSED = "COLLAPSED"
    }
}