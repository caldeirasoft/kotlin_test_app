package com.caldeirasoft.basicapp.presentation.ui.catalog

import android.os.Bundle
import android.view.*
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.epoxy.TypedEpoxyController
import com.caldeirasoft.basicapp.R
import com.caldeirasoft.basicapp.databinding.FragmentCatalogBinding
import com.caldeirasoft.basicapp.itemPodcast
import com.caldeirasoft.basicapp.presentation.ui.base.BindingFragment
import com.caldeirasoft.basicapp.presentation.ui.podcast.PodcastFragmentDirections
import com.caldeirasoft.basicapp.presentation.utils.extensions.navigateTo
import com.caldeirasoft.basicapp.presentation.utils.extensions.observeK
import com.caldeirasoft.castly.domain.model.Podcast
import com.google.android.material.bottomsheet.BottomSheetBehavior

import kotlinx.android.synthetic.main.fragment_catalog.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class CatalogFragment : BindingFragment<FragmentCatalogBinding>() {

    var category:Int = 26
    private val mViewModel:CatalogViewModel by viewModel { parametersOf(category)}
    private lateinit var mBottomSheetBehavior: BottomSheetBehavior<View>
    private val controller by lazy { createEpoxyController() }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?): View? {
        mBinding = FragmentCatalogBinding.inflate(inflater, container, false)
        mBinding.let {
            it.setLifecycleOwner(this)
            it.viewModel = mViewModel
            return it.root
        }
    }

    override fun onCreate() {
        initObservers()
        initUi()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setTitle()
        setHasOptionsMenu(true)
        setupSwipeRefreshLayout()
    }

    private fun setTitle() {
        val strings = resources.getStringArray(R.array.categories_options)
        val codes = resources.getIntArray(R.array.categories_options_ids)
        val codePosition = codes.indexOf(category)
        if (codePosition >= 0)
        {
            val title = strings[codePosition]
            activity?.title = title;
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun setupSwipeRefreshLayout() =
            with (catalog_swipeRefreshLayout) {
                setOnRefreshListener {
                    mViewModel.refresh()
                    this.isRefreshing = false
                }
            }

    private fun initUi() {
        mBinding.recyclerView.setController(controller)
        mBinding.recyclerView.addItemDecoration(DividerItemDecoration(activity, LinearLayoutManager.VERTICAL))
    }

    private fun initObservers() {
        mViewModel.data.observeK(this) {data ->
            controller.setData(data)
        }
    }

    private fun createEpoxyController(): TypedEpoxyController<List<Podcast>> =
            object : TypedEpoxyController<List<Podcast>>() {
                override fun buildModels(data: List<Podcast>?) {
                    data ?: return
                    data.forEach { podcast ->
                        itemPodcast {
                            id(podcast.feedId)
                            title(podcast.title)
                            imageUrl(podcast.imageUrl)
                            onPodcastClick { _, parentView, clickedView, position ->
                                val transitionName = "iv_podcast$position"
                                val rootView = parentView.dataBinding.root
                                val imageView: ImageView = rootView.findViewById(R.id.img_row)
                                ViewCompat.setTransitionName(imageView, transitionName)

                                val direction =
                                        PodcastFragmentDirections.openPodcast(podcast.feedId.orEmpty(), transitionName)
                                val extras = FragmentNavigatorExtras(
                                        imageView to transitionName)
                                navigateTo(direction, extras)
                            }
                        }
                    }
                }
            }

    private fun updateBottomSheetBehavior(category: Int?) {
        when (category) {
            null -> {
                mBottomSheetBehavior.isHideable = true
                mBottomSheetBehavior.peekHeight = 0
                mBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }
            else -> {
                mBottomSheetBehavior.isHideable = false
                mBottomSheetBehavior.peekHeight = 95
                mBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }
        }
    }

    private fun setupFabClick() {
        with(fab_filtersection_catalog) {
            setOnClickListener { view ->
                mBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
    }


    companion object {
        const val EXTRA_FEED_ID = "FEED_ID"
    }
}