package com.caldeirasoft.basicapp.presentation.ui.discover

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.ViewCompat
import androidx.navigation.fragment.FragmentNavigatorExtras
import com.airbnb.epoxy.DataBindingEpoxyModel
import com.airbnb.epoxy.TypedEpoxyController
import com.alfianyusufabdullah.init
import com.caldeirasoft.basicapp.*
import com.caldeirasoft.basicapp.databinding.FragmentDiscoverBinding
import com.caldeirasoft.castly.domain.model.itunes.ItunesStore
import com.caldeirasoft.castly.domain.model.Podcast
import com.caldeirasoft.basicapp.presentation.ui.base.BindingFragment
import com.caldeirasoft.basicapp.presentation.ui.base.MediaItemViewModel
import com.caldeirasoft.basicapp.presentation.ui.catalog.CatalogFragment
import com.caldeirasoft.basicapp.presentation.utils.extensions.*
import com.caldeirasoft.castly.domain.model.MediaID
import com.caldeirasoft.castly.domain.model.SectionState
import org.koin.androidx.viewmodel.ext.android.viewModel

class DiscoverFragment :
        BindingFragment<FragmentDiscoverBinding>() {

    private var showHeader: Boolean = true
    private val mViewModel: DiscoverViewModel by viewModel()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?): View? {
        mBinding = FragmentDiscoverBinding.inflate(inflater, container, false)
        mBinding.let {
            it.setLifecycleOwner(this)
            it.viewModel = mViewModel
            return it.root
        }
    }

    override fun onCreate() {
        initUi()
    }

    private fun initUi() {
        mBinding.mainToolbar.title = "SPager"
        mBinding.mainPage.apply {
            init(mBinding.mainTabLayout) {
                addPages("Catalog", CatalogFragment())
                addPages("Trending", DiscoverTrendingFragment())
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

        navigateToPodcast(podcast, transitionName, imageView)
    }

    private fun navigateToPodcast(podcast: Podcast,
                                  transitionName: String,
                                  imageView: ImageView) {
        val id = MediaID(SectionState.PODCAST, podcast.feedUrl).asString()
        val direction =
                DiscoverFragmentDirections.openPodcast(id, transitionName).also{
                    it.podcast = podcast
                }
        val extras = FragmentNavigatorExtras(imageView to transitionName)
        navigateTo(direction, extras)
    }
}