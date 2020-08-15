package com.caldeirasoft.basicapp.presentation.utils.epoxy

import android.content.Context
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.epoxy.EpoxyController
import com.airbnb.epoxy.EpoxyModel
import com.airbnb.epoxy.Typed3EpoxyController
import com.caldeirasoft.basicapp.EpisodelistSectionDateBindingModel_
import com.caldeirasoft.basicapp.ItemEpisodeBindingModel_
import com.caldeirasoft.basicapp.itemEpisode
import com.caldeirasoft.basicapp.presentation.utils.extensions.clearDecorations
import com.caldeirasoft.basicapp.util.RelativeTimestampGenerator
import com.caldeirasoft.castly.domain.model.entities.Episode
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneOffset

class EpisodesGroupByDateController(
        private val callbacks: Callbacks,
        private val context: Context)
    : Typed3EpoxyController<List<Episode>, Long?, Int?>(), EpoxyController.Interceptor {
    private var _recyclerView: RecyclerView? = null

    interface Callbacks {
        fun onEpisodeOpen(episode: Episode, view: View)
        fun onEpisodePlay(episode: Episode, view: View)
        fun onEpisodeQueueNext(episode: Episode, view: View)
        fun onEpisodeQueueLast(episode: Episode, view: View)
        fun onEpisodeArchive(episode: Episode, view: View)
    }


    /*
    var ids: HashSet<String> = hashSetOf()
    // observe currentMediaId with old and new value
    var currentMediaId: String? by Delegates.observable<String?>(null) {
        //Delegates.observable on https://proandroiddev.com/the-magic-in-kotlin-delegates-377d27a7b531
        property, oldValue, newValue ->
            if (!oldValue.isNullOrEmpty() && (ids.contains(oldValue) || ids.contains(newValue)))
                this.requestForcedModelBuild()
    }
     */

    init {
        // add interceptor
        addInterceptor(this)
    }

    override fun buildModels(data1: List<Episode>?, playingEpisodeId: Long?, playingState: Int?) {
        data1 ?: return

        // episodes
        data1?.forEach { episode ->
            itemEpisode {
                id(episode.id)
                episode(episode)
                duration(episode.description)
                //playState(getPlayState(episode))
                onEpisodeClick { model, parentView, clickedView, position ->
                    callbacks.onEpisodeOpen(model.episode(), clickedView)
                }
                onPlayClick { model, parentView, clickedView, position ->
                    callbacks.onEpisodePlay(model.episode(), clickedView)
                }
                onQueueNextClick { model, parentView, clickedView, position ->
                    callbacks.onEpisodeQueueNext(model.episode(), clickedView)
                }
                onQueueEndClick { model, parentView, clickedView, position ->
                    callbacks.onEpisodeQueueLast(model.episode(), clickedView)
                }
                onArchiveClick { model, parentView, clickedView, position ->
                    callbacks.onEpisodeArchive(model.episode(), clickedView)
                }
            }
        }
    }

    override fun intercept(models: MutableList<EpoxyModel<*>>) {
        Log.d("intercept", "")
        val sections : MutableList<String> = mutableListOf()
        val generator = RelativeTimestampGenerator()

        // for each model, get section value (date)
        val mapSections : List<LocalDateTime?> = models.map { model ->
            (model as ItemEpisodeBindingModel_).let {
                model.episode()?.releaseDateTime?.let { releaseDate ->
                    val instant = releaseDate.toInstant(ZoneOffset.UTC)
                    val dateSectionTimeStamp = generator.generateDateTime(instant)
                    dateSectionTimeStamp
                }
            }
        }

        // filter section
        val mapSectionsDistinct = mapSections.filterNotNull().distinct()

        // get for each section the first occurent
        // then put into a map
        // and reverse the sort order
        val sectionsIndexMap = mapSectionsDistinct.map { mapSections.indexOf(it) to it }
                .toMap()
                .toSortedMap(reverseOrder())
        // insert sections into the newModels at the right place
        for((key, value) in sectionsIndexMap) {
            val dateSectionString = generator.generate(value).displayText(context)
            models.add(key, EpisodelistSectionDateBindingModel_()
                    .id(dateSectionString)
                    .title(dateSectionString))
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)

        recyclerView.addItemDecoration(
                StickyHeaderItemDecoration(
                        this, EpisodelistSectionDateBindingModel_::class.java))
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)

        recyclerView.clearDecorations()
    }
}
