package com.caldeirasoft.basicapp.presentation.ui.podcastdetail

import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat.MediaItem
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.epoxy.EpoxyModel
import com.airbnb.epoxy.paging.PagedListEpoxyController
import com.caldeirasoft.basicapp.ItemEpisodePodcastBindingModel_
import com.caldeirasoft.basicapp.R
import com.caldeirasoft.basicapp.databinding.FragmentPodcastdetailBinding
import com.caldeirasoft.basicapp.presentation.ui.base.BindingFragment
import com.caldeirasoft.basicapp.presentation.ui.episodeinfo.EpisodeInfoDialogFragment
import com.caldeirasoft.basicapp.presentation.ui.episodeinfo.EpisodeInfoDialogFragment.Companion.EPISODE_ARG
import com.caldeirasoft.basicapp.presentation.utils.epoxy.BasePagedController
import com.caldeirasoft.basicapp.presentation.utils.epoxy.EpoxyTouchHelperExt
import com.caldeirasoft.basicapp.presentation.utils.epoxy.SwipeReturnCallbacks
import com.caldeirasoft.basicapp.presentation.utils.extensions.*
import com.caldeirasoft.basicapp.presentation.views.SimplePagerAdapter
import com.caldeirasoft.castly.domain.model.Episode
import com.caldeirasoft.castly.domain.model.Genre
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class PodcastDetailFragment :
        BindingFragment<FragmentPodcastdetailBinding>()
{
    // private property
    private var mIsSubscribing:Boolean = false
    private var showHeader:Boolean = true
    private val mediaItemDiffCallback: DiffUtil.ItemCallback<MediaItem> =
            object : DiffUtil.ItemCallback<MediaItem>() {
                override fun areItemsTheSame(oldItem: MediaItem, newItem: MediaItem) =
                    areContentsTheSame(oldItem, newItem)

                override fun areContentsTheSame(oldItem: MediaItem, newItem: MediaItem) =
                    oldItem.mediaId == newItem.mediaId
            }

    // arguments
    private val args by lazy { PodcastDetailFragmentArgs.fromBundle(requireArguments()) }
    val collapsed by lazyArg<Boolean?>(COLLAPSED)

    // viewmodel
    private val mViewModel:PodcastDetailViewModel by viewModel { parametersOf(args.mediaId, args.podcast) }
    private val controller by lazy { createEpoxyController() }

    // views
    private lateinit var mArtworkImageView: ImageView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?): View? {
        mBinding = FragmentPodcastdetailBinding.inflate(inflater, container, false)
        mBinding.let {
            it.setLifecycleOwner(this)
            it.viewModel = mViewModel

            // set transition item
            mArtworkImageView = it.root.findViewById(R.id.imageView_thumbnail)
            ViewCompat.setTransitionName(mArtworkImageView, args.podcast?.transitionName)

            return it.root
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postponeEnterTransition(500)
        setupSwipeToDelete()
        initMotionLayout()
        initObservers()
        initUi()
    }


    private fun initObservers() {
        // podcast media item
        mViewModel.podcastLiveData.observeK(this) {
            it?.let { podcast ->
                mBinding.apply {
                    title = podcast.name
                    artist = podcast.artistName
                    displayDescription = podcast.description
                    albumArtUri = podcast.getArtwork(100)
                    inDatabase = podcast.isSubscribed

                    createChipsCategories(this.chipView, podcast.genres)
                }
            }
        }
        //
        mViewModel.dataItems.observeK(this) {
            controller.submitList(it)
        }

    }

    private fun initUi() {
        mBinding.apply {
            // viewpager adapter
            viewPager.apply {
                val pageTitles = arrayOf("Episodes", "Details")
                val viewPagerAdapter = SimplePagerAdapter(requireActivity(), this, pageTitles)
                adapter = viewPagerAdapter
                tabLayout.setupWithViewPager(this)
            }
            // recycler view
            episodesRecyclerView.apply {
                setController(controller)
                addItemDecoration(DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL))
            }
            //shimmerLayout.alpha = 1f
        }
    }

    private fun initMotionLayout() {
        // init statusbar offset
        mBinding.motionLayoutRoot.apply {
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


    private fun setButtonSubscribeText() {
        mIsSubscribing = false
        val inDb = mViewModel.podcastDb.value?.isSubscribed ?: false
    }

    /*
    override fun onItemClick(item: Episode?, position: Int, viewId: Int) {
        item?.let {
            when (viewId) {
                R.id.button_favorite -> mViewModel.toggleEpisodeFavorite(it)
                R.id.button_archive -> mViewModel.archiveEpisode(it)
                R.id.button_queuelast -> mViewModel.queueEpisodeLast(it)
                R.id.button_queuenext -> mViewModel.queueEpisodeFirst(it)
                R.id.button_play -> mViewModel.playEpisode(it)
                else -> openEpisodeDetail(it)
            }
        }
    }
    */

    private fun createEpoxyController(): PagedListEpoxyController<Episode> =
            object : BasePagedController<Episode>() {
                override fun buildItemModel(currentPosition: Int, item: Episode?): EpoxyModel<*> {
                    item?.let {
                        return ItemEpisodePodcastBindingModel_().apply {
                            id(item.id)
                            title(item.name)
                            imageUrl(item.getArtwork(100))
                            duration(item.description)
                            publishedDate(it.releaseDate.toString())
                            playbackState(0)
                            timePlayed(0)
                            onEpisodeClick { model, parentView, _, position ->
                                val episodeInfoDialog =
                                        EpisodeInfoDialogFragment().withArgs(EPISODE_ARG to item)
                                episodeInfoDialog.show(childFragmentManager, episodeInfoDialog.tag)
                            }
                        }
                    } ?: run {
                        return ItemEpisodePodcastBindingModel_()
                                .id(currentPosition)
                    }
                }
            }

    private fun setupSwipeToDelete() {
        val context = requireContext()

        val swipeCallback = object : SwipeReturnCallbacks<ItemEpisodePodcastBindingModel_>(
                context.getDrawable(R.drawable.ic_archive_24dp)!!,
                context.resources.getDimensionPixelSize(R.dimen.element_margin_large),
                context.color(R.color.colorBackground),
                context.color(R.color.colorAccent)
        ) {
            override fun onSwipeCompleted(
                    model: ItemEpisodePodcastBindingModel_,
                    itemView: View,
                    position: Int,
                    direction: Int
            ) {
                //mViewModel.removeFavoriteAlbum(model.id())
            }

            override fun onSwipeStarted(model: ItemEpisodePodcastBindingModel_, itemView: View, adapterPosition: Int) {
                super.onSwipeStarted(model, itemView, adapterPosition)
                mBinding.episodesRecyclerView.let { _ ->
                    /*
                    removableItemDecoration = null
                    val itemDecorationCount = recyclerView.itemDecorationCount
                    if (itemDecorationCount > 0) {
                        (itemDecorationCount - 1 downTo 0)
                                .filter { recyclerView.getItemDecorationAt(it) !is ItemTouchHelper } //do not remove itemTouchHelper so we can swipe to deleteCompletable
                                .map { i ->
                                    (recyclerView.getItemDecorationAt(i) as ArtistHeadersDecoration).headers.filterKeys { it == adapterPosition - 1 }
                                            .map {
                                                Timber.d(it.toString())
                                                removableItemDecoration = Pair(
                                                        i,
                                                        recyclerView.getItemDecorationAt(i) as ArtistHeadersDecoration
                                                )
                                                recyclerView.removeItemDecorationAt(i)

                                            }
                                }
                    }
                    */
                }
            }


            override fun onSwipeReleased(model: ItemEpisodePodcastBindingModel_, itemView: View) {
                super.onSwipeReleased(model, itemView)
                /*
                removableItemDecoration?.second?.let { itemDecoration ->
                    epoxyRecyclerView?.addItemDecoration(
                            itemDecoration,
                            removableItemDecoration!!.first
                    )
                }
                */
            }


        }

        EpoxyTouchHelperExt.initSwiping(mBinding.episodesRecyclerView)
                .let { if (view?.layoutDirection == View.LAYOUT_DIRECTION_RTL) it.right() else it.left() }
                .withTarget(ItemEpisodePodcastBindingModel_::class.java)
                .andCallbacks(swipeCallback)


    }

    /**
     * @param chipGroup Chip Group for inflate category chip
     * @param categories list of movie category
     */
    private fun createChipsCategories(chipGroup: ChipGroup, categories: List<Genre>) {
        val inflator = LayoutInflater.from(chipGroup.context)

        val categoriesPodcast = categories.map { category ->
            val chip = inflator.inflate(R.layout.chip_category, chipGroup, false) as Chip
            chip.apply {
                text = category.name
                tag = category.id
                setOnCheckedChangeListener { _, _ ->
                    //viewModel.onCategoryFilterChanged(button.text as String, isChecked)
                }
            }
            chip
        }

        // Clear all Chip in Chip Group before append new Chip
        chipGroup.removeAllViews()

        // Append all Category Chip in Chip Group
        for (category in categoriesPodcast) {
            chipGroup.addView(category)
        }

    }

    companion object {
        const val EXTRA_FEED_ID = "FEED_ID"
        const val COLLAPSED = "COLLAPSED"
    }
}