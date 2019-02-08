package com.caldeirasoft.basicapp.presentation.ui.podcastinfo

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.ViewCompat
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.epoxy.EpoxyModel
import com.airbnb.epoxy.paging.PagedListEpoxyController
import com.caldeirasoft.basicapp.ItemEpisodePodcastBindingModel_
import com.caldeirasoft.basicapp.R
import com.caldeirasoft.basicapp.databinding.FragmentPodcastdetailBinding
import com.caldeirasoft.basicapp.domain.entity.Episode
import com.caldeirasoft.basicapp.presentation.bindingadapter.isVisible
import com.caldeirasoft.basicapp.presentation.ui.base.BindingFragment
import com.caldeirasoft.basicapp.presentation.ui.episodeinfo.EpisodeInfoDialogFragment
import com.caldeirasoft.basicapp.presentation.utils.epoxy.BasePagedController
import com.caldeirasoft.basicapp.presentation.utils.epoxy.EpoxyTouchHelperExt
import com.caldeirasoft.basicapp.presentation.utils.epoxy.SwipeReturnCallbacks
import com.caldeirasoft.basicapp.presentation.utils.extensions.*
import com.marozzi.roundbutton.RoundButton
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class PodcastInfoFragment :
        BindingFragment<FragmentPodcastdetailBinding>()
{
    // private property
    private var mIsSubscribing:Boolean = false
    private var showHeader:Boolean = true

    // arguments
    private val args by lazy { PodcastInfoFragmentArgs.fromBundle(arguments!!) }
    val collapsed by lazyArg<Boolean?>(COLLAPSED)

    // viewmodel
    private val mViewModel:PodcastInfoViewModel by viewModel { parametersOf(args.podcast) }
    private val controller by lazy { createEpoxyController() }

    // views
    private val mButtonSubscribe: RoundButton by bindView(R.id.button_subscribe)
    private val mImageView: ImageView by bindView(R.id.imageview_artwork)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?): View? {
        mBinding = FragmentPodcastdetailBinding.inflate(inflater, container, false)
        mBinding.let {
            it.setLifecycleOwner(this)
            it.viewModel = mViewModel

            // set transition item
            val imageView: ImageView = it.root.findViewById(R.id.imageview_artwork)
            ViewCompat.setTransitionName(imageView, args.transitionName)

            return it.root
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postponeEnterTransition(500)
        setupSwipeToDelete()
        initObservers()
        initUi()
    }


    private fun initObservers() {
        // set loading
        mViewModel.episodes.observeK(this) {
            controller.submitList(it)
        }
        mViewModel.updateEpisodeEvent.observeK(this) {
            //controller.
        }

        mViewModel.isLoading.observeK(this) {
            mBinding.shimmerLayout.isVisible = (it ?: true)
        }

        // set button subscribe
        mViewModel.isInDatabase.observeK(this) {
            if (!mIsSubscribing)
                setButtonSubscribeText()
        }

        // set button subscribe animation
        mViewModel.isSubscribing.observeK(this) {
            mButtonSubscribe.apply {
                if (it == true) {
                    startAnimation()
                    mIsSubscribing = true;
                }
                else {
                    Handler().run {
                        setResultState(RoundButton.ResultState.SUCCESS)
                        postDelayed({
                            setButtonSubscribeText()
                        }, 2500)
                    }
                }
            }
        }
    }

    private fun initUi() {
        mBinding.apply {
            // recycler view
            recyclerView.apply {
                setController(controller)
                addItemDecoration(DividerItemDecoration(activity, LinearLayoutManager.VERTICAL))
            }
            shimmerLayout.alpha = 1f
        }
    }


    private fun setButtonSubscribeText() {
        mIsSubscribing = false
        mButtonSubscribe.apply {
            val textButton =
                    if (mViewModel.isInDatabase.value == true) context.getString(R.string.menu_unsubscribe)
                    else context.getString(R.string.menu_subscribe)

            this.text = textButton
            RoundButton.newBuilder()
                    .withText(textButton)
                    .let {
                        this.setCustomizations(it)
                        this.revertAnimation()
                    }
        }
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
                        return ItemEpisodePodcastBindingModel_()
                                .id(item.episodeId)
                                .title(item.title)
                                .duration(item.durationFormat())
                                .imageUrl(item.imageUrl)
                                .publishedDate(item.publishedFormat())
                                .onEpisodeClick { model, parentView, clickedView, position ->

                                    val episodeInfoDialog =
                                            EpisodeInfoDialogFragment()
                                                    .withArgs("episode" to item)
                                    episodeInfoDialog.show(childFragmentManager, episodeInfoDialog.tag)
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
                mBinding.recyclerView.let { recyclerView ->
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

        EpoxyTouchHelperExt.initSwiping(mBinding.recyclerView)
                .let { if (view?.layoutDirection == View.LAYOUT_DIRECTION_RTL) it.right() else it.left() }
                .withTarget(ItemEpisodePodcastBindingModel_::class.java)
                .andCallbacks(swipeCallback)


    }

    companion object {
        const val EXTRA_FEED_ID = "FEED_ID"
        const val COLLAPSED = "COLLAPSED"
    }
}