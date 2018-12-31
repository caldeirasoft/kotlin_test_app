package com.caldeirasoft.basicapp.ui.favorite

import com.caldeirasoft.basicapp.R
import com.caldeirasoft.basicapp.ui.episodes.EpisodesBaseFragment
import com.caldeirasoft.basicapp.ui.extensions.viewModelProviders
import kotlinx.android.synthetic.main.fragment_favorite.*

class FavoriteFragment : EpisodesBaseFragment() {

    override val viewModel by lazy { viewModelProviders<FavoriteViewModel>() }

    override fun getLayout() = R.layout.fragment_favorite

    override fun onCreate() {
        // toolbar
        setupRecyclerView(rw_favorite_episodes)
        observeEpisodes()
    }
}
