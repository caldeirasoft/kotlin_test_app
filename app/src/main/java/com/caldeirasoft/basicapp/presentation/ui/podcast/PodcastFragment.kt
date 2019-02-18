package com.caldeirasoft.basicapp.presentation.ui.podcast

import android.support.v4.media.MediaBrowserCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.ViewCompat
import androidx.media2.MediaItem
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.epoxy.EpoxyRecyclerView
import com.airbnb.epoxy.TypedEpoxyController
import com.caldeirasoft.basicapp.R
import com.caldeirasoft.basicapp.databinding.FragmentPodcastsBinding
import com.caldeirasoft.castly.domain.model.Podcast
import com.caldeirasoft.basicapp.itemPodcast
import com.caldeirasoft.basicapp.presentation.ui.base.BindingFragment
import com.caldeirasoft.basicapp.presentation.ui.base.annotation.FragmentLayout
import com.caldeirasoft.basicapp.presentation.utils.extensions.bindView
import com.caldeirasoft.basicapp.presentation.utils.extensions.navigateTo
import com.caldeirasoft.basicapp.presentation.utils.extensions.observeK
import com.caldeirasoft.castly.service.playback.extensions.displayDescription
import com.caldeirasoft.castly.service.playback.extensions.id
import com.caldeirasoft.castly.service.playback.extensions.title
import com.caldeirasoft.castly.service.playback.extensions.toPodcast
import org.koin.androidx.viewmodel.ext.android.viewModel

@FragmentLayout(layoutId = R.layout.fragment_podcasts)
class PodcastFragment : BindingFragment<FragmentPodcastsBinding>() {

    private var showHeader:Boolean = true

    // view model
    private val viewModel: PodcastViewModel by viewModel()
    private val controller by lazy { createEpoxyController() }

    // views
    private val mRecyclerView: EpoxyRecyclerView by bindView(R.id.recyclerView)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?): View? {
        mBinding = FragmentPodcastsBinding.inflate(inflater, container, false)
        mBinding.let {
            it.setLifecycleOwner(this)
            return it.root
        }
    }

    override fun onCreate() {
        initObservers()
        initUi()
    }

    private fun initUi() {
        mBinding.recyclerView.setController(controller)
        mBinding.recyclerView.addItemDecoration(DividerItemDecoration(activity, LinearLayoutManager.VERTICAL))
    }

    private fun initObservers() {
        viewModel.mediaItems.observeK(this) {data ->
            controller.setData(data)
        }
    }

    private fun createEpoxyController(): TypedEpoxyController<List<MediaItem>> =
            object : TypedEpoxyController<List<MediaItem>>() {
                override fun buildModels(data: List<MediaItem>?) {
                    data ?: return
                    data.forEach { content ->
                        itemPodcast {
                            id(content.metadata?.id)
                            title(content.metadata?.title.toString())
                            imageUrl(content.metadata?.displayDescription.toString())
                            onPodcastClick { model, parentView, clickedView, position ->
                                val transitionName = "iv_podcast$position"
                                val rootView = parentView.dataBinding.root
                                val imageView: ImageView = rootView.findViewById(R.id.img_row)
                                ViewCompat.setTransitionName(imageView, transitionName)

                                val direction =
                                        PodcastFragmentDirections.openPodcast(content.metadata?.mediaId, transitionName)
                                val extras = FragmentNavigatorExtras(
                                        imageView to transitionName)
                                navigateTo(direction, extras)
                            }
                        }
                    }
                }
            }

    /*
    private fun changeLayout()
    {
        menu?.let {
            when (UserPref.podcastLayout) {
                EnumPodcastLayout.LIST.value -> // set grid icon
                    UserPref.podcastLayout = EnumPodcastLayout.GRID.value
                EnumPodcastLayout.GRID.value -> // set small grid icon
                    UserPref.podcastLayout = EnumPodcastLayout.GRID_SMALL.value
                EnumPodcastLayout.GRID_SMALL.value -> //set list icon
                    UserPref.podcastLayout = EnumPodcastLayout.LIST.value
            }

            setupRecyclerView()
            activity?.invalidateOptionsMenu()
        }
    }
    */
}