package com.caldeirasoft.basicapp.ui.podcastinfo

import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.caldeirasoft.basicapp.data.entity.Episode
import com.caldeirasoft.basicapp.data.entity.Podcast
import com.caldeirasoft.basicapp.data.enum.SectionState
import com.caldeirasoft.basicapp.databinding.FragmentPodcastinfoBinding
import com.caldeirasoft.basicapp.extensions.lazyArg
import com.caldeirasoft.basicapp.ui.common.BindingFragment
import com.caldeirasoft.basicapp.ui.common.MainNavigationFragment
import com.caldeirasoft.basicapp.ui.episodedetail.EpisodeDetailDialog
import com.caldeirasoft.basicapp.ui.extensions.observeK
import com.caldeirasoft.basicapp.ui.extensions.viewModelProviders
import com.caldeirasoft.basicapp.ui.podcastdetail.filter.PodcastFilterFragment
import com.caldeirasoft.basicapp.widget.BottomSheetBehavior
import com.caldeirasoft.basicapp.widget.BottomSheetBehavior.Companion.STATE_COLLAPSED
import com.caldeirasoft.basicapp.widget.BottomSheetBehavior.Companion.STATE_EXPANDED
import com.caldeirasoft.basicapp.widget.BottomSheetBehavior.Companion.STATE_HIDDEN
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.fragment_podcastinfo.*

class PodcastInfoFragment : BindingFragment<FragmentPodcastinfoBinding>(), PodcastInfoController.Callbacks, MainNavigationFragment {

    val podcast by lazyArg<Podcast>(EXTRA_FEED_ID)
    val collapsed by lazyArg<Boolean?>(COLLAPSED)

    private var menu: Menu? = null
    private lateinit var mBottomSheetBehavior: BottomSheetBehavior<*>
    private lateinit var filtersFab: FloatingActionButton

    private val podcastViewModel by lazy { viewModelProviders<PodcastInfoViewModel>() }
    private val controller = PodcastInfoController(this)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?): View? {
        mBinding = FragmentPodcastinfoBinding.inflate(inflater, container, false)
                .apply {
                    setLifecycleOwner(this@PodcastInfoFragment)
                    viewModel = podcastViewModel
                }
        return mBinding.root
    }

    override fun onCreate() {
        initObservers()
        initUi()
    }

    private fun initObservers() {
        podcastViewModel.setDataSource(podcast)
        podcastViewModel.episodes.observeK(this) {
            controller.submitList(it)
        }
    }

    private fun initUi() {
        recyclerView.setController(controller)
        recyclerView.addItemDecoration(DividerItemDecoration(activity, LinearLayoutManager.VERTICAL))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        podcastViewModel.updateEpisodeEvent.removeObservers(this@PodcastInfoFragment)
        podcastViewModel.episodes.removeObservers(this@PodcastInfoFragment)
        podcastViewModel.section.removeObservers(this@PodcastInfoFragment)
        podcastViewModel.subscribePodcastEvent.removeObservers(this@PodcastInfoFragment)
        podcastViewModel.loadingState.removeObservers(this@PodcastInfoFragment)
    }

    /*
    private fun observePodcast() {
        podcast.let {
            viewModel.apply {

                // data binding
                this.setDataSource(it)

                // update episode event
                updateEpisodeEvent.observe(this@PodcastInfoFragment, Observer { episode ->
                    episode?.let { ep ->
                        episodesAdapter.apply {
                            updateItem(ep)
                        }
                    }
                })
                // collection without section
                episodes.observe(this@PodcastInfoFragment, Observer { episodes ->
                    episodes?.let { list ->
                        episodesAdapter.submitList(list)
                    }
                })

                // update section
                section.observe(this@PodcastInfoFragment, Observer { section ->
                    updateFilterUI(section)
                })

                // subscribe event
                subscribePodcastEvent.observe(this@PodcastInfoFragment, Observer { podcast ->
                    podcast?.let {
                        when (isInDatabase.value) {
                            false -> showSubscribeDialog(it)
                            true -> unsubscribeFromPodcast(it)
                        }
                    }
                })

                // network updates
                loadingState.observe(this@PodcastInfoFragment, Observer { state ->
                    state?.let { st ->
                        //episodesAdapter.setState(st)
                    }
                })
            }
        }
    }

    override fun onItemClick(item: Episode?, position: Int, viewId: Int) {
        item?.let {
            when (viewId) {
                R.id.button_favorite -> viewModel.toggleEpisodeFavorite(it)
                R.id.button_archive -> viewModel.archiveEpisode(it)
                R.id.button_queuelast -> viewModel.queueEpisodeLast(it)
                R.id.button_queuenext -> viewModel.queueEpisodeFirst(it)
                R.id.button_play -> viewModel.playEpisode(it)
                else -> openEpisodeDetail(it)
            }
        }
    }
    */

    override fun onEpisodeClick(episode: Episode) {
        EpisodeDetailDialog().let {
            it.episode = episode
            it.podcast = podcast
            it.show(this.childFragmentManager, "episode")
        }
    }

    private fun updateFilterUI(section: Int?) {
        val showFab = section == null
        val hideable = section == null

        //fabVisibility(filtersFab, showFab)
        if (::mBottomSheetBehavior.isInitialized) {
            mBottomSheetBehavior.isHideable = hideable
            mBottomSheetBehavior.skipCollapsed = hideable
            if (hideable && mBottomSheetBehavior.state == STATE_COLLAPSED) {
                mBottomSheetBehavior.state = STATE_HIDDEN
            }
        }
    }

    private fun updateBottomSheetFragmentSelectedIndex(section: Int?) {
        val fm = childFragmentManager
        var bottomSheetFragment = fm.findFragmentByTag("bottomSheetFragment") as PodcastFilterFragment?
        bottomSheetFragment?.let {
            when (section) {
                null -> it.setSelectedIndex(0)
                SectionState.QUEUE.value -> it.setSelectedIndex(1)
                SectionState.INBOX.value -> it.setSelectedIndex(2)
                SectionState.FAVORITE.value -> it.setSelectedIndex(3)
                SectionState.HISTORY.value -> it.setSelectedIndex(4)
                else -> {}
            }
        }
    }

    private fun setupFabClick() {
        filtersFab.setOnClickListener {
            mBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }

    override fun onBackPressed(): Boolean {
        if (::mBottomSheetBehavior.isInitialized && mBottomSheetBehavior.state == STATE_EXPANDED) {
            // collapse or hide the sheet
            if (mBottomSheetBehavior.isHideable && mBottomSheetBehavior.skipCollapsed) {
                mBottomSheetBehavior.state = STATE_HIDDEN
            } else {
                mBottomSheetBehavior.state = STATE_COLLAPSED
            }
            return true
        }
        return super.onBackPressed()
    }

    /*
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
                        0 -> viewModel.subscribeToPodcast(podcast, SubscribeAction.INBOX)
                        1 -> viewModel.subscribeToPodcast(podcast, SubscribeAction.QUEUE_NEXT)
                        2 -> viewModel.subscribeToPodcast(podcast, SubscribeAction.QUEUE_LAST)
                        3 -> viewModel.subscribeToPodcast(podcast, SubscribeAction.ARCHIVE)
                    }
                    dialog.dismiss()// dismiss the alertbox after chose option
                }
        val alert = builder.create()
        alert.show()
    }
    */

    companion object {
        const val EXTRA_FEED_ID = "FEED_ID"
        const val COLLAPSED = "COLLAPSED"
    }
}