package com.caldeirasoft.basicapp.presentation.ui.podcastinfo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.caldeirasoft.basicapp.ItemEpisodePodcastBindingModel_
import com.caldeirasoft.basicapp.R
import com.caldeirasoft.basicapp.databinding.FragmentPodcastinfoBinding
import com.caldeirasoft.basicapp.presentation.ui.base.BindingFragment
import com.caldeirasoft.basicapp.presentation.ui.episodeinfo.EpisodeInfoDialogFragment
import com.caldeirasoft.basicapp.presentation.utils.combineTuple
import com.caldeirasoft.basicapp.presentation.utils.epoxy.EpoxyTouchHelperExt
import com.caldeirasoft.basicapp.presentation.utils.epoxy.SwipeReturnCallbacks
import com.caldeirasoft.basicapp.presentation.utils.extensions.*
import com.caldeirasoft.basicapp.presentation.utils.extensions.color
import com.caldeirasoft.castly.domain.model.entities.Episode
import com.caldeirasoft.castly.domain.model.entities.Podcast
import com.caldeirasoft.castly.domain.repository.PlayerRepository
import kotlinx.coroutines.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

@ExperimentalCoroutinesApi
@FlowPreview
class PodcastInfoFragment :
        BindingFragment<FragmentPodcastinfoBinding>()
{
    // private property
    private var mIsSubscribing:Boolean = false
    private var showHeader:Boolean = true

    // arguments
    private val args by lazy { PodcastInfoFragmentArgs.fromBundle(requireArguments()) }
    val collapsed by lazyArg<Boolean?>(COLLAPSED)

    // viewmodel
    private val model: PodcastInfoViewModel by viewModel {
        parametersOf(args.podcast)
    }

    // player repository //TODO: replace by viewmodel
    val playerRepository: PlayerRepository by inject()

    // epoxy controller
    private val controller: PodcastInfoController by lazy { createEpoxyController() }

    // views
    private lateinit var mArtworkImageView: ImageView

    // on create view : set binding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?): View? {
        mBinding = FragmentPodcastinfoBinding.inflate(inflater, container, false)
        mBinding.let {
            it.setLifecycleOwner(this)

            // set transition item
            mArtworkImageView = it.root.findViewById(R.id.imageview_podcastinfo_thumbnail)
            ViewCompat.setTransitionName(mArtworkImageView, "")

            return it.root
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postponeEnterTransition(500)
        setupSwipeToDelete()
        initMotionLayout()
        // setup insets
        initObservers()
        initUi()
    }

    private fun initObservers() {
        combineTuple(model.podcastEpisodes, model.loadingStatus)
                .observeK(this)
                {
                    controller.setData(it?.first.orEmpty(), it?.second)
                }
        //TODO: scroll on playing episode : controller.addModelBuildListener()
    }

    private fun initUi() {
        mBinding.apply {
            // recycler view
            recyclerViewPodcastinfoEpisodes.apply {
                setController(controller)
                addItemDecoration(DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL))
            }
            //shimmerLayout.alpha = 1f
        }
    }

    private fun initMotionLayout() {
        // init statusbar offset
        mBinding.motionlayoutPodcastinfoRoot.apply {
            setTransitionListener(object : MotionLayout.TransitionListener {
                override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) {}
                override fun onTransitionChange(p0: MotionLayout?, p1: Int, p2: Int, p3: Float) {}
                override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) {}
                override fun onTransitionCompleted(p0: MotionLayout?, p1: Int) {
                    showHeader = p1 == R.id.expanded
                }
            })
        }
    }

    private fun createEpoxyController(): PodcastInfoController =
            PodcastInfoController(model, object : PodcastInfoController.Callbacks {
                override fun onEpisodeOpen(episode: Episode, view: View) {
                    val episodeInfoDialog =
                            EpisodeInfoDialogFragment().withArgs(EpisodeInfoDialogFragment.EPISODE_ARG to episode.id)
                    episodeInfoDialog.show(this@PodcastInfoFragment.childFragmentManager, episodeInfoDialog.tag)
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

                override fun onPodcastSubscribe(podcast: Podcast, view: View) {
                    TODO("Not yet implemented")
                }
            })


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
                mBinding.recyclerViewPodcastinfoEpisodes.let { _ ->
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

        EpoxyTouchHelperExt.initSwiping(mBinding.recyclerViewPodcastinfoEpisodes)
                .let { if (view?.layoutDirection == View.LAYOUT_DIRECTION_RTL) it.right() else it.left() }
                .withTarget(ItemEpisodePodcastBindingModel_::class.java)
                .andCallbacks(swipeCallback)


    }

    companion object {
        const val EXTRA_FEED_ID = "FEED_ID"
        const val COLLAPSED = "COLLAPSED"
    }
}