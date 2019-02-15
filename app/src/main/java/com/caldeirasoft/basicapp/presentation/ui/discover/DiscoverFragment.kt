package com.caldeirasoft.basicapp.presentation.ui.discover

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.ViewCompat
import androidx.media2.MediaItem
import androidx.media2.MediaMetadata
import androidx.navigation.fragment.FragmentNavigatorExtras
import com.airbnb.epoxy.DataBindingEpoxyModel
import com.airbnb.epoxy.TypedEpoxyController
import com.caldeirasoft.basicapp.*
import com.caldeirasoft.basicapp.databinding.FragmentDiscoverBinding
import com.caldeirasoft.castly.domain.model.itunes.ItunesStore
import com.caldeirasoft.castly.domain.model.Podcast
import com.caldeirasoft.basicapp.presentation.ui.base.BindingFragment
import com.caldeirasoft.basicapp.presentation.ui.base.MediaItemViewModel
import com.caldeirasoft.basicapp.presentation.utils.extensions.*
import com.caldeirasoft.castly.service.playback.extensions.mediaMetadata
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
        mBinding.recyclerView.apply {
            setController(controller)
        }
    }

    private fun createEpoxyController() =
            object : TypedEpoxyController<ItunesStore>() {
                override fun buildModels(store: ItunesStore?) {
                    store ?: return
                    headerTrending {
                        id("trending_header")
                    }
                    carousel {
                        id("trending_content")
                        withModelsFrom(store.trending) {
                            ItemPodcastTrendingBindingModel_()
                                    .id("trending" + it.artworkUrl)
                                    .imageUrl(it.artworkUrl)
                                    .onPodcastClick { model, parentView, clickedView, position ->
                                        val transitionName = "iv_podcast$position"
                                        val rootView = parentView.dataBinding.root
                                        val imageView: ImageView = rootView.findViewById(R.id.imageview_background)
                                        ViewCompat.setTransitionName(imageView, transitionName)

                                        val direction =
                                                DiscoverFragmentDirections.openMediaItem(it.podcast.mediaMetadata, transitionName)
                                        val extras = FragmentNavigatorExtras(imageView to transitionName)
                                        navigateTo(direction, extras)
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
                                            navigateToPodcast(it.mediaMetadata, parentView, position)
                                        }
                            }
                        }
                    }
                }
            }


    private fun navigateToPodcast(mediaItem: MediaMetadata,
                                  parentView: DataBindingEpoxyModel.DataBindingHolder,
                                  position: Int) {
        val transitionName = "iv_podcast$position"
        val rootView = parentView.dataBinding.root
        val imageView: ImageView = rootView.findViewById(R.id.img_row)
        ViewCompat.setTransitionName(imageView, transitionName)

        val direction =
                DiscoverFragmentDirections.openMediaItem(mediaItem, transitionName)
        val extras = FragmentNavigatorExtras(imageView to transitionName)
        navigateTo(direction, extras)
    }
}