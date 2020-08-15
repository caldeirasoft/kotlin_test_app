package com.caldeirasoft.basicapp.presentation.ui.catalog

import android.view.View
import com.airbnb.epoxy.EpoxyModel
import com.caldeirasoft.basicapp.LoadingBindingModel_
import com.caldeirasoft.basicapp.PodcastCatalogBindingModel_
import com.caldeirasoft.basicapp.presentation.utils.epoxy.BasePagedController
import com.caldeirasoft.castly.domain.model.entities.Podcast

class CatalogEpoxyController(private val callbacks: Callbacks)
    : BasePagedController<Podcast>() {
    interface Callbacks {
        fun onPodcastClicked(podcast: Podcast, view: View)
    }

    override fun buildItemModel(currentPosition: Int, item: Podcast?): EpoxyModel<*> {
        item?.let { podcast ->
            return PodcastCatalogBindingModel_().apply {
                id(podcast.id)
                title(podcast.name)
                authors(podcast.artistName)
                imageUrl(podcast.getArtwork(100))
                clickListener { view -> callbacks.onPodcastClicked(podcast, view) }

            }
        } ?: run {
            return LoadingBindingModel_().apply {
                id("loading")
            }
        }
    }

    override fun addModels(models: List<EpoxyModel<*>>) {
        if (isError) {
            super.addModels(
                    models/*.plus(
                            //Error View Model
                            ErrorEpoxyModel_()
                                    .id("Error")
                                    .errorStr(error)
                             ).filter { !(it is LoadingBindingModel_) }
                    )*/
            )
        }
        else if (isLoading) {
            super.addModels(
                    models.plus(
                            //Error View Model
                            LoadingBindingModel_()
                                    .id("loading")
                    ).distinct()
            )
        } else {
            super.addModels(models.distinct())
        }
    }
}