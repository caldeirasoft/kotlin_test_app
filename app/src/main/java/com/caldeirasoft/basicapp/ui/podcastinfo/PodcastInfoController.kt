package com.caldeirasoft.basicapp.ui.podcastinfo

import com.airbnb.epoxy.EpoxyAsyncUtil
import com.airbnb.epoxy.EpoxyModel
import com.airbnb.epoxy.TypedEpoxyController
import com.airbnb.epoxy.paging.PagedListEpoxyController
import com.caldeirasoft.basicapp.EpisodePodcastItemBindingModel_
import com.caldeirasoft.basicapp.data.entity.Episode
import com.caldeirasoft.basicapp.data.entity.Podcast
import com.caldeirasoft.basicapp.podcastItem
import com.caldeirasoft.basicapp.ui.adapter.defaultItemDiffCallback
import com.caldeirasoft.basicapp.ui.epoxy.BasePagedController

class PodcastInfoController(private val callbacks: Callbacks) :
        BasePagedController<Episode>()
{
    interface Callbacks {
        fun onEpisodeClick(episode: Episode)
    }

    override fun toggleLoading(isLoading: Boolean) {
        this.isLoading = isLoading
    }

    override fun toggleRetry(retry: Boolean) {
        this.retry = retry
    }

    override fun buildItemModel(currentPosition: Int, item: Episode?): EpoxyModel<*> {
        item?.let {
            return EpisodePodcastItemBindingModel_()
                    .id(item.episodeId)
                    .title(item.title)
                    .duration(item.durationFormat())
                    .imageUrl(item.imageUrl)
                    .publishedDate(item.publishedFormat())
                    .onEpisodeClick { _ ->
                        callbacks.onEpisodeClick(item)
                        //TODO: transition with image translate
                        //callbacks.onItemClicked(item, view.findViewById(R.id.image))
                    }
        } ?: run {
            return EpisodePodcastItemBindingModel_()
                    .id(currentPosition)
        }
    }

    override fun addModels(models: List<EpoxyModel<*>>) {
        super.addModels(models)
        /*
        loadingState
            .addIf(
                isLoading, this
            )

        retryState
            .error("Opps something went wrong")
            .retryClickListener { callbacks.retryClicked() }
            .addIf(
                retry, this
            )
         */
    }
}