package com.caldeirasoft.basicapp.presentation.utils.epoxy

import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.DiffUtil
import com.airbnb.epoxy.EpoxyModel
import com.caldeirasoft.basicapp.ItemEpisodePodcastBindingModel_
import com.caldeirasoft.basicapp.presentation.ui.episodeinfo.EpisodeInfoDialogFragment
import com.caldeirasoft.basicapp.presentation.utils.defaultItemDiffCallback
import com.caldeirasoft.basicapp.presentation.utils.extensions.withArgs
import com.caldeirasoft.castly.domain.model.Episode

abstract class EpisodesController(
        val transaction: FragmentTransaction,
        itemDiffCallback: DiffUtil.ItemCallback<Episode> = defaultItemDiffCallback())
    : BasePagedController<Episode>( itemDiffCallback = itemDiffCallback )
{
    override fun addModels(models: List<EpoxyModel<*>>) {
        super.addModels(models)
    }

    override fun buildItemModel(currentPosition: Int, item: Episode?): EpoxyModel<*> {
        item?.let { episode ->
            return ItemEpisodePodcastBindingModel_().apply {
                id(episode.id)
                title(episode.name)
                imageUrl(episode.getArtwork(100))
                duration(episode.description)
                publishedDate(episode.releaseDate.toString())
                playbackState(0)
                timePlayed(0)
                onEpisodeClick { model, parentView, clickedView, position ->
                    val episodeInfoDialog =
                            EpisodeInfoDialogFragment().withArgs(EpisodeInfoDialogFragment.EPISODE_ARG to episode)
                    episodeInfoDialog.show(transaction, episodeInfoDialog.tag)
                }
            }
        } ?: run {
            return ItemEpisodePodcastBindingModel_()
                    .id(currentPosition)
        }
    }
}