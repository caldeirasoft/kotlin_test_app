package com.caldeirasoft.basicapp.ui.history

import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.caldeirasoft.basicapp.R
import com.caldeirasoft.basicapp.service.sync.SyncAdapterManager
import com.caldeirasoft.basicapp.ui.episodes.EpisodesBaseFragment
import com.caldeirasoft.basicapp.viewModelProviders
import kotlinx.android.synthetic.main.fragment_history.*

class HistoryFragment : EpisodesBaseFragment() {

    override val viewModel by lazy { viewModelProviders<HistoryViewModel>() }

    override fun getLayout() = R.layout.fragment_history

    override fun onCreate() {
        setupRecyclerView(rw_history_episodes)
        observeEpisodes()
    }
}
