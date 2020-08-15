package com.caldeirasoft.basicapp.presentation.ui.podcastinfo

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.epoxy.*
import com.caldeirasoft.basicapp.*
import com.caldeirasoft.basicapp.presentation.ui.podcast.PodcastViewModel
import com.caldeirasoft.basicapp.presentation.utils.epoxy.AgendaHeaderDecoration
import com.caldeirasoft.basicapp.presentation.utils.extensions.clearDecorations
import com.caldeirasoft.castly.domain.model.entities.Episode
import com.caldeirasoft.castly.domain.model.entities.Podcast
import com.caldeirasoft.castly.domain.util.Status

class PodcastInfoController(
        private val viewModel: PodcastInfoViewModel,
        private val callbacks: Callbacks)
    : Typed2EpoxyController<List<Episode>, Status?>(), EpoxyController.Interceptor {
    private var _recyclerView: RecyclerView? = null

    interface Callbacks {
        fun onEpisodeOpen(episode: Episode, view: View)
        fun onEpisodePlay(episode: Episode, view: View)
        fun onEpisodeQueueNext(episode: Episode, view: View)
        fun onEpisodeQueueLast(episode: Episode, view: View)
        fun onPodcastSubscribe(podcast: Podcast, view: View)
    }

    init {
        addInterceptor(this)
    }

    override fun buildModels(data1: List<Episode>, data2: Status?) {
        // podcast info header
        podcastinfoHeader {
            id("podcastInfoHeader")
            viewModel(viewModel)
        }

        // podcast info episode header
        podcastinfoEpisodesHeader {
            id("podcastInfoEpisodeHeader")
            viewModel(viewModel)
        }

        // episodes
        data1.forEach { episode ->
            itemEpisodePodcast {
                id(episode.id)
                episode(episode)
                duration(episode.description)
                progression(0.5f)
                //playState(getPlayState(episode))
                onEpisodeClick { model, parentView, clickedView, position ->
                    callbacks.onEpisodeOpen(model.episode(), clickedView)
                }
                onPlayClick { model, parentView, clickedView, position ->
                    callbacks.onEpisodePlay(model.episode(), clickedView)
                }
            }
        }
    }

    override fun intercept(models: MutableList<EpoxyModel<*>>) {
        _recyclerView?.let { rv ->
            rv.clearDecorations()
            rv.addItemDecoration(
                    AgendaHeaderDecoration(
                            context = rv.context,
                            epoxyController = this,
                            models = models,
                            headerClass = ItemEpisodePodcastBindingModel_::class.java,
                            dateConverter = { model -> model.episode()?.releaseDate }))

        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this._recyclerView = recyclerView
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        this._recyclerView = null
    }
}