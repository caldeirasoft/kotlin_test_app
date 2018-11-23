package com.caldeirasoft.basicapp.ui.inbox

import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.caldeirasoft.basicapp.R
import com.caldeirasoft.basicapp.service.sync.SyncAdapterManager
import com.caldeirasoft.basicapp.ui.episodes.EpisodesBaseFragment
import com.caldeirasoft.basicapp.ui.home.IMainFragment
import com.caldeirasoft.basicapp.viewModelProviders
import kotlinx.android.synthetic.main.fragment_inbox.*

class InboxFragment : EpisodesBaseFragment(), IMainFragment {

    override val viewModel by lazy { viewModelProviders<InboxViewModel>() }
    private var menu: Menu? = null

    override fun getLayout() = R.layout.fragment_inbox

    override fun getMenuItem() = R.id.navigation_inbox

    override fun onCreate() {
        // toolbar
        setToolbar();

        setupRecyclerView(rw_inbox_episodes)
        setHasOptionsMenu(true)
        observeEpisodes()
    }

    private fun setToolbar() {
        (activity as AppCompatActivity).setSupportActionBar(toolbar);
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        this.menu = menu
        inflater?.inflate(R.menu.main_menu, menu)
        // change "podcast layout" icon
        super.onDestroyOptionsMenu()
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menu_refresh -> {
                val syncAdapterManager = SyncAdapterManager(activity!!)
                syncAdapterManager.syncImmediately()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        const val EXTRA_FEED_ID = "FEED_ID"
    }
}
