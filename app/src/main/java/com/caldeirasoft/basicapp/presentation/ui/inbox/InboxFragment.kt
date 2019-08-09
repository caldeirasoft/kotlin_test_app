package com.caldeirasoft.basicapp.presentation.ui.inbox

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.epoxy.EpoxyModel
import com.airbnb.epoxy.paging.PagedListEpoxyController
import com.caldeirasoft.basicapp.ItemEpisodeBindingModel_
import com.caldeirasoft.basicapp.R
import com.caldeirasoft.basicapp.databinding.FragmentEpisodelistBinding
import com.caldeirasoft.basicapp.presentation.ui.base.BindingFragment
import com.caldeirasoft.basicapp.presentation.ui.episodeinfo.EpisodeInfoDialogFragment
import com.caldeirasoft.basicapp.presentation.ui.episodeinfo.EpisodeInfoDialogFragment.Companion.EPISODE_ARG
import com.caldeirasoft.basicapp.presentation.utils.epoxy.BasePagedController
import com.caldeirasoft.basicapp.presentation.utils.extensions.observeK
import com.caldeirasoft.basicapp.presentation.utils.extensions.withArgs
import com.caldeirasoft.castly.domain.model.Episode
import org.koin.androidx.viewmodel.ext.android.viewModel

class InboxFragment : BindingFragment<FragmentEpisodelistBinding>() {

    val mViewModel: InboxViewModel by viewModel()
    private val controller: PagedListEpoxyController<Episode> by lazy { createEpoxyController() }

    override fun onCreate() {

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?): View? {
        mBinding = FragmentEpisodelistBinding.inflate(inflater, container, false)
        mBinding.let {
            it.lifecycleOwner = this
            it.title = context?.getString(R.string.inbox)
            return it.root
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
        initObservers()
    }

    private fun initObservers() {
        mViewModel.dataItems.observeK(this) { data ->
            //controller.submitList(data)
        }
    }
    private fun initUi() {
        mBinding.recyclerView.apply {
            setController(controller)
            addItemDecoration(DividerItemDecoration(activity, LinearLayoutManager.VERTICAL))
        }
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
