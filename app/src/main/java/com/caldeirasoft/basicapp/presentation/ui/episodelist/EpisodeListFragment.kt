package com.caldeirasoft.basicapp.presentation.ui.episodelist

import android.support.v4.media.MediaBrowserCompat.MediaItem
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.epoxy.EpoxyModel
import com.airbnb.epoxy.TypedEpoxyController
import com.airbnb.epoxy.paging.PagedListEpoxyController
import com.caldeirasoft.basicapp.ItemEpisodeBindingModel_
import com.caldeirasoft.basicapp.databinding.FragmentEpisodelistBinding
import com.caldeirasoft.basicapp.itemEpisode
import com.caldeirasoft.castly.domain.model.Episode
import com.caldeirasoft.basicapp.presentation.ui.base.BindingFragment
import com.caldeirasoft.basicapp.presentation.ui.episodeinfo.EpisodeInfoDialogFragment
import com.caldeirasoft.basicapp.presentation.ui.episodeinfo.EpisodeInfoDialogFragment.Companion.EPISODE_ARG
import com.caldeirasoft.basicapp.presentation.utils.epoxy.BasePagedController
import com.caldeirasoft.basicapp.presentation.utils.extensions.observeK
import com.caldeirasoft.basicapp.presentation.utils.extensions.withArgs
import com.caldeirasoft.castly.service.playback.const.Constants.Companion.EXTRA_DURATION
import com.caldeirasoft.castly.service.playback.extensions.*
import kotlinx.android.synthetic.main.fragment_episodelist.*


abstract class EpisodeListFragment : BindingFragment<FragmentEpisodelistBinding>() {

    protected var showHeader: Boolean = true
    protected abstract val mViewModel: EpisodeListViewModel
    private val controller by lazy { createEpoxyController() }

    override fun onCreate() {
        initObservers()
        initUi()
    }

    private fun initObservers() {
        mViewModel.mediaItems.observeK(this) { data ->
            controller.setData(data)
        }
    }

    private fun initUi() {
        recyclerView.setController(controller)
        recyclerView.addItemDecoration(DividerItemDecoration(activity, LinearLayoutManager.VERTICAL))
    }

    private fun createEpoxyController(): TypedEpoxyController<List<MediaItem>> =
            object : TypedEpoxyController<List<MediaItem>>() {
                override fun buildModels(data: List<MediaItem>?) {
                    data ?: return
                    data.forEach { item ->
                        itemEpisode {
                            id(item.mediaId)
                            title(item.description.metadata.title)
                            imageUrl(item.description.metadata.albumArtUri.toString())
                            duration(item.description.metadata.duration.toString())
                            onEpisodeClick { model, parentView, clickedView, position ->

                                val episodeInfoDialog =
                                        EpisodeInfoDialogFragment()
                                                .withArgs(EPISODE_ARG to item)
                                episodeInfoDialog.show(childFragmentManager, episodeInfoDialog.tag)
                            }
                        }
                    }
                }
            }
}