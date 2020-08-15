package com.caldeirasoft.basicapp.presentation.ui.discover

import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.util.TypedValue.COMPLEX_UNIT_DIP
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.MarginPageTransformer
import com.airbnb.epoxy.DataBindingEpoxyModel
import com.airbnb.epoxy.paging.PagedListEpoxyController
import com.caldeirasoft.basicapp.R
import com.caldeirasoft.basicapp.databinding.FragmentDiscoverBinding
import com.caldeirasoft.basicapp.presentation.ui.base.BindingFragment
import com.caldeirasoft.basicapp.presentation.ui.catalog.CatalogEpoxyController
import com.caldeirasoft.basicapp.presentation.utils.combineTuple
import com.caldeirasoft.basicapp.presentation.utils.epoxy.CyclicAdapter
import com.caldeirasoft.basicapp.presentation.utils.extensions.dataBinding
import com.caldeirasoft.basicapp.presentation.utils.extensions.navigateTo
import com.caldeirasoft.basicapp.presentation.utils.extensions.observeK
import com.caldeirasoft.basicapp.presentation.utils.extensions.setController
import com.caldeirasoft.basicapp.presentation.views.SimplePagerAdapter
import com.caldeirasoft.castly.data.models.itunes.GroupingPageDataEntity
import com.caldeirasoft.castly.domain.model.entities.MediaID
import com.caldeirasoft.castly.domain.model.entities.Podcast
import com.caldeirasoft.castly.domain.model.entities.SectionState
import com.caldeirasoft.castly.domain.model.itunes.GroupingPageData
import kotlinx.android.synthetic.main.fragment_podcastdetail.view.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class DiscoverFragment : Fragment(R.layout.fragment_discover) {
    private val binding: FragmentDiscoverBinding by dataBinding()
    private val model: DiscoverViewModel by viewModel()
    private val catalogController: PagedListEpoxyController<Podcast> by lazy { createCatalogEpoxyController() }
    private val discoverController: DiscoverEpoxyController by lazy { createDiscoverEpoxyController() }
    private val discoverHeaderController: DiscoverHeaderEpoxyController by lazy { createDiscoverHeaderEpoxyController() }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = model
        initUi()
        initObservers()
    }


    private fun initUi() {
        binding.viewpager2DiscoverHeader.apply {
            adapter = CyclicAdapter(discoverHeaderController.adapter)
            clipToPadding = false   // allow full width shown with padding
            clipChildren = false    // allow left/right item is not clipped
            offscreenPageLimit = 2  // make sure left/right item is rendered

            // increase this offset to show more of left/right
            val offsetPx = 30.dpToPx(resources.displayMetrics)
            setPadding(offsetPx, 0, offsetPx, 0)

            // increase this offset to increase distance between 2 items
            val pageMarginPx = 10.dpToPx(resources.displayMetrics)
            val marginTransformer = MarginPageTransformer(pageMarginPx)
            setPageTransformer(marginTransformer)
        }

        binding.epoxyRecyclerViewDiscoverTrending.apply {
            setController(catalogController)
            addItemDecoration(DividerItemDecoration(this.context, LinearLayoutManager.VERTICAL))
        }

        binding.epoxyRecyclerViewDiscoverTrending.apply {
            setController(discoverController)
        }
    }

    @FlowPreview
    @ExperimentalCoroutinesApi
    private fun initObservers() {
        // set data
        combineTuple(model.groupingPageData, model.groupingPageLoadingStatus)
                .observeK(this) { content ->
                    discoverController.setData(content?.first, content?.second, null)
                }

        // set header
        combineTuple(model.groupingPageHeaderData, model.groupingPageLoadingStatus)
                .observeK(this) { content ->
                    val collection = content?.first?.items.orEmpty()
                    discoverHeaderController.setData(collection, content?.second)
                }

    }

    private fun navigateToPodcast(podcast: Podcast,
                                  parentView: DataBindingEpoxyModel.DataBindingHolder,
                                  position: Int) {
        val transitionName = "iv_podcast$position"
        val rootView = parentView.dataBinding.root
        val imageView: ImageView = rootView.findViewById(R.id.img_row)
        ViewCompat.setTransitionName(imageView, transitionName)

        navigateToPodcast(podcast, transitionName, imageView)
    }

    private fun navigateToPodcast(podcast: Podcast,
                                  transitionName: String,
                                  imageView: ImageView) {
        val direction = DiscoverFragmentDirections.actionDiscoverFragment2ToNavigationPodcastinfoGraph(podcast)
        val extras = FragmentNavigatorExtras(imageView to transitionName)
        navigateTo(direction, extras)
    }

    private fun createCatalogEpoxyController(): PagedListEpoxyController<Podcast> =
            CatalogEpoxyController(object: CatalogEpoxyController.Callbacks {
                override fun onPodcastClicked(podcast: Podcast, view: View) {
                    val direction = DiscoverFragmentDirections.actionDiscoverFragment2ToNavigationPodcastinfoGraph(podcast)
                    val imageView: ImageView = view.findViewById(R.id.img_row)
                    //TODO: get imageView.transitionName
                    val transitionName = podcast.transitionName + Math.random().toString()
                    Log.d("Transition", transitionName)
                    val extras = FragmentNavigatorExtras(imageView to transitionName)
                    navigateTo(direction, extras)
                }

            })

    private fun createDiscoverEpoxyController() =
            DiscoverEpoxyController(model, object : DiscoverEpoxyController.Callbacks {
                override fun onPodcastClicked(podcast: Podcast, view: View) {
                    val direction = DiscoverFragmentDirections.actionDiscoverFragment2ToNavigationPodcastinfoGraph(podcast)
                    val imageView: ImageView = view.findViewById(R.id.img_row)
                    val transitionName = podcast.transitionName + Math.random().toString()
                    val extras = FragmentNavigatorExtras(imageView to transitionName)
                    navigateTo(direction, extras)
                }

                override fun onCollectionItemClicked(collectionItem: GroupingPageData.TrendingItem, view: View) {
                    when (collectionItem) {
                        is GroupingPageDataEntity.TrendingProviderArtist -> {
                            TODO("navigate to artist")
                        }
                        is GroupingPageDataEntity.TrendingRoom -> {
                            TODO("navigate to room")
                        }
                    }
                }
            })

    private fun createDiscoverHeaderEpoxyController() =
            DiscoverHeaderEpoxyController(model, object : DiscoverHeaderEpoxyController.Callbacks {
                override fun onCollectionItemClicked(collectionItem: GroupingPageData.TrendingItem, view: View) {
                    when (collectionItem) {
                        is GroupingPageDataEntity.TrendingPodcast -> {
                            val podcast = collectionItem.podcast
                            val direction = DiscoverFragmentDirections.actionDiscoverFragment2ToNavigationPodcastinfoGraph(podcast)
                            navigateTo(direction)
                        }
                        is GroupingPageDataEntity.TrendingRoom -> {
                            TODO("navigate to room")
                        }
                    }
                }
            })

    private fun Int.dpToPx(displayMetrics: DisplayMetrics): Int =
            TypedValue.applyDimension(
                    COMPLEX_UNIT_DIP,
                    this.toFloat(),
                    displayMetrics).toInt()
}