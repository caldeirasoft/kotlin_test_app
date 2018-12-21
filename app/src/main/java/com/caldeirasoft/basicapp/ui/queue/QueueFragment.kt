package com.caldeirasoft.basicapp.ui.queue

import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.databinding.library.baseAdapters.BR
import com.caldeirasoft.basicapp.R
import com.caldeirasoft.basicapp.data.entity.Episode
import com.caldeirasoft.basicapp.data.enum.EnumPodcastLayout
import com.caldeirasoft.basicapp.data.preferences.UserPref
import com.caldeirasoft.basicapp.databinding.ListitemEpisodesqueueBinding
import com.caldeirasoft.basicapp.service.sync.SyncAdapterManager
import com.caldeirasoft.basicapp.ui.adapter.ItemViewClickListener
import com.caldeirasoft.basicapp.ui.adapter.SimpleDataBindingAdapter
import com.caldeirasoft.basicapp.ui.common.BaseFragment
import com.caldeirasoft.basicapp.ui.home.IMainFragment
import com.caldeirasoft.basicapp.viewModelProviders

import kotlinx.android.synthetic.main.fragment_queue.*

class QueueFragment : BaseFragment(), IMainFragment, ItemViewClickListener<Episode> {

    private val viewModel by lazy { viewModelProviders<QueueViewModel>() }
    private val queueAdapter by lazy {
        SimpleDataBindingAdapter<Episode, ListitemEpisodesqueueBinding>(
                layoutId = R.layout.listitem_episodesqueue,
                variableId = BR.episode,
                itemViewClickListener = this,
                lifecycleOwner = this,
                clickAwareViewIds = *intArrayOf(R.id.itemEpisodeConstraintLayout))
    }
    private var menu: Menu? = null

    override fun getLayout() = R.layout.fragment_queue

    override fun getMenuItem() = R.id.navigation_inbox

    override fun onCreate() {
        // toolbar
        setToolbar();

        setupRecyclerView()
        setHasOptionsMenu(true)
        observeQueue()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.episodes.removeObservers(this@QueueFragment)
    }

    private fun setToolbar() {
        (activity as AppCompatActivity).setSupportActionBar(toolbar);
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        //this.menu = menu
        //inflater?.inflate(R.menu.main_menu, menu)
        // change "podcast layout" icon
        //super.onDestroyOptionsMenu()
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
        val manager: RecyclerView.LayoutManager =
                when (UserPref.podcastLayout) {
                    EnumPodcastLayout.GRID.value -> // grid
                        GridLayoutManager(activity, 3)
                    EnumPodcastLayout.GRID_SMALL.value -> //small grid
                        GridLayoutManager(activity, 4)
                    else -> // list
                        LinearLayoutManager(activity)
                }

        with(queue_recyclerView) {
            layoutManager = manager
            addItemDecoration(DividerItemDecoration(activity, LinearLayoutManager.VERTICAL))
            adapter = queueAdapter
        }
    }

    private fun observeQueue()
    {
        viewModel.apply {
            // collection
            episodes.observe(this@QueueFragment, Observer { episodes ->
                queueAdapter.submitList(episodes)
            })
        }
    }

    override fun onItemClick(item: Episode?, position: Int, viewId: Int) {
        item?.let {
            openEpisodeDetail(item)
        }
    }

    private fun openEpisodeDetail(episode: Episode)
    {
        //startActivity(intentFor<PodcastDetailActivity>().putExtra(EXTRA_FEED_ID, podcast.feedId))
    }

    companion object {
        const val EXTRA_FEED_ID = "FEED_ID"
    }
}