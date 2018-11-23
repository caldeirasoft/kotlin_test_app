package com.caldeirasoft.basicapp.ui.library

import android.content.Context
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.caldeirasoft.basicapp.R
import com.caldeirasoft.basicapp.service.sync.SyncAdapterManager
import com.caldeirasoft.basicapp.ui.base.BaseFragment
import com.caldeirasoft.basicapp.ui.episodes.EpisodesBaseFragment
import com.caldeirasoft.basicapp.ui.favorite.FavoriteFragment
import com.caldeirasoft.basicapp.ui.history.HistoryFragment
import com.caldeirasoft.basicapp.ui.home.IMainFragment
import com.caldeirasoft.basicapp.ui.podcast.PodcastFragment
import com.caldeirasoft.basicapp.viewModelProviders
import kotlinx.android.synthetic.main.fragment_inbox.*

class LibraryFragmentPagerAdapter(val context:Context, fm:FragmentManager) : FragmentStatePagerAdapter(fm) {

    override fun getItem(position: Int): Fragment =
        when (position) {
            0 -> PodcastFragment()
            1 -> FavoriteFragment()
            2 -> HistoryFragment()
            else -> PodcastFragment()
        }

    override fun getCount(): Int = 3

    override fun getPageTitle(position: Int): CharSequence? =
        when (position) {
            1 -> context.resources.getString(R.string.starred)
            2 -> context.resources.getString(R.string.history)
            else -> context.resources.getString(R.string.podcasts)
        }
}
