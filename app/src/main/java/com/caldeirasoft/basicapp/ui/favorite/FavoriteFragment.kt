package com.caldeirasoft.basicapp.ui.favorite

import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.caldeirasoft.basicapp.R
import com.caldeirasoft.basicapp.service.sync.SyncAdapterManager
import com.caldeirasoft.basicapp.ui.episodes.EpisodesBaseFragment
import com.caldeirasoft.basicapp.viewModelProviders
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
