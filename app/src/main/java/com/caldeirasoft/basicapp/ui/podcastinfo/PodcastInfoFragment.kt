package com.caldeirasoft.basicapp.ui.podcastinfo

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.library.baseAdapters.BR
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.caldeirasoft.basicapp.R
import com.caldeirasoft.basicapp.data.entity.Episode
import com.caldeirasoft.basicapp.data.entity.Podcast
import com.caldeirasoft.basicapp.data.enum.SectionState
import com.caldeirasoft.basicapp.databinding.FragmentPodcastinfoBinding
import com.caldeirasoft.basicapp.databinding.ListitemEpisodesinfoBinding
import com.caldeirasoft.basicapp.extensions.lazyArg
import com.caldeirasoft.basicapp.service.sync.SyncAdapterManager
import com.caldeirasoft.basicapp.ui.adapter.ItemViewClickListener
import com.caldeirasoft.basicapp.ui.adapter.SimplePagedDataBindingAdapter
import com.caldeirasoft.basicapp.ui.adapter.decorations.HeaderViewDecoration
import com.caldeirasoft.basicapp.ui.common.BindingFragment
import com.caldeirasoft.basicapp.ui.common.MainNavigationFragment
import com.caldeirasoft.basicapp.ui.episodedetail.EpisodeDetailDialog
import com.caldeirasoft.basicapp.ui.podcastdetail.filter.PodcastFilterFragment
import com.caldeirasoft.basicapp.viewModelProviders
import com.caldeirasoft.basicapp.widget.BottomSheetBehavior
import com.caldeirasoft.basicapp.widget.BottomSheetBehavior.Companion.STATE_COLLAPSED
import com.caldeirasoft.basicapp.widget.BottomSheetBehavior.Companion.STATE_EXPANDED
import com.caldeirasoft.basicapp.widget.BottomSheetBehavior.Companion.STATE_HIDDEN
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.fragment_podcastinfo.*
import kotlinx.android.synthetic.main.fragment_podcastinfo.view.*

class PodcastInfoFragment : BindingFragment<FragmentPodcastinfoBinding>(), ItemViewClickListener<Episode>, MainNavigationFragment {

    val podcast by lazyArg<Podcast>(EXTRA_FEED_ID)
    val collapsed by lazyArg<Boolean>(COLLAPSED)

    private var menu: Menu? = null
    private lateinit var mBottomSheetBehavior: BottomSheetBehavior<*>
    private lateinit var filtersFab: FloatingActionButton

    private val viewModel by lazy { viewModelProviders<PodcastInfoViewModel>() }
    private val episodesAdapter by lazy {
        SimplePagedDataBindingAdapter<Episode, ListitemEpisodesinfoBinding>(
                layoutId = R.layout.listitem_episodesinfo,
                variableId = BR.episode,
                itemViewClickListener = this,
                lifecycleOwner = this,
                clickAwareViewIds = *intArrayOf(R.id.buttonInfo, R.id.button_play, R.id.button_archive, R.id.button_queuenext, R.id.button_queuelast, R.id.button_favorite)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?): View? {
        mBinding = FragmentPodcastinfoBinding.inflate(inflater, container, false).apply {
            setLifecycleOwner(this@PodcastInfoFragment)
            viewModel = this@PodcastInfoFragment.viewModel
        }
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // toolbar
        setToolbar()
        setupRecyclerView()
        setupSwipeRefreshLayout()
        //setHasOptionsMenu(true)
        //-setupBottomSheet()
        observePodcast()
    }

    private fun setToolbar() {
        if (collapsed) {
            appbar.setExpanded(false, false)
        }

        (activity as AppCompatActivity).apply {
            setSupportActionBar(toolbar)
            supportActionBar?.apply {
                setDisplayHomeAsUpEnabled(true)
                setDisplayShowHomeEnabled(true)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        this.menu = menu
        inflater?.inflate(R.menu.main_menu, menu)
        // change "podcast layout" icon
        super.onDestroyOptionsMenu()
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item?.itemId) {
            R.id.menu_refresh -> {
                val syncAdapterManager = SyncAdapterManager(activity!!)
                syncAdapterManager.syncImmediately()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupRecyclerView() {
        // sticky header
        with(recyclerView_podcastinfo) {
            layoutManager = LinearLayoutManager(this.context, RecyclerView.VERTICAL, false)
            addItemDecoration(DividerItemDecoration(activity, LinearLayoutManager.VERTICAL))
            addItemDecoration(HeaderViewDecoration(R.layout.header_podcastinfo))
            //addItemDecoration(ItemOffsetDecoration(5, 5))
            //addItemDecoration(StickyHeaderLeftDecoration(episodesAdapter))
            adapter = episodesAdapter
        }

        episodesAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                if (positionStart == 0)
                    recyclerView_podcastinfo.layoutManager?.scrollToPosition(0)
            }
        })
    }

    private fun setupSwipeRefreshLayout() =
            with (swipeRefreshLayout_podcastinfo) {
                setOnRefreshListener {
                    viewModel.refresh()
                    this.isRefreshing = false
                }
            }

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

    private fun openEpisodeDetail(episode: Episode)  {
        EpisodeDetailDialog().let {
            it.episode = episode
            it.podcast = viewModel.podcast.value!!
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

    companion object {
        const val EXTRA_FEED_ID = "FEED_ID"
        const val COLLAPSED = "COLLAPSED"
    }
}