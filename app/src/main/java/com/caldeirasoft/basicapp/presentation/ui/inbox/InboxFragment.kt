package com.caldeirasoft.basicapp.presentation.ui.inbox

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.caldeirasoft.basicapp.R
import com.caldeirasoft.basicapp.databinding.FragmentEpisodelistBinding
import com.caldeirasoft.basicapp.presentation.ui.base.BindingFragment
import com.caldeirasoft.basicapp.presentation.ui.episodeinfo.EpisodeInfoDialogFragment
import com.caldeirasoft.basicapp.presentation.utils.ThemeHelper
import com.caldeirasoft.basicapp.presentation.utils.epoxy.EpisodesGroupByDateController
import com.caldeirasoft.basicapp.presentation.utils.extensions.observeK
import com.caldeirasoft.basicapp.presentation.utils.extensions.withArgs
import com.caldeirasoft.castly.domain.model.entities.Episode
import com.caldeirasoft.castly.domain.repository.PlayerRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class InboxFragment : BindingFragment<FragmentEpisodelistBinding>() {

    // viewmodel
    val model: InboxViewModel by viewModel()

    // player repository //TODO: replace by viewmodel
    val playerRepository: PlayerRepository by inject()

    // epoxy controller
    private val controller by lazy { createEpoxyController() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?): View? {
        mBinding = FragmentEpisodelistBinding.inflate(inflater, container, false)
        mBinding.let {
            it.lifecycleOwner = this
            it.title = context?.getString(R.string.inbox)
            it.viewModel = model
            return it.root
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
        initObservers()
    }

    private fun initObservers() {
        lifecycleScope.launch {
            model.podcastEpisodes.collect { data ->
                controller.setData(data, null, null)
            }

            model.podcastsWithCount.collect { data ->
                mBinding.podcastsWithCount = data
            }
        }

        model.darkThemeOn.observeK(this) { theme ->
            when (theme) {
                true -> ThemeHelper.applyTheme(ThemeHelper.DARK_MODE)
                else -> ThemeHelper.applyTheme(ThemeHelper.LIGHT_MODE)
            }
        }
    }

    private fun initUi() {
        mBinding.recyclerView.apply {
            setController(controller)
            addItemDecoration(DividerItemDecoration(activity, LinearLayoutManager.VERTICAL))
        }
    }

    private fun createEpoxyController(): EpisodesGroupByDateController =
            EpisodesGroupByDateController(
                    context = requireContext(),
                    callbacks = object : EpisodesGroupByDateController.Callbacks {
                        override fun onEpisodeOpen(episode: Episode, view: View) {
                            val episodeInfoDialog =
                                    EpisodeInfoDialogFragment().withArgs(EpisodeInfoDialogFragment.EPISODE_ARG to episode.id)
                            episodeInfoDialog.show(this@InboxFragment.childFragmentManager, episodeInfoDialog.tag)
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
