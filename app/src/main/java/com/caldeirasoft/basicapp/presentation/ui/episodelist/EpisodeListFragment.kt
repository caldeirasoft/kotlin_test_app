package com.caldeirasoft.basicapp.presentation.ui.episodelist

import android.widget.ImageView
import androidx.core.view.ViewCompat
import androidx.navigation.NavDirections
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.epoxy.EpoxyModel
import com.airbnb.epoxy.paging.PagedListEpoxyController
import com.caldeirasoft.basicapp.ItemEpisodeBindingModel_
import com.caldeirasoft.basicapp.R
import com.caldeirasoft.basicapp.databinding.FragmentEpisodelistBinding
import com.caldeirasoft.basicapp.domain.entity.Episode
import com.caldeirasoft.basicapp.presentation.ui.base.BindingFragment
import com.caldeirasoft.basicapp.presentation.utils.epoxy.BasePagedController
import com.caldeirasoft.basicapp.presentation.utils.extensions.navigateTo
import com.caldeirasoft.basicapp.presentation.utils.extensions.observeK
import kotlinx.android.synthetic.main.fragment_episodelist.*


abstract class EpisodeListFragment : BindingFragment<FragmentEpisodelistBinding>() {

    protected var showHeader:Boolean = true
    protected abstract val mViewModel:EpisodeListViewModel
    private val controller by lazy { createEpoxyController() }

    override fun onCreate() {
        initObservers()
        initUi()
    }

    private fun initObservers() {
        mViewModel.episodes.observeK(this) { data ->
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
                        return ItemEpisodeBindingModel_()
                                .id(item.episodeId)
                                .title(item.title)
                                .duration(item.durationFormat())
                                .imageUrl(item.imageUrl)
                                .onEpisodeClick { model, parentView, clickedView, position ->
                                    val transitionName = "iv_episode$position"
                                    val rootView = parentView.dataBinding.root
                                    val imageView: ImageView = rootView.findViewById(R.id.imageview_thumbnail)
                                    ViewCompat.setTransitionName(imageView, transitionName)

                                    val direction =
                                            getEpisodeDirection(item, transitionName)
                                    val extras = FragmentNavigatorExtras(
                                            imageView to transitionName)
                                    navigateTo(direction)
                                }
                    } ?: run {
                        return ItemEpisodeBindingModel_()
                                .id(currentPosition)
                    }
                }
            }

    abstract fun getEpisodeDirection(episode: Episode, transitionName:String): NavDirections
}