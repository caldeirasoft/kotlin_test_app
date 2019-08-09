package com.caldeirasoft.basicapp.presentation.ui.queue

import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat.MediaItem
import android.support.v4.media.session.MediaSessionCompat.QueueItem
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.epoxy.EpoxyRecyclerView
import com.airbnb.epoxy.TypedEpoxyController
import com.caldeirasoft.basicapp.R
import com.caldeirasoft.basicapp.databinding.FragmentQueueBinding
import com.caldeirasoft.basicapp.itemEpisode
import com.caldeirasoft.basicapp.presentation.ui.base.BindingFragment
import com.caldeirasoft.basicapp.presentation.ui.episodeinfo.EpisodeInfoDialogFragment
import com.caldeirasoft.basicapp.presentation.ui.episodeinfo.EpisodeInfoDialogFragment.Companion.EPISODE_ARG
import com.caldeirasoft.basicapp.presentation.utils.extensions.observeK
import com.caldeirasoft.basicapp.presentation.utils.extensions.withArgs
import com.caldeirasoft.castly.service.playback.extensions.albumArtUri
import com.caldeirasoft.castly.service.playback.extensions.duration
import org.koin.androidx.viewmodel.ext.android.viewModel

class QueueFragment : BindingFragment<FragmentQueueBinding>() {

    val mViewModel: QueueViewModel by viewModel()
    private val controller by lazy { createEpoxyController() }
    private lateinit var recyclerView: EpoxyRecyclerView

    override fun onCreate() {
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?): View? {
        lifecycle.addObserver(mViewModel)
        mBinding = FragmentQueueBinding.inflate(inflater, container, false)
        mBinding.let {
            it.lifecycleOwner = this
            it.title = context?.getString(R.string.queue)
            recyclerView = it.root.findViewById<EpoxyRecyclerView>(R.id.recyclerView)

            return it.root
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        lifecycle.removeObserver(mViewModel)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObservers()
        initUi()
    }

    private fun initObservers() {
        mViewModel.dataItems.observeK(this) { data ->
            controller.setData(data)
        }
    }

    private fun initUi() {
        recyclerView.setController(controller)
        recyclerView.addItemDecoration(DividerItemDecoration(activity, LinearLayoutManager.VERTICAL))
    }

    private fun createEpoxyController(): TypedEpoxyController<List<QueueItem>> =
            object : TypedEpoxyController<List<QueueItem>>() {
                override fun buildModels(data: List<QueueItem>?) {
                    data ?: return
                    data.forEach { queueItem ->
                        itemEpisode {
                            id(queueItem.queueId)
                            title(queueItem.description.title.toString())
                            imageUrl(queueItem.description.albumArtUri.toString())
                            duration(queueItem.description.duration.toString())
                            onEpisodeClick { model, parentView, clickedView, position ->

                                val mediaItem = MediaItem(queueItem.description, MediaItem.FLAG_PLAYABLE)
                                val episodeInfoDialog =
                                        EpisodeInfoDialogFragment()
                                                .withArgs(EPISODE_ARG to mediaItem)
                                episodeInfoDialog.show(childFragmentManager, episodeInfoDialog.tag)
                            }
                        }
                    }
                }
            }
}