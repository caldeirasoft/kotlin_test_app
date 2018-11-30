package com.caldeirasoft.basicapp.ui.episodes

import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.databinding.library.baseAdapters.BR
import com.caldeirasoft.basicapp.R
import com.caldeirasoft.basicapp.data.entity.Episode
import com.caldeirasoft.basicapp.data.entity.PodcastWithCount
import com.caldeirasoft.basicapp.databinding.ListitemEpisodesinboxBinding
import com.caldeirasoft.basicapp.ui.adapter.ItemViewClickListener
import com.caldeirasoft.basicapp.ui.adapter.decorations.StickyHeaderDecoration
import com.caldeirasoft.basicapp.ui.common.BaseFragment

abstract class EpisodesBaseFragment : BaseFragment(), ItemViewClickListener<Episode> {

    protected abstract val viewModel: EpisodesViewModel
    protected val episodesAdapter by lazy {
        EpisodesDataBindingAdapter<ListitemEpisodesinboxBinding>(
                layoutId = R.layout.listitem_episodesinbox,
                variableId = BR.episode,
                itemViewClickListener = this,
                lifecycleOwner = this,
                clickAwareViewIds = *intArrayOf(R.id.itemEpisodeConstraintLayout))
    }

    override fun onCreate() {
        observeEpisodes()
    }

    protected fun observeEpisodes() {
        viewModel.apply {
            // collection
            episodes.observe(this@EpisodesBaseFragment, Observer { episodes ->
                episodes?.let {
                    episodesAdapter.submitList(episodes)
                }
            })
            // filters
            podcastsWithCount.observe(this@EpisodesBaseFragment, Observer { podcasts ->
                podcasts?.let {
                    // add starred + history
                    val customList = mutableListOf<PodcastWithCount>(PodcastWithCount()).plus(podcasts).orEmpty()
                    updateViewPager(customList)
                }
            })
        }
    }

    protected fun setupRecyclerView(recyclerView: RecyclerView) {
        with(recyclerView) {
            layoutManager = LinearLayoutManager(activity)
            addItemDecoration(DividerItemDecoration(activity, LinearLayoutManager.VERTICAL))
            addItemDecoration(StickyHeaderDecoration(episodesAdapter))
            adapter = episodesAdapter
        }
    }
    protected fun updateViewPager(podcasts: List<PodcastWithCount>)
    {
        val feedUrls:List<String> = podcasts.map { pc -> pc.feedUrl }.sorted().toList()
        //inboxAdapter.updatePodcasts(feedIds)
        //updateTabLayoutTitles(podcasts)
    }

    override fun onItemClick(item: Episode?, position: Int, viewId: Int) {
        item?.let {
            openEpisodeDetail(item)
        }
    }


    private fun openEpisodeDetail(episode: Episode) {
        //startActivity(intentFor<PodcastDetailActivity>().putExtra(EXTRA_FEED_ID, podcast.feedId))
    }

    companion object {
        const val EXTRA_FEED_ID = "FEED_ID"
    }
}
