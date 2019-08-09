package com.caldeirasoft.basicapp.presentation.ui.episodelist

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.epoxy.EpoxyModel
import com.airbnb.epoxy.paging.PagedListEpoxyController
import com.caldeirasoft.basicapp.ItemEpisodeBindingModel_
import com.caldeirasoft.basicapp.databinding.FragmentEpisodelistBinding
import com.caldeirasoft.basicapp.presentation.ui.base.BindingFragment
import com.caldeirasoft.basicapp.presentation.ui.episodeinfo.EpisodeInfoDialogFragment
import com.caldeirasoft.basicapp.presentation.ui.episodeinfo.EpisodeInfoDialogFragment.Companion.EPISODE_ARG
import com.caldeirasoft.basicapp.presentation.utils.epoxy.BasePagedController
import com.caldeirasoft.basicapp.presentation.utils.extensions.observeK
import com.caldeirasoft.basicapp.presentation.utils.extensions.withArgs
import com.caldeirasoft.castly.domain.model.Episode
import kotlinx.android.synthetic.main.fragment_episodelist.*


abstract class EpisodeListFragment : BindingFragment<FragmentEpisodelistBinding>() {

    protected var showHeader: Boolean = true
    protected abstract val mViewModel: EpisodeListViewModel
    private val controller: PagedListEpoxyController<Episode> by lazy { createEpoxyController() }

    override fun onCreate() {
        initUi()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObservers()
    }

    private fun initObservers() {
        val dataItems = mViewModel.dataItems
        mViewModel.dataItems.observeK(this) { data ->
            controller.submitList(data)
        }
    }

    private fun initUi() {
        recyclerView.setController(controller)
        recyclerView.addItemDecoration(DividerItemDecoration(activity, LinearLayoutManager.VERTICAL))
    }

    private fun createEpoxyController(): PagedListEpoxyController<Episode> =
            object : BasePagedController<Episode>() {
                override fun buildItemModel(currentPosition: Int, item: Episode?): EpoxyModel<*> {
                    item?.let {
                        return ItemEpisodeBindingModel_().apply {
                            id(item.id)
                            title(item.name)
                            imageUrl(item.getArtwork(100))
                            duration(item.description)
                            //publishedDate(it.releaseDate.toString())
                            //playbackState(0)
                            //timePlayed(0)
                            onEpisodeClick { model, parentView, clickedView, position ->
                                val episodeInfoDialog =
                                        EpisodeInfoDialogFragment().withArgs(EPISODE_ARG to item)
                                episodeInfoDialog.show(childFragmentManager, episodeInfoDialog.tag)
                            }
                        }
                    } ?: run {
                        return ItemEpisodeBindingModel_()
                                .id(currentPosition)
                    }
                }
            }
}