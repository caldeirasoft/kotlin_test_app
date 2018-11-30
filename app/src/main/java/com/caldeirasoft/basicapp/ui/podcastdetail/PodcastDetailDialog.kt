package com.caldeirasoft.basicapp.ui.podcastdetail

import androidx.lifecycle.Observer
import androidx.databinding.library.baseAdapters.BR
import com.caldeirasoft.basicapp.R
import com.caldeirasoft.basicapp.data.entity.Podcast
import com.caldeirasoft.basicapp.ui.common.FullScreenBottomSheetDialog
import com.caldeirasoft.basicapp.ui.catalog.CatalogViewModel


class PodcastDetailDialog() : FullScreenBottomSheetDialog()
{
    lateinit var podcast:Podcast
    lateinit var catalogViewModel: CatalogViewModel

    override fun getLayout(): Int = R.layout.fragment_podcastdialog

    override fun onCreate() {
        viewDataBinding?.apply {
            setVariable(BR.podcast, podcast)
            setVariable(BR.handler, catalogViewModel)
            this.setLifecycleOwner(this@PodcastDetailDialog)
        }

        catalogViewModel.apply {
            // get last episode
            openLastEpisodeEvent.observe(this@PodcastDetailDialog, Observer { episode ->
                episode?.let {
                    viewDataBinding?.setVariable(BR.episode, episode)
                }
            })
            // update podcasts
            updatePodcastSubscriptionEvent.observe(this@PodcastDetailDialog, Observer { podcast ->
                podcast?.let {
                    if (it.feedUrl == this@PodcastDetailDialog.podcast.feedUrl)
                        viewDataBinding?.setVariable(BR.podcast, it)
                }
            })
            // update description
            updatePodcastDescriptionEvent.observe(this@PodcastDetailDialog, Observer { podcast ->
                podcast?.let {
                    if (it.feedUrl == this@PodcastDetailDialog.podcast.feedUrl)
                        viewDataBinding?.setVariable(BR.podcast, it)
                }
            })

            // get last episode
            getLastEpisode(podcast)
            // get description
            getDescription(podcast)
        }
    }

}