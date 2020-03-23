package com.caldeirasoft.basicapp.presentation.utils.epoxy

import android.content.Context
import android.util.Log
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.epoxy.EpoxyController
import com.airbnb.epoxy.EpoxyModel
import com.caldeirasoft.basicapp.CollectionTitleBindingModel_
import com.caldeirasoft.basicapp.ItemEpisodeBindingModel_
import com.caldeirasoft.basicapp.presentation.ui.episodeinfo.EpisodeInfoDialogFragment
import com.caldeirasoft.basicapp.presentation.utils.defaultItemDiffCallback
import com.caldeirasoft.basicapp.presentation.utils.extensions.observeK
import com.caldeirasoft.basicapp.presentation.utils.extensions.withArgs
import com.caldeirasoft.basicapp.util.RelativeTimestampGenerator
import com.caldeirasoft.castly.domain.model.Episode
import com.caldeirasoft.castly.domain.repository.PlayerRepository
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneOffset
import org.threeten.bp.format.DateTimeFormatter
import kotlin.properties.Delegates

class EpisodesGroupByDateController(
        val context: Context,
        val manager: FragmentManager,
        val lifecycleOwner: LifecycleOwner,
        val playerRepository: PlayerRepository,
        itemDiffCallback: DiffUtil.ItemCallback<Episode> = defaultItemDiffCallback())
    : BasePagedController<Episode>( itemDiffCallback = itemDiffCallback ), EpoxyController.Interceptor
{
    var ids: HashSet<String> = hashSetOf()
    // observe currentMediaId with old and new value
    var currentMediaId: String? by Delegates.observable<String?>(null) {
        //Delegates.observable on https://proandroiddev.com/the-magic-in-kotlin-delegates-377d27a7b531
        property, oldValue, newValue ->
            if (!oldValue.isNullOrEmpty() && (ids.contains(oldValue) || ids.contains(newValue)))
                this.requestForcedModelBuild()
    }

    init {
        // add interceptor
        addInterceptor(this)
        requestModelBuild()
        initObservers()
    }

    private fun initObservers() {
        playerRepository.currentMediaId.observeK(lifecycleOwner) {
            currentMediaId = it
        }
        playerRepository.playerState.observeK(lifecycleOwner) {
            if (ids.contains(playerRepository.currentMediaId.value))
                this.requestForcedModelBuild()
        }
        playerRepository.bufferingState.observeK(lifecycleOwner) {
            if (ids.contains(playerRepository.currentMediaId.value))
                this.requestForcedModelBuild()
        }
    }

    override fun addModels(models: List<EpoxyModel<*>>) {
        Log.d("addModels", "")
        super.addModels(models)
        /*
        if (isLoading) {
            loadingItem {
                id("loading_item")
            }
        }
         */
    }

    override fun buildItemModel(currentPosition: Int, item: Episode?): EpoxyModel<*> {
        item?.let {
            ids.add(item.id.toString())

            return ItemEpisodeBindingModel_().apply {
                id(item.id)
                title(item.name)
                description(item.description)
                imageUrl(item.getArtwork(100))
                duration(item.description)
                publishedDate(it.releaseDate.toString())
                episode(item)
                /*publishedDate(it.releaseDate.toString())
                playbackState(0)
                timePlayed(0)
                roundedTopCorners(false)
                roundedBottomCorners(false)*/
                isPlaying((playerRepository.currentMediaId.value == item.id.toString()))
                playerState(playerRepository.playerState.value)
                bufferingState(playerRepository.bufferingState.value)
                onEpisodeClick { model, parentView, clickedView, position ->
                    val episodeInfoDialog =
                            EpisodeInfoDialogFragment().withArgs(EpisodeInfoDialogFragment.EPISODE_ARG to item.id)
                    episodeInfoDialog.show(manager, episodeInfoDialog.tag)
                }
                onPlayClick { model, parentView, clickedView, position ->
                    playerRepository.playEpisode(item.id.toString())
                }
            }
        } ?: run {
            return ItemEpisodeBindingModel_()
                    .id(currentPosition)
        }
    }

    override fun intercept(models: MutableList<EpoxyModel<*>>) {
        Log.d("intercept", "")
        val sections : MutableList<String> = mutableListOf()
        val generator = RelativeTimestampGenerator()

        // for each model, get section value (date)
        val mapSections : List<LocalDateTime?> = models.map { model ->
            (model as ItemEpisodeBindingModel_)?.let {
                model.publishedDate()?.let {
                    val releaseDate = LocalDateTime.parse(it, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
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
            models.add(key, CollectionTitleBindingModel_()
                    .id(dateSectionString)
                    .collectionTitle(dateSectionString))
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)

        recyclerView.addItemDecoration(StickyHeaderItemDecoration(
                    this, CollectionTitleBindingModel_::class.java))
    }
}
