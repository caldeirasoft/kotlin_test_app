package com.caldeirasoft.basicapp.presentation.ui.discover

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.view.ViewCompat
import androidx.navigation.fragment.FragmentNavigatorExtras
import com.airbnb.epoxy.DataBindingEpoxyModel
import com.airbnb.epoxy.EpoxyRecyclerView
import com.airbnb.epoxy.TypedEpoxyController
import com.caldeirasoft.basicapp.*
import com.caldeirasoft.basicapp.databinding.FragmentDiscoverBinding
import com.caldeirasoft.basicapp.domain.entity.ItunesSection
import com.caldeirasoft.basicapp.domain.entity.ItunesStore
import com.caldeirasoft.basicapp.domain.entity.Podcast
import com.caldeirasoft.basicapp.presentation.ui.base.BindingFragment
import com.caldeirasoft.basicapp.presentation.utils.extensions.*
import kotlinx.android.synthetic.main.fragment_discover.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class DiscoverFragment :
        BindingFragment<FragmentDiscoverBinding>() {

    private var showHeader: Boolean = true
    private val mViewModel: DiscoverViewModel by viewModel()
    private val controller by lazy { createEpoxyController() }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?): View? {
        mBinding = FragmentDiscoverBinding.inflate(inflater, container, false)
        mBinding.let {
            it.setLifecycleOwner(this)
            it.viewModel = mViewModel
            return it.root
        }
    }

    override fun onCreate() {
        initObservers()
        initUi()
        mViewModel.request()
    }

    private fun initObservers() {
        mViewModel.itunesStore.observeK(this) { data ->
            controller.setData(data)
        }
    }

    private fun initUi() {
        mBinding.recyclerView.setController(controller)
    }

    private fun createEpoxyController() =
            object : TypedEpoxyController<ItunesStore>() {
                override fun buildModels(store: ItunesStore?) {
                    store ?: return
                    headerTrending {
                        id("trending_header")
                        text("")
                    }
                    carousel {
                        id("trending_content")
                        withModelsFrom(store.trending) {
                            ItemPodcastTrendingBindingModel_()
                                    .id("trending" + it.artworkUrl)
                                    .imageUrl(it.artworkUrl)
                                    .onPodcastClick { model, parentView, clickedView, position ->
                                        navigateToPodcast(it.podcast, parentView, position)
                                    }
                        }
                    }

                    store.sections.forEach { section ->
                        headerDiscoverSection {
                            id(section.name + "_header")
                            text(section.name)
                        }
                        carousel {
                            id(section.name + "_content")
                            withModelsFrom(section.podcasts) {
                                ItemPodcastDiscoverBindingModel_()
                                        .id("section" + it.feedUrl)
                                        .title(it.title)
                                        .imageUrl(it.imageUrl)
                                        .authors(it.authors)
                                        .onPodcastClick { model, parentView, clickedView, position ->
                                            navigateToPodcast(it, parentView, position)
                                        }
                            }
                        }
                    }
                }
            }


    private fun navigateToPodcast(podcast: Podcast,
                                  parentView: DataBindingEpoxyModel.DataBindingHolder,
                                  position: Int) {
        val transitionName = "iv_podcast$position"
        val rootView = parentView.dataBinding.root
        val imageView: ImageView = rootView.findViewById(R.id.img_row)
        ViewCompat.setTransitionName(imageView, transitionName)

        val direction =
                DiscoverFragmentDirections.goToPodcast(podcast, transitionName)
        val extras = FragmentNavigatorExtras(imageView to transitionName)
        navigateTo(direction)
    }
}