package com.caldeirasoft.basicapp.ui.podcast

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.databinding.library.baseAdapters.BR
import com.caldeirasoft.basicapp.R
import com.caldeirasoft.basicapp.addFragment
import com.caldeirasoft.basicapp.data.entity.Podcast
import com.caldeirasoft.basicapp.data.enum.EnumPodcastLayout
import com.caldeirasoft.basicapp.data.preferences.UserPref
import com.caldeirasoft.basicapp.databinding.ListitemPodcastBinding
import com.caldeirasoft.basicapp.extensions.withArgs
import com.caldeirasoft.basicapp.service.sync.SyncAdapterManager
import com.caldeirasoft.basicapp.ui.adapter.ItemViewClickListener
import com.caldeirasoft.basicapp.ui.adapter.SimpleDataBindingAdapter
import com.caldeirasoft.basicapp.ui.adapter.decorations.ItemDividerDecoration
import com.caldeirasoft.basicapp.ui.base.BaseActivity
import com.caldeirasoft.basicapp.ui.base.BaseFragment
import com.caldeirasoft.basicapp.ui.favorite.FavoriteFragment
import com.caldeirasoft.basicapp.ui.history.HistoryFragment
import com.caldeirasoft.basicapp.ui.home.IMainFragment
import com.caldeirasoft.basicapp.ui.podcastdetail.PodcastDetailActivity
import com.caldeirasoft.basicapp.ui.podcastdetail.PodcastDetailFragment
import com.caldeirasoft.basicapp.viewModelProviders

import kotlinx.android.synthetic.main.fragment_podcasts.*
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.android.UI
import org.jetbrains.anko.support.v4.intentFor

class PodcastFragment : BaseFragment(), IMainFragment, ItemViewClickListener<Podcast> {

    private val viewModel by lazy { viewModelProviders<PodcastViewModel>() }
    private val podcastAdapter by lazy {
        SimpleDataBindingAdapter<Podcast, ListitemPodcastBinding>(
                layoutId = R.layout.listitem_podcast,
                variableId = BR.podcast,
                itemViewClickListener = this,
                lifecycleOwner = this,
                clickAwareViewIds = *intArrayOf(R.id.linearLayout_podcast))
    }

    override fun getLayout() = R.layout.fragment_podcasts

    override fun getMenuItem() = R.id.navigation_podcasts

    override fun onCreate() {

        setupRecyclerView()
        setHasOptionsMenu(true)
        launch(UI) {
            observePodcast()
        }
    }

    /*
    fun onCreateOptionsLayout(item: MenuItem?)
    {
        activity?.apply {
            when (UserPref.podcastLayout) {
                EnumPodcastLayout.LIST.value -> // set grid icon
                    item?.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_grid_large_24dp))
                EnumPodcastLayout.GRID.value -> // set small grid icon
                    item?.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_grid_24dp))
                EnumPodcastLayout.GRID_SMALL.value -> //set list icon
                    item?.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_list_24dp))
            }
        }
    }
    */


    private fun setupRecyclerView() {
        with(podcasts_recyclerView) {
            layoutManager = LinearLayoutManager(activity)
            addItemDecoration(DividerItemDecoration(activity, LinearLayoutManager.VERTICAL))
            addItemDecoration(ItemDividerDecoration(context, 30, 2))
            adapter = podcastAdapter
        }
    }

    private fun observePodcast()
    {
        viewModel.apply {
            // collection
            podcasts.observe(this@PodcastFragment, Observer { podcasts ->
                podcastAdapter.submitList(podcasts.toList())
            })
        }
    }

    override fun onItemClick(item: Podcast?, position: Int, viewId: Int) {
        item?.let {
            openPodcastDetail(item)
        }
    }

    private fun openPodcastDetail(podcast: Podcast)
    {
        PodcastDetailFragment().let {
            it.withArgs(EXTRA_FEED_ID to podcast)
            this.activity?.addFragment(it, "podcastdetail" + podcast.feedUrl, true)
        }
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
    }
}