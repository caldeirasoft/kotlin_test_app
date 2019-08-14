package com.caldeirasoft.basicapp.presentation.ui.discover

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.view.ViewCompat
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.epoxy.DataBindingEpoxyModel
import com.airbnb.epoxy.paging.PagedListEpoxyController
import com.caldeirasoft.basicapp.R
import com.caldeirasoft.basicapp.databinding.FragmentDiscoverBinding
import com.caldeirasoft.basicapp.presentation.ui.base.BindingFragment
import com.caldeirasoft.basicapp.presentation.ui.catalog.CatalogEpoxyController
import com.caldeirasoft.basicapp.presentation.utils.extensions.navigateTo
import com.caldeirasoft.basicapp.presentation.utils.extensions.observeK
import com.caldeirasoft.basicapp.presentation.views.SimplePagerAdapter
import com.caldeirasoft.castly.domain.model.MediaID
import com.caldeirasoft.castly.domain.model.Podcast
import com.caldeirasoft.castly.domain.model.SectionState
import com.caldeirasoft.castly.domain.model.itunes.StoreCollection
import org.koin.androidx.viewmodel.ext.android.viewModel

class DiscoverFragment :
        BindingFragment<FragmentDiscoverBinding>() {

    private var showHeader: Boolean = true
    private val mViewModel: DiscoverViewModel by viewModel()
    private val catalogController by lazy { createCatalogEpoxyController() }
    private val discoverController by lazy { createDiscoverEpoxyController() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?): View? {
        mBinding = FragmentDiscoverBinding.inflate(inflater, container, false)
        mBinding.let {
            it.lifecycleOwner = this
            it.viewModel = mViewModel
            return it.root
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initMotionLayout()
        initUi()
        initObservers()
    }

    override fun onResume() {
        super.onResume()
        if (!showHeader)
            mBinding.motionLayoutRoot.progress = 1f
    }


    private fun initUi() {
        // viewpager adapter
        mBinding.viewPager.apply {
            val pageTitles = arrayOf("Discover", "Trending")
            val viewPagerAdapter = SimplePagerAdapter(requireActivity(), this, pageTitles)
            adapter = viewPagerAdapter
            mBinding.tabLayout.setupWithViewPager(this)
        }

        mBinding.catalogRecyclerView.apply {
            setController(catalogController)
            addItemDecoration(DividerItemDecoration(this.context, LinearLayoutManager.VERTICAL))
        }

        mBinding.discoverRecyclerView.apply {
            setController(discoverController)
        }
    }

    private fun initObservers() {
        mViewModel.topItems.observeK(this) {data ->
            catalogController.submitList(data)
        }

        mViewModel.itunesStoreData.observeK(this) { data ->
            discoverController.setData(data)
            data?.trending?.map { artwork -> artwork.artworkUrl }?.let { list ->
                mBinding.bannerViewPager.setImageList(list)
            }
        }

        mViewModel.itunesStoreInitialState.observeK(this) {
            it?.let {
                discoverController.setNetworkState(it)
            }
        }
    }

    private fun initMotionLayout() {
        mBinding.motionLayoutRoot.apply {
            // init statusbar offset
            getConstraintSet(R.id.expanded)?.constrainHeight(R.id.status_bar_view,
                    (mBinding.statusBarView.rootWindowInsets?.stableInsetTop ?: 96))
            getConstraintSet(R.id.collapsed)?.constrainHeight(R.id.status_bar_view,
                    (mBinding.statusBarView.rootWindowInsets?.stableInsetTop ?: 96))

            setTransitionListener(object : MotionLayout.TransitionListener {
                override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) { }
                override fun onTransitionChange(p0: MotionLayout?, p1: Int, p2: Int, p3: Float) { }
                override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) { }
                override fun onTransitionCompleted(p0: MotionLayout?, p1: Int) {
                    showHeader = p1 == R.id.expanded
                }
            })
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
        val id = MediaID(SectionState.PODCAST, podcast.id).id
        val direction =
                DiscoverFragmentDirections.openPodcast(id).also{
                    it.podcast = podcast
                }
        val extras = FragmentNavigatorExtras(imageView to transitionName)
        navigateTo(direction, extras)
    }

    private fun createCatalogEpoxyController(): PagedListEpoxyController<Podcast> =
            CatalogEpoxyController(object: CatalogEpoxyController.Callbacks {
                override fun onPodcastClicked(podcast: Podcast, view: View) {
                    val id = MediaID(SectionState.PODCAST, podcast.id).id
                    val direction =
                            DiscoverFragmentDirections.openPodcast(id).also{
                                it.podcast = podcast
                            }
                    val imageView: ImageView = view.findViewById(R.id.img_row)
                    val extras = FragmentNavigatorExtras(imageView to podcast.transitionName)
                    navigateTo(direction, extras)
                }

                override fun onCollectionClicked(collection: StoreCollection, view: View) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }
            })

    private fun createDiscoverEpoxyController() =
            DiscoverExpoxyController(object: DiscoverExpoxyController.Callbacks {
                override fun onPodcastClicked(podcast: Podcast, view: View) {
                    val id = MediaID(SectionState.PODCAST, podcast.id).id
                    val direction =
                            DiscoverFragmentDirections.openPodcast(id).also{
                                it.podcast = podcast
                            }
                    val imageView: ImageView = view.findViewById(R.id.img_row)
                    val extras = FragmentNavigatorExtras(imageView to podcast.transitionName)
                    navigateTo(direction, extras)

                    //mViewModel.updatePodcast(podcast)
                }

                override fun onCollectionClicked(collection: StoreCollection, view: View) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }
            })
}