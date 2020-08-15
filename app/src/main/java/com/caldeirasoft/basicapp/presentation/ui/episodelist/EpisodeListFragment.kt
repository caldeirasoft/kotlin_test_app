package com.caldeirasoft.basicapp.presentation.ui.episodelist

import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.epoxy.EpoxyModel
import com.airbnb.epoxy.Typed2EpoxyController
import com.airbnb.epoxy.TypedEpoxyController
import com.airbnb.epoxy.paging.PagedListEpoxyController
import com.caldeirasoft.basicapp.ItemEpisodeBindingModel_
import com.caldeirasoft.basicapp.databinding.FragmentEpisodelistBinding
import com.caldeirasoft.basicapp.itemEpisode
import com.caldeirasoft.basicapp.presentation.ui.base.BindingFragment
import com.caldeirasoft.basicapp.presentation.ui.episodeinfo.EpisodeInfoDialogFragment
import com.caldeirasoft.basicapp.presentation.ui.episodeinfo.EpisodeInfoDialogFragment.Companion.EPISODE_ARG
import com.caldeirasoft.basicapp.presentation.utils.epoxy.BasePagedController
import com.caldeirasoft.basicapp.presentation.utils.epoxy.EpisodesGroupByDateController
import com.caldeirasoft.basicapp.presentation.utils.extensions.observeK
import com.caldeirasoft.basicapp.presentation.utils.extensions.withArgs
import com.caldeirasoft.castly.domain.model.entities.Episode
import kotlinx.android.synthetic.main.fragment_episodelist.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


abstract class EpisodeListFragment : BindingFragment<FragmentEpisodelistBinding>() {

    protected var showHeader: Boolean = true
    protected abstract val model: EpisodeListViewModel
    private val controller by lazy { createEpoxyController() }


    override fun onCreate() {
        initUi()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObservers()
    }

    private fun initObservers() {
        lifecycleScope.launch {
            model.podcastEpisodes.collect { data ->
                controller.setData(data, null, null)
            }
        }
    }

    private fun initUi() {
        recyclerView.setController(controller)
        recyclerView.addItemDecoration(DividerItemDecoration(activity, LinearLayoutManager.VERTICAL))
    }

    private fun createEpoxyController(): EpisodesGroupByDateController =
            EpisodesGroupByDateController(
                    context = requireContext(),
                    callbacks = object : EpisodesGroupByDateController.Callbacks {
                        override fun onEpisodeOpen(episode: Episode, view: View) {
                            val episodeInfoDialog =
                                    EpisodeInfoDialogFragment().withArgs(EPISODE_ARG to episode.id)
                            episodeInfoDialog.show(this@EpisodeListFragment.childFragmentManager, episodeInfoDialog.tag)
                        }

                        override fun onEpisodePlay(episode: Episode, view: View) {
                            TODO("Not yet implemented")
                        }

                        override fun onEpisodeQueueLast(episode: Episode, view: View) {
                            TODO("Not yet implemented")
                        }

                        override fun onEpisodeQueueNext(episode: Episode, view: View) {
                            TODO("Not yet implemented")
                        }

                        override fun onEpisodeArchive(episode: Episode, view: View) {
                            TODO("Not yet implemented")
                        }
                    })
}