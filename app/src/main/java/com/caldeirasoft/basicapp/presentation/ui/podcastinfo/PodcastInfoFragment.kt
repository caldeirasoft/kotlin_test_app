package com.caldeirasoft.basicapp.presentation.ui.podcastinfo

import android.app.AlertDialog
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.epoxy.EpoxyTouchHelper
import com.caldeirasoft.basicapp.ItemEpisodePodcastBindingModel_
import com.caldeirasoft.basicapp.R
import com.caldeirasoft.basicapp.domain.entity.SubscribeAction
import com.caldeirasoft.basicapp.databinding.FragmentPodcastinfoBinding
import com.caldeirasoft.basicapp.domain.entity.Episode
import com.caldeirasoft.basicapp.domain.entity.Podcast
import com.caldeirasoft.basicapp.presentation.bindingadapter.isVisible
import com.caldeirasoft.basicapp.presentation.extensions.color
import com.caldeirasoft.basicapp.presentation.extensions.drawableToBitmap
import com.caldeirasoft.basicapp.presentation.extensions.lazyArg
import com.caldeirasoft.basicapp.presentation.ui.base.BindingFragment
import com.caldeirasoft.basicapp.presentation.extensions.observeK
import com.caldeirasoft.basicapp.presentation.utils.SwipeAwayCallbacks
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.marozzi.roundbutton.RoundButton
import kotlinx.android.synthetic.main.fragment_podcastinfo.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class PodcastInfoFragment :
        BindingFragment<FragmentPodcastinfoBinding>(),
        PodcastInfoController.Callbacks
{
    private var mIsSubscribing:Boolean = false
    private var showHeader:Boolean = true
    private lateinit var filtersFab: FloatingActionButton

    //var removableItemDecoration: Pair<Int, ArtistHeadersDecoration?>? = null

    val podcast by lazyArg<Podcast>(EXTRA_FEED_ID)
    val collapsed by lazyArg<Boolean?>(COLLAPSED)
    private val mViewModel:PodcastInfoViewModel by viewModel { parametersOf(podcast) }
    private val controller by lazy { PodcastInfoController(this, mViewModel) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?): View? {
        mBinding = FragmentPodcastinfoBinding.inflate(inflater, container, false)
        mBinding.let {
            it.setLifecycleOwner(this)
            it.viewModel = mViewModel
            return it.root
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initMotionLayout()
        setupSwipeToDelete()
        initObservers()
        initUi()
    }

    override fun onResume() {
        super.onResume()
        if (!showHeader)
            motionLayout_root.progress = 1f
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
            mBinding.shimmerLayout.isVisible = (it ?: false)
        }

        // set button subscribe
        mViewModel.isInDatabase.observeK(this) {
            if (!mIsSubscribing)
                setButtonSubscribeText()
        }

        // set button subscribe animation
        mViewModel.isSubscribing.observeK(this) {
            mBinding.buttonSubscribe.apply {
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

    private fun setButtonSubscribeText() {
        mIsSubscribing = false
        mBinding.buttonSubscribe.apply {
            val textButton = if (mViewModel.isInDatabase.value == true)
                context.getString(R.string.menu_unsubscribe)
            else
                context.getString(R.string.menu_subscribe)

            this.text = textButton
            RoundButton.newBuilder()
                    .withText(textButton)
                    .let {
                        this.setCustomizations(it)
                        this.revertAnimation()
                    }
        }
    }

    private fun initMotionLayout() {
        motionLayout_root.setTransitionListener(object : MotionLayout.TransitionListener {
            override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) { }
            override fun onTransitionChange(p0: MotionLayout?, p1: Int, p2: Int, p3: Float) { }
            override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) { }
            override fun onTransitionCompleted(p0: MotionLayout?, p1: Int) {
                showHeader = p1 != R.id.podcastinfo_end
            }
        })
    }

    private fun initUi() {
        mBinding.apply {
            // recycler view
            recyclerView.setController(controller)
            recyclerView.addItemDecoration(DividerItemDecoration(activity, LinearLayoutManager.VERTICAL))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        /*
        mViewModel.updateEpisodeEvent.removeObservers(this)
        mViewModel.episodes.removeObservers(this)
        mViewModel.subscribePodcastEvent.removeObservers(this)
        mViewModel.loadingState.removeObservers(this)
        */
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

    override fun onEpisodeClick(episode: Episode) {
        val bundle = Bundle()
        bundle.putParcelable(EXTRA_FEED_ID, episode)
        findNavController(view!!).navigate(R.id.action_podcastInfoFragment_to_episodeInfoFragment, bundle)
    }

    private fun setupSwipeToDelete() {
        val context = requireContext()

        val swipeCallback = object : SwipeAwayCallbacks<ItemEpisodePodcastBindingModel_>(
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

            override fun onSwipeStarted(model: ItemEpisodePodcastBindingModel_, itemView: View?, adapterPosition: Int) {
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


            override fun onSwipeReleased(model: ItemEpisodePodcastBindingModel_?, itemView: View?) {
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

        EpoxyTouchHelper.initSwiping(mBinding.recyclerView)
                .let { if (view?.layoutDirection == View.LAYOUT_DIRECTION_RTL) it.right() else it.left() }
                .withTarget(ItemEpisodePodcastBindingModel_::class.java)
                .andCallbacks(swipeCallback)


    }

    companion object {
        const val EXTRA_FEED_ID = "FEED_ID"
        const val COLLAPSED = "COLLAPSED"
    }
}