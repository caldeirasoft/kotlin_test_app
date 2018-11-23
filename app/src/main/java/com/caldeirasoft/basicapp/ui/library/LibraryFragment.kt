package com.caldeirasoft.basicapp.ui.library

import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.caldeirasoft.basicapp.R
import com.caldeirasoft.basicapp.service.sync.SyncAdapterManager
import com.caldeirasoft.basicapp.ui.base.BaseFragment
import com.caldeirasoft.basicapp.ui.episodes.EpisodesBaseFragment
import com.caldeirasoft.basicapp.ui.home.IMainFragment
import com.caldeirasoft.basicapp.viewModelProviders
import kotlinx.android.synthetic.main.fragment_library.*

class LibraryFragment : BaseFragment(), IMainFragment {

    private var menu: Menu? = null

    override fun getLayout() = R.layout.fragment_library

    override fun getMenuItem() = R.id.navigation_podcasts

    override fun onCreate() {
        // toolbar
        setToolbar();
        setupViewPager()
        setHasOptionsMenu(true)
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

    fun setupViewPager() {
        val adapter = LibraryFragmentPagerAdapter(this.requireContext(), childFragmentManager)
        vp_library.adapter = adapter
        tb_library.setupWithViewPager(vp_library)
    }

    companion object {
        const val EXTRA_FEED_ID = "FEED_ID"
    }
}
